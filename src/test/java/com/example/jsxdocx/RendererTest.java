package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RendererTest {

    @TempDir
    Path tempDir;

    @Test
    public void rendersSimpleParagraph() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>Hello World</Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("simple.docx");
        new Renderer().renderToDocx(v, out.toString());

        assertTrue(Files.exists(out));
        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertEquals(1, doc.getParagraphs().size());
            assertEquals("Hello World", doc.getParagraphs().get(0).getText());
        }
    }

    @Test
    public void rendersTextFormatting() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Text bold color="#FF0000" size={24}>Bold Red Text</Text>
                    </Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("format.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            XWPFRun run = p.getRuns().get(0);
            assertTrue(run.isBold());
            assertEquals("FF0000", run.getColor());
            assertEquals(24, run.getFontSize());
            assertEquals("Bold Red Text", run.text());
        }
    }

    @Test
    public void rendersTable() throws Exception {
        String jsx = """
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
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("table.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertEquals(1, doc.getTables().size());
            XWPFTable t = doc.getTables().get(0);
            assertEquals(2, t.getRows().size());
            assertEquals("Cell 1", t.getRow(0).getCell(0).getText());
        }
    }

    @Test
    public void rendersHeading() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Heading level={1}>Main Title</Heading>
                    <Paragraph>Some content</Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("heading.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertEquals(2, doc.getParagraphs().size());
            assertEquals("Heading1", doc.getParagraphs().get(0).getStyle());
        }
    }

    @Test
    public void rendersComplex() throws Exception {
        String jsx = """
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
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("complex.docx");
        new Renderer().renderToDocx(v, out.toString());

        assertTrue(Files.exists(out));
        assertTrue(Files.size(out) > 1000);
    }
}
