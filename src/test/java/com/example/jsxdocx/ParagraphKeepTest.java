package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphKeepTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesKeepWithNextAndKeepLines() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph keepWithNext={true} keepLines={true}>A</Paragraph>
                    <Paragraph>B</Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("keep_props.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            String xml = p.getCTP().xmlText();
            assertTrue(xml.contains("keepNext"));
            assertTrue(xml.contains("keepLines"));
        }
    }
}
