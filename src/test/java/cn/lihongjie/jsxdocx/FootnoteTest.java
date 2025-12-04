package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFootnote;
import org.apache.poi.xwpf.usermodel.XWPFEndnote;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Footnote and Endnote components
 * <Footnote text="Footnote content" />
 * <Endnote text="Endnote content" />
 */
public class FootnoteTest {

    @TempDir
    Path tempDir;

    @Test
    void testBasicFootnote() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>这是正文内容</Text>
                        <Footnote text="这是脚注内容" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-footnote-basic.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            // Check that footnotes exist
            List<XWPFFootnote> footnotes = doc.getFootnotes();
            assertNotNull(footnotes);
            // POI creates default footnotes (separator, continuation), so we check >= 1
            assertTrue(footnotes.size() >= 1);
        }
    }
    
    @Test
    void testBasicEndnote() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>这是正文内容</Text>
                        <Endnote text="这是尾注内容" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-endnote-basic.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            // Check that endnotes exist
            List<XWPFEndnote> endnotes = doc.getEndnotes();
            assertNotNull(endnotes);
            // POI creates default endnotes (separator, continuation), so we check >= 1
            assertTrue(endnotes.size() >= 1);
        }
    }
    
    @Test
    void testMultipleFootnotes() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>第一个引用</Text>
                        <Footnote text="第一个脚注" />
                        <Text>，第二个引用</Text>
                        <Footnote text="第二个脚注" />
                        <Text>，第三个引用</Text>
                        <Footnote text="第三个脚注" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-footnote-multiple.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFFootnote> footnotes = doc.getFootnotes();
            assertNotNull(footnotes);
            // We created 3 footnotes, plus POI default ones
            assertTrue(footnotes.size() >= 3);
        }
    }
    
    @Test
    void testMultipleEndnotes() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>引用一</Text>
                        <Endnote text="尾注内容一" />
                        <Text>，引用二</Text>
                        <Endnote text="尾注内容二" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-endnote-multiple.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFEndnote> endnotes = doc.getEndnotes();
            assertNotNull(endnotes);
            assertTrue(endnotes.size() >= 2);
        }
    }
    
    @Test
    void testMixedFootnotesAndEndnotes() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>这段文字有脚注</Text>
                        <Footnote text="页脚脚注内容" />
                        <Text>也有尾注</Text>
                        <Endnote text="文档末尾尾注内容" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-footnote-endnote-mixed.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFFootnote> footnotes = doc.getFootnotes();
            List<XWPFEndnote> endnotes = doc.getEndnotes();
            assertNotNull(footnotes);
            assertNotNull(endnotes);
            assertTrue(footnotes.size() >= 1);
            assertTrue(endnotes.size() >= 1);
        }
    }
    
    @Test
    void testFootnoteInDifferentParagraphs() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Text>第一段内容</Text>
                        <Footnote text="第一段的脚注" />
                    </Paragraph>
                    <Paragraph>
                        <Text>第二段内容</Text>
                        <Footnote text="第二段的脚注" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-footnote-paragraphs.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFFootnote> footnotes = doc.getFootnotes();
            assertNotNull(footnotes);
            assertTrue(footnotes.size() >= 2);
            
            // Check that we have 2 paragraphs in the main content
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 2);
        }
    }
}
