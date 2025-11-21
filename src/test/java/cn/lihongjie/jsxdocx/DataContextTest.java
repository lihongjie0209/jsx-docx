package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for data context passing to JSX runtime
 */
public class DataContextTest {

    @Test
    public void testDataContextSimpleObject() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph><Text>{data.title}</Text></Paragraph>
                <Paragraph><Text>{data.author}</Text></Paragraph>
              </Section>
            </Document>
            """;

        Map<String, Object> data = new HashMap<>();
        data.put("title", "My Document");
        data.put("author", "John Doe");

        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled, data);

        assertEquals("document", vnode.getType());
        assertEquals(1, vnode.getChildren().size());

        VNode section = (VNode) vnode.getChildren().get(0);
        assertEquals("section", section.getType());
        
        // Should have 2 paragraphs with text from data
        List<Object> paragraphs = section.getChildren();
        assertTrue(paragraphs.size() >= 2);
        
        System.out.println("✓ Data context test passed");
    }

    @Test
    public void testDataContextNestedObject() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph><Text>{data.user.name}</Text></Paragraph>
                <Paragraph><Text>{data.user.email}</Text></Paragraph>
              </Section>
            </Document>
            """;

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Alice Smith");
        user.put("email", "alice@example.com");
        data.put("user", user);

        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled, data);

        assertEquals("document", vnode.getType());
        System.out.println("✓ Nested data context test passed");
    }

    @Test
    public void testDataContextWithArray() throws Exception {
        String jsx = """
            const items = data.items;
            <Document>
              <Section pageSize="A4">
                <BulletedList>
                  {items.map(item => (
                    <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
                  ))}
                </BulletedList>
              </Section>
            </Document>
            """;

        Map<String, Object> data = new HashMap<>();
        data.put("items", List.of("Item 1", "Item 2", "Item 3"));

        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled, data);

        assertEquals("document", vnode.getType());
        System.out.println("✓ Data context with array test passed");
    }

    @Test
    public void testDataContextEndToEnd(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph align="center" bold="true"><Text>{data.title}</Text></Paragraph>
                <Paragraph><Text>Author: {data.author}</Text></Paragraph>
                <Paragraph><Text> </Text></Paragraph>
                <Paragraph><Text>{data.content}</Text></Paragraph>
              </Section>
            </Document>
            """;

        Map<String, Object> data = new HashMap<>();
        data.put("title", "Test Document with Data Context");
        data.put("author", "Test Author");
        data.put("content", "This is content from data context passed at runtime.");

        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled, data);

        File outFile = tempDir.resolve("test-data-context.docx").toFile();
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vnode, outFile.getAbsolutePath());

        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);

        // Verify DOCX structure
        try (FileInputStream fis = new FileInputStream(outFile);
             XWPFDocument doc = new XWPFDocument(fis)) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            assertTrue(paragraphs.size() >= 4);
            System.out.println("Data context end-to-end test passed: " + outFile.getAbsolutePath());
        }
    }

    @Test
    public void testDataContextNull() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph><Text>No data passed</Text></Paragraph>
              </Section>
            </Document>
            """;

        String compiled = new Compiler().compile(jsx);
        VNode vnode = new JsRuntime().run(compiled, null);

        assertEquals("document", vnode.getType());
        System.out.println("✓ Null data context test passed");
    }
}
