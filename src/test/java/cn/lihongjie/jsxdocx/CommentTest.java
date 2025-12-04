package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Comment component
 * <Comment author="Author" text="Comment content">Annotated text</Comment>
 */
public class CommentTest {

    @TempDir
    Path tempDir;

    @Test
    void testBasicComment() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Comment author="张三" text="这里需要修改">
                            <Text>被批注的文字</Text>
                        </Comment>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-comment.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        // Verify document structure
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            // Check that comments exist
            XWPFComment[] comments = doc.getComments();
            assertNotNull(comments);
            assertTrue(comments.length > 0);
            
            // Check first comment
            XWPFComment comment = comments[0];
            assertNotNull(comment);
            assertEquals("张三", comment.getAuthor());
            assertEquals("这里需要修改", comment.getText());
        }
    }
    
    @Test
    void testCommentWithInitials() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Comment author="John Doe" initials="JD" text="Please review this">
                            <Text>Review this section</Text>
                        </Comment>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-comment-initials.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            XWPFComment[] comments = doc.getComments();
            assertNotNull(comments);
            assertTrue(comments.length > 0);
            
            XWPFComment comment = comments[0];
            assertNotNull(comment);
            assertEquals("John Doe", comment.getAuthor());
            assertEquals("JD", comment.getInitials());
        }
    }
    
    @Test
    void testMultipleComments() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Comment author="Author1" text="First comment">
                            <Text>First annotated text</Text>
                        </Comment>
                        <Text> - normal text - </Text>
                        <Comment author="Author2" text="Second comment">
                            <Text>Second annotated text</Text>
                        </Comment>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-multiple-comments.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            XWPFComment[] comments = doc.getComments();
            assertNotNull(comments);
            assertEquals(2, comments.length);
            
            // First comment
            assertEquals("Author1", comments[0].getAuthor());
            assertEquals("First comment", comments[0].getText());
            
            // Second comment
            assertEquals("Author2", comments[1].getAuthor());
            assertEquals("Second comment", comments[1].getText());
        }
    }
    
    @Test
    void testCommentInDifferentParagraphs() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Comment author="Reviewer" text="Check spelling">
                            <Text>First paragraph with comment</Text>
                        </Comment>
                    </Paragraph>
                    <Paragraph>
                        <Text>Normal paragraph</Text>
                    </Paragraph>
                    <Paragraph>
                        <Comment author="Editor" text="Needs citation">
                            <Text>Third paragraph with comment</Text>
                        </Comment>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-comment-paragraphs.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            XWPFComment[] comments = doc.getComments();
            assertNotNull(comments);
            assertEquals(2, comments.length);
        }
    }
    
    @Test
    void testCommentWithFormattedText() throws Exception {
        String jsx = """
            <Document>
                <Section>
                    <Paragraph>
                        <Comment author="张三" text="加粗文字批注">
                            <Text bold={true}>粗体文字</Text>
                        </Comment>
                    </Paragraph>
                </Section>
            </Document>
            """;
        
        Path output = tempDir.resolve("test-comment-formatted.docx");
        
        Compiler compiler = new Compiler();
        String js = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        VNode vnode = runtime.run(js);
        
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(output))) {
            // Verify comment exists
            XWPFComment[] comments = doc.getComments();
            assertNotNull(comments);
            assertTrue(comments.length > 0);
            
            // Verify paragraph has formatted text
            assertFalse(doc.getParagraphs().isEmpty());
        }
    }
}
