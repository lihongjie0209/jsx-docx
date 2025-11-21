package cn.lihongjie.jsxdocx;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import cn.lihongjie.jsxdocx.model.VNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Replaced by split component tests")
public class JsxDocxGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    public void testCompiler() throws Exception {
      String jsxCode = "const x = <div>Hello</div>;";
        Compiler compiler = new Compiler();
        String jsCode = compiler.compile(jsxCode);

    assertNotNull(jsCode);
    assertTrue(jsCode.contains("React.createElement"));
    }

    @Test
    public void testJsRuntime() throws Exception {
                String jsCode = """
                                render(
                                    <Document>
                                        <Paragraph>Hello World</Paragraph>
                                    </Document>
                                );
                                """;

        JsRuntime runtime = new JsRuntime();
        VNode result = runtime.run(new Compiler().compile(jsCode));

        assertNotNull(result);
        assertEquals("document", result.getType());
    }

    @Test
    public void testSimpleDocument() throws Exception {
        // JSX code for a simple document
        String jsxCode = """
            render(
              <Document>
                <Paragraph>Hello World</Paragraph>
              </Document>
            );
            """;

        // Execute JS
        JsRuntime runtime = new JsRuntime();
        VNode vDom = runtime.run(new Compiler().compile(jsxCode));

        // Render to DOCX
        Path outputPath = tempDir.resolve("test-simple.docx");
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vDom, outputPath.toString());

        // Verify file exists
        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);

        // Verify content
        try (FileInputStream fis = new FileInputStream(outputPath.toFile());
                XWPFDocument doc = new XWPFDocument(fis)) {

            assertEquals(1, doc.getParagraphs().size());
            XWPFParagraph para = doc.getParagraphs().get(0);
            String text = para.getText();
            assertEquals("Hello World", text);
        }
    }

    @Test
    public void testTextFormatting() throws Exception {
                String jsxCode = """
                                render(
                                    <Document>
                                        <Paragraph>
                                            <Text bold color="#FF0000" size={24}>Bold Red Text</Text>
                                        </Paragraph>
                                    </Document>
                                );
                                """;

        JsRuntime runtime = new JsRuntime();
                VNode vDom = runtime.run(new Compiler().compile(jsxCode));

        Path outputPath = tempDir.resolve("test-formatting.docx");
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vDom, outputPath.toString());

        assertTrue(Files.exists(outputPath));

        try (FileInputStream fis = new FileInputStream(outputPath.toFile());
                XWPFDocument doc = new XWPFDocument(fis)) {

            XWPFParagraph para = doc.getParagraphs().get(0);
            XWPFRun run = para.getRuns().get(0);

            assertTrue(run.isBold());
            assertEquals("FF0000", run.getColor());
            assertEquals(24, run.getFontSize());
            assertEquals("Bold Red Text", run.text());
        }
    }

    @Test
    public void testTable() throws Exception {
                String jsxCode = """
                                render(
                                    <Document>
                                        <Table>
                                            <Row>
                                                <Cell><Paragraph><Text>Cell 1</Text></Paragraph></Cell>
                                                <Cell><Paragraph><Text>Cell 2</Text></Paragraph></Cell>
                                            </Row>
                                            <Row>
                                                <Cell><Paragraph><Text>Cell 3</Text></Paragraph></Cell>
                                                <Cell><Paragraph><Text>Cell 4</Text></Paragraph></Cell>
                                            </Row>
                                        </Table>
                                    </Document>
                                );
                                """;

        JsRuntime runtime = new JsRuntime();
                VNode vDom = runtime.run(new Compiler().compile(jsxCode));

        Path outputPath = tempDir.resolve("test-table.docx");
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vDom, outputPath.toString());

        assertTrue(Files.exists(outputPath));

        try (FileInputStream fis = new FileInputStream(outputPath.toFile());
                XWPFDocument doc = new XWPFDocument(fis)) {

            assertEquals(1, doc.getTables().size());
            XWPFTable table = doc.getTables().get(0);

            assertEquals(2, table.getRows().size());
            assertEquals(2, table.getRow(0).getTableCells().size());

            String cell1Text = table.getRow(0).getCell(0).getText();
            assertEquals("Cell 1", cell1Text);
        }
    }

    @Test
    public void testHeading() throws Exception {
        String jsxCode = """
            render(
              <Document>
                <Heading level={1}>Main Title</Heading>
                <Paragraph>Some content</Paragraph>
              </Document>
            );
            """;

        JsRuntime runtime = new JsRuntime();
        VNode vDom = runtime.run(new Compiler().compile(jsxCode));

        Path outputPath = tempDir.resolve("test-heading.docx");
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vDom, outputPath.toString());

        assertTrue(Files.exists(outputPath));

        try (FileInputStream fis = new FileInputStream(outputPath.toFile());
                XWPFDocument doc = new XWPFDocument(fis)) {

            assertEquals(2, doc.getParagraphs().size());
            XWPFParagraph heading = doc.getParagraphs().get(0);
            assertEquals("Heading1", heading.getStyle());
        }
    }

    @Test
    public void testComplexDocument() throws Exception {
                String jsxCode = """
                                render(
                                    <Document>
                                        <Section pageSize="A4">
                                            <Heading level={1} align="center">JSX to Word Demo</Heading>
                                            <Paragraph align="left">
                                                This is a <Text bold>bold</Text> and <Text italic>italic</Text> text.
                                            </Paragraph>
                                            <Table>
                                                <Row>
                                                    <Cell><Paragraph><Text bold>Header 1</Text></Paragraph></Cell>
                                                    <Cell><Paragraph><Text bold>Header 2</Text></Paragraph></Cell>
                                                </Row>
                                                <Row>
                                                    <Cell><Paragraph><Text>Data 1</Text></Paragraph></Cell>
                                                    <Cell><Paragraph><Text>Data 2</Text></Paragraph></Cell>
                                                </Row>
                                            </Table>
                                        </Section>
                                    </Document>
                                );
                                """;

        JsRuntime runtime = new JsRuntime();
                VNode vDom = runtime.run(new Compiler().compile(jsxCode));

        Path outputPath = tempDir.resolve("test-complex.docx");
        Renderer renderer = new Renderer();
        renderer.renderToDocx(vDom, outputPath.toString());

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 1000); // Should be a reasonable size
    }
}
