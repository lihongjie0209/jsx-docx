package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for nested bullet lists with different bullet characters at different levels
 */
public class NestedBulletListTest {

    @Test
    public void testBasicNestedBulletList(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Nested bullet list test:</Paragraph>
                <BulletedList>
                  <ListItem level="0"><Paragraph><Text>Level 0 - Item 1</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - Item 1.1</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - Item 1.2</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - Item 1.2.1</Text></Paragraph></ListItem>
                  <ListItem level="0"><Paragraph><Text>Level 0 - Item 2</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - Item 2.1</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-nested-bullets.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists(), "Output file should exist");
        assertTrue(outputFile.length() > 0, "Output file should not be empty");
        
        // Open and verify list structure
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            System.out.println("\n=== Nested Bullet List Test ===");
            System.out.println("Total paragraphs: " + paragraphs.size());
            
            int listItemsFound = 0;
            for (int i = 0; i < paragraphs.size(); i++) {
                XWPFParagraph para = paragraphs.get(i);
                if (para.getNumID() != null) {
                    String text = para.getText();
                    int level = para.getNumIlvl() != null ? para.getNumIlvl().intValue() : 0;
                    System.out.println("  Paragraph " + i + ": '" + text + "' (level=" + level + ", numId=" + para.getNumID() + ")");
                    listItemsFound++;
                }
            }
            
            assertEquals(6, listItemsFound, "Should have 6 list items at various levels");
            
            // Verify specific level assignments
            int paraIndex = 1; // Skip title paragraph
            assertEquals(0, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 1 should be level 0");
            assertEquals(1, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 1.1 should be level 1");
            assertEquals(1, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 1.2 should be level 1");
            assertEquals(2, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 1.2.1 should be level 2");
            assertEquals(0, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 2 should be level 0");
            assertEquals(1, paragraphs.get(paraIndex++).getNumIlvl().intValue(), "Item 2.1 should be level 1");
        }
        
        System.out.println("Nested bullet list test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testNestedBulletWithCustomCharacters(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Nested bullets with custom characters:</Paragraph>
                <BulletedList bulletChar="l" bulletFont="Wingdings">
                  <ListItem level="0"><Paragraph><Text>Bullet level 0</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Bullet level 1</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Bullet level 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Diamond bullets:</Paragraph>
                <BulletedList bulletChar="n" bulletFont="Wingdings">
                  <ListItem level="0"><Paragraph><Text>Diamond level 0</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Diamond level 1</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Diamond level 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Unicode bullets:</Paragraph>
                <BulletedList bulletChar="\\u25CF" bulletFont="Arial">
                  <ListItem level="0"><Paragraph><Text>Unicode level 0</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Unicode level 1</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Unicode level 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-nested-custom-bullets.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            System.out.println("\n=== Nested Custom Bullets Test ===");
            
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemsFound++;
                    int level = para.getNumIlvl() != null ? para.getNumIlvl().intValue() : 0;
                    String text = para.getText();
                    System.out.println("  '" + text + "' (level=" + level + ")");
                }
            }
            
            assertEquals(9, listItemsFound, "Should have 9 list items total (3 lists × 3 levels)");
        }
        
        System.out.println("Nested custom bullets test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testDeepNesting(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Deep nesting test (up to level 8):</Paragraph>
                <BulletedList>
                  <ListItem level="0"><Paragraph><Text>Level 0</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2</Text></Paragraph></ListItem>
                  <ListItem level="3"><Paragraph><Text>Level 3</Text></Paragraph></ListItem>
                  <ListItem level="4"><Paragraph><Text>Level 4</Text></Paragraph></ListItem>
                  <ListItem level="5"><Paragraph><Text>Level 5</Text></Paragraph></ListItem>
                  <ListItem level="6"><Paragraph><Text>Level 6</Text></Paragraph></ListItem>
                  <ListItem level="7"><Paragraph><Text>Level 7</Text></Paragraph></ListItem>
                  <ListItem level="8"><Paragraph><Text>Level 8</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-deep-nesting.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            System.out.println("\n=== Deep Nesting Test ===");
            
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    int level = para.getNumIlvl() != null ? para.getNumIlvl().intValue() : 0;
                    String text = para.getText();
                    System.out.println("  '" + text + "' (level=" + level + ")");
                    
                    // Verify the level matches what we expect
                    assertTrue(text.contains("Level " + level), 
                        "Text '" + text + "' should contain 'Level " + level + "'");
                    
                    listItemsFound++;
                }
            }
            
            assertEquals(9, listItemsFound, "Should have 9 list items (levels 0-8)");
        }
        
        System.out.println("Deep nesting test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testNestedListWithMapArray(@TempDir Path tempDir) throws Exception {
        String jsx = """
            const categories = [
              { name: 'Fruits', items: ['Apple', 'Banana', 'Orange'] },
              { name: 'Vegetables', items: ['Carrot', 'Broccoli'] },
              { name: 'Grains', items: ['Rice', 'Wheat', 'Oats'] }
            ];
            
            <Document>
              <Section pageSize="A4">
                <Paragraph>Nested list with map():</Paragraph>
                <BulletedList>
                  {categories.map(category => [
                    <ListItem level="0" key={category.name}>
                      <Paragraph><Text bold="true">{category.name}</Text></Paragraph>
                    </ListItem>,
                    ...category.items.map(item => (
                      <ListItem level="1" key={item}>
                        <Paragraph><Text>{item}</Text></Paragraph>
                      </ListItem>
                    ))
                  ])}
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-nested-map.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            System.out.println("\n=== Nested List with Map Test ===");
            
            int level0Count = 0;
            int level1Count = 0;
            
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    int level = para.getNumIlvl() != null ? para.getNumIlvl().intValue() : 0;
                    String text = para.getText();
                    System.out.println("  '" + text + "' (level=" + level + ")");
                    
                    if (level == 0) level0Count++;
                    if (level == 1) level1Count++;
                }
            }
            
            assertEquals(3, level0Count, "Should have 3 category items (level 0)");
            assertEquals(8, level1Count, "Should have 8 sub-items (level 1): 3 fruits + 2 vegetables + 3 grains");
        }
        
        System.out.println("Nested list with map test passed: " + outputFile.getAbsolutePath());
    }
}
