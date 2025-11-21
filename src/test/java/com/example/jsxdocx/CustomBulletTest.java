package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for configurable bullet characters and fonts in BulletedList
 */
public class CustomBulletTest {

    @Test
    public void testDefaultBulletChar(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Default bullets (Wingdings l):</Paragraph>
                <BulletedList>
                  <ListItem><Paragraph><Text>Item 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Item 2</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Item 3</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-default-bullet.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists(), "Output file should exist");
        assertTrue(outputFile.length() > 0, "Output file should not be empty");
        
        // Open and verify list items exist
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 4, "Should have at least 4 paragraphs");
            
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemsFound++;
                }
            }
            assertEquals(3, listItemsFound, "Should have exactly 3 list items");
        }
        
        System.out.println("Default bullet test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testCustomBulletCharWingdings(@TempDir Path tempDir) throws Exception {
        // Note: Using escape sequences for special Wingdings characters
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Wingdings diamond (n):</Paragraph>
                <BulletedList bulletChar="n" bulletFont="Wingdings">
                  <ListItem><Paragraph><Text>Diamond item 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Diamond item 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Wingdings arrow (u + umlaut):</Paragraph>
                <BulletedList bulletChar="\\u00FC" bulletFont="Wingdings">
                  <ListItem><Paragraph><Text>Arrow item 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Arrow item 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-wingdings-bullet.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemsFound++;
                }
            }
            assertEquals(4, listItemsFound, "Should have 4 list items total (2 diamond + 2 arrow)");
        }
        
        System.out.println("Wingdings bullet test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testCustomBulletCharUnicode(@TempDir Path tempDir) throws Exception {
        // Using Unicode characters with Arial/Calibri
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Unicode filled circle (Arial):</Paragraph>
                <BulletedList bulletChar="\\u25CF" bulletFont="Arial">
                  <ListItem><Paragraph><Text>Filled circle 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Filled circle 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Unicode square (Calibri):</Paragraph>
                <BulletedList bulletChar="\\u25A0" bulletFont="Calibri">
                  <ListItem><Paragraph><Text>Square 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Square 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Unicode arrow (Arial):</Paragraph>
                <BulletedList bulletChar="\\u25BA" bulletFont="Arial">
                  <ListItem><Paragraph><Text>Arrow 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Arrow 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-unicode-bullet.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemsFound++;
                }
            }
            assertEquals(6, listItemsFound, "Should have 6 list items total (2+2+2)");
        }
        
        System.out.println("Unicode bullet test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testMixedBulletStyles(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Mixed bullet styles in one document:</Paragraph>
                
                <Paragraph>Default style:</Paragraph>
                <BulletedList>
                  <ListItem><Paragraph><Text>Default 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Default 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Diamond style:</Paragraph>
                <BulletedList bulletChar="n" bulletFont="Wingdings">
                  <ListItem><Paragraph><Text>Diamond 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Diamond 2</Text></Paragraph></ListItem>
                </BulletedList>
                
                <Paragraph>Unicode filled circle:</Paragraph>
                <BulletedList bulletChar="\\u25CF" bulletFont="Arial">
                  <ListItem><Paragraph><Text>Circle 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Circle 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-mixed-bullets.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemsFound = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemsFound++;
                }
            }
            assertEquals(6, listItemsFound, "Should have 6 list items total from 3 different lists");
        }
        
        System.out.println("Mixed bullet styles test passed: " + outputFile.getAbsolutePath());
    }
}
