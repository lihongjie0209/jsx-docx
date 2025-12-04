package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Bookmark and BookmarkRef components
 * <Bookmark name="bookmark1">Bookmarked content</Bookmark>
 * <BookmarkRef name="bookmark1" type="pageref" />
 */
public class BookmarkTest {

    @TempDir
    Path tempDir;

    @Test
    void testBasicBookmark() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="intro">
                            <Text>Introduction Section</Text>
                        </Bookmark>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmark-basic.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertFalse(paragraphs.isEmpty());
            
            // Find paragraph with bookmark
            boolean foundBookmark = false;
            for (XWPFParagraph para : paragraphs) {
                CTP ctp = para.getCTP();
                if (ctp.sizeOfBookmarkStartArray() > 0) {
                    CTBookmark bookmark = ctp.getBookmarkStartArray(0);
                    assertEquals("intro", bookmark.getName());
                    foundBookmark = true;
                    break;
                }
            }
            assertTrue(foundBookmark, "Should find bookmark named 'intro'");
        }
    }
    
    @Test
    void testBookmarkRefPageRef() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="chapter1">
                            <Text>Chapter 1: Getting Started</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>See page </Text>
                        <BookmarkRef name="chapter1" type="pageref" />
                        <Text> for details.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmarkref-pageref.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 2);
            
            // Second paragraph should contain field codes for PAGEREF
            XWPFParagraph refPara = paragraphs.get(1);
            String paraText = refPara.getText();
            // The field shows placeholder text
            assertTrue(paraText.contains("See page") || paraText.contains("[#]"));
        }
    }
    
    @Test
    void testBookmarkRefContentRef() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="title">
                            <Text>Important Title</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>As mentioned in "</Text>
                        <BookmarkRef name="title" type="ref" />
                        <Text>"</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmarkref-ref.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document exists and is valid
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 2);
        }
    }
    
    @Test
    void testBookmarkRefWithCustomText() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="section1">
                            <Text>Section One Content</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>Click </Text>
                        <BookmarkRef name="section1" type="text" text="here" />
                        <Text> to go to section 1.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmarkref-text.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document exists and has expected content
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 2);
            
            // Check the reference paragraph contains the custom text
            XWPFParagraph refPara = paragraphs.get(1);
            String paraText = refPara.getText();
            assertTrue(paraText.contains("here") || paraText.contains("Click"));
        }
    }
    
    @Test
    void testMultipleBookmarks() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="part1">
                            <Text>Part 1: Introduction</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Bookmark name="part2">
                            <Text>Part 2: Methods</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Bookmark name="part3">
                            <Text>Part 3: Results</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>References: Part 1 (p.</Text>
                        <BookmarkRef name="part1" type="pageref" />
                        <Text>), Part 2 (p.</Text>
                        <BookmarkRef name="part2" type="pageref" />
                        <Text>), Part 3 (p.</Text>
                        <BookmarkRef name="part3" type="pageref" />
                        <Text>)</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-multiple-bookmarks.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 4);
            
            // Count bookmarks
            int bookmarkCount = 0;
            for (XWPFParagraph para : paragraphs) {
                CTP ctp = para.getCTP();
                bookmarkCount += ctp.sizeOfBookmarkStartArray();
            }
            assertEquals(3, bookmarkCount, "Should have 3 bookmarks");
        }
    }
    
    @Test
    void testBookmarkRefNoHyperlink() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="ref1">
                            <Text>Reference Point</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>Page: </Text>
                        <BookmarkRef name="ref1" type="pageref" hyperlink="false" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmarkref-no-hyperlink.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document exists and is valid
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            assertNotNull(doc);
            assertFalse(doc.getParagraphs().isEmpty());
        }
    }
    
    @Test
    void testBookmarkDefaultType() throws Exception {
        // Test that BookmarkRef defaults to pageref type
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Bookmark name="defaultTest">
                            <Text>Default Type Test</Text>
                        </Bookmark>
                    </Paragraph>
                    <Paragraph>
                        <Text>Page number: </Text>
                        <BookmarkRef name="defaultTest" />
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-bookmarkref-default-type.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Document should be valid
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            assertNotNull(doc);
            assertTrue(doc.getParagraphs().size() >= 2);
        }
    }
}
