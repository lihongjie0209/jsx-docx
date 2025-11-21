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
 * Test for map() array handling in JSX - includes end-to-end DOCX generation and validation
 */
public class MapArrayTest {

    @Test
    public void testMapGeneratedList() {
        String jsx = """
            const items = ['Apple', 'Banana', 'Orange'];
            
            <Document>
              <Section pageSize="A4">
                <Paragraph>水果列表：</Paragraph>
                <BulletedList>
                  {items.map(item => (
                    <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
                  ))}
                </BulletedList>
              </Section>
            </Document>
            """;

        try {
            String compiled = new Compiler().compile(jsx);
            System.out.println("=== Compiled JS ===");
            System.out.println(compiled);
            System.out.println();
            
            VNode vnode = new JsRuntime().run(compiled);
            System.out.println("=== VNode Tree ===");
            printVNode(vnode, 0);
            System.out.println();
            
            // Verify structure
            assertEquals("document", vnode.getType());
            assertEquals(1, vnode.getChildren().size());
            
            VNode section = (VNode) vnode.getChildren().get(0);
            assertEquals("section", section.getType());
            assertEquals(2, section.getChildren().size()); // Paragraph + BulletedList
            
            VNode list = (VNode) section.getChildren().get(1);
            assertEquals("bulletedlist", list.getType());
            System.out.println("BulletedList children count: " + list.getChildren().size());
            
            // Should have 3 list items
            assertEquals(3, list.getChildren().size(), 
                "BulletedList should have 3 ListItem children from map()");
            
            for (int i = 0; i < 3; i++) {
                VNode item = (VNode) list.getChildren().get(i);
                assertEquals("listitem", item.getType());
            }
            
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testMapGeneratedListEndToEnd(@TempDir Path tempDir) throws Exception {
        String jsx = """
            const items = ['Apple', 'Banana', 'Orange'];
            
            <Document>
              <Section pageSize="A4">
                <Paragraph>水果列表：</Paragraph>
                <BulletedList>
                  {items.map(item => (
                    <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
                  ))}
                </BulletedList>
              </Section>
            </Document>
            """;

        System.out.println("\n=== End-to-End DOCX Test ===");
        
        // Compile and render
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        
        File outputFile = tempDir.resolve("test-map-output.docx").toFile();
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists(), "Output DOCX file should exist");
        assertTrue(outputFile.length() > 0, "Output DOCX file should not be empty");
        
        System.out.println("Generated DOCX: " + outputFile.getAbsolutePath());
        System.out.println("File size: " + outputFile.length() + " bytes");
        
        // Open and verify the DOCX content
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            System.out.println("\nTotal paragraphs: " + paragraphs.size());
            
            // Should have: 1 title paragraph + 3 list item paragraphs
            assertTrue(paragraphs.size() >= 4, 
                "Should have at least 4 paragraphs (1 title + 3 list items), found: " + paragraphs.size());
            
            // Verify first paragraph is the title
            XWPFParagraph titlePara = paragraphs.get(0);
            String titleText = titlePara.getText();
            System.out.println("\nParagraph 0: '" + titleText + "' (numId=" + titlePara.getNumID() + ")");
            assertEquals("水果列表：", titleText, "First paragraph should be the title");
            
            // Verify the three list items
            String[] expectedItems = {"Apple", "Banana", "Orange"};
            int listItemsFound = 0;
            
            for (int i = 1; i < paragraphs.size(); i++) {
                XWPFParagraph para = paragraphs.get(i);
                String text = para.getText();
                System.out.println("Paragraph " + i + ": '" + text + "' (numId=" + para.getNumID() + ")");
                
                // Check if this is a list item (has numId)
                if (para.getNumID() != null) {
                    assertTrue(listItemsFound < expectedItems.length, 
                        "Found more list items than expected");
                    
                    String expected = expectedItems[listItemsFound];
                    assertEquals(expected, text.trim(), 
                        "List item " + listItemsFound + " should be '" + expected + "'");
                    
                    listItemsFound++;
                }
            }
            
            System.out.println("\n✓ Found " + listItemsFound + " list items");
            assertEquals(3, listItemsFound, 
                "Should have found exactly 3 list items (Apple, Banana, Orange)");
        }
    }
    
    private void printVNode(Object obj, int indent) {
        String prefix = "  ".repeat(indent);
        
        if (obj instanceof String) {
            System.out.println(prefix + "\"" + obj + "\"");
            return;
        }
        
        VNode node = (VNode) obj;
        System.out.println(prefix + node.getType() + " {");
        
        if (!node.getProps().isEmpty()) {
            System.out.println(prefix + "  props: " + node.getProps());
        }
        
        if (!node.getChildren().isEmpty()) {
            System.out.println(prefix + "  children: [");
            for (Object child : node.getChildren()) {
                printVNode(child, indent + 2);
            }
            System.out.println(prefix + "  ]");
        }
        
        System.out.println(prefix + "}");
    }
}
