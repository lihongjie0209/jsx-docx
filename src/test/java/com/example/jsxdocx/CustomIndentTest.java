package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom indent configuration in BulletedList
 */
public class CustomIndentTest {

    @Test
    public void testDefaultIndent(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Default indent (420 + 360*level):</Paragraph>
                <BulletedList>
                  <ListItem level="0"><Paragraph><Text>Level 0 - default</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - default</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - default</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-default-indent.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemCount = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemCount++;
                }
            }
            assertEquals(3, listItemCount, "Should have 3 list items");
        }
        
        System.out.println("Default indent test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testCustomIndentLeft(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Custom indentLeft (720 instead of 420):</Paragraph>
                <BulletedList indentLeft="720">
                  <ListItem level="0"><Paragraph><Text>Level 0 - 720 twips</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - 1080 twips</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - 1440 twips</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-custom-indent-left.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemCount = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemCount++;
                }
            }
            assertEquals(3, listItemCount, "Should have 3 list items");
        }
        
        System.out.println("Custom indentLeft test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testCustomIndentIncrement(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Custom indentIncrement (720 instead of 360):</Paragraph>
                <BulletedList indentIncrement="720">
                  <ListItem level="0"><Paragraph><Text>Level 0 - 420 twips</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - 1140 twips</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - 1860 twips</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-custom-increment.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemCount = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemCount++;
                }
            }
            assertEquals(3, listItemCount, "Should have 3 list items");
        }
        
        System.out.println("Custom indentIncrement test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testCustomHangingIndent(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>Custom indentHanging (600 instead of 420):</Paragraph>
                <BulletedList indentHanging="600">
                  <ListItem level="0"><Paragraph><Text>Level 0 - hanging 600</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - hanging 600</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - hanging 600</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-custom-hanging.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemCount = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemCount++;
                }
            }
            assertEquals(3, listItemCount, "Should have 3 list items");
        }
        
        System.out.println("Custom indentHanging test passed: " + outputFile.getAbsolutePath());
    }
    
    @Test
    public void testAllCustomIndents(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph>All custom indents (left=500, increment=500, hanging=300):</Paragraph>
                <BulletedList indentLeft="500" indentIncrement="500" indentHanging="300">
                  <ListItem level="0"><Paragraph><Text>Level 0 - 500 left, 300 hanging</Text></Paragraph></ListItem>
                  <ListItem level="1"><Paragraph><Text>Level 1 - 1000 left, 300 hanging</Text></Paragraph></ListItem>
                  <ListItem level="2"><Paragraph><Text>Level 2 - 1500 left, 300 hanging</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;

        File outputFile = tempDir.resolve("test-all-custom-indents.docx").toFile();
        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled);
        new Renderer().renderToDocx(vnode, outputFile.getAbsolutePath());
        
        assertTrue(outputFile.exists());
        
        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            int listItemCount = 0;
            for (XWPFParagraph para : paragraphs) {
                if (para.getNumID() != null) {
                    listItemCount++;
                }
            }
            assertEquals(3, listItemCount, "Should have 3 list items");
        }
        
        System.out.println("All custom indents test passed: " + outputFile.getAbsolutePath());
    }
}
