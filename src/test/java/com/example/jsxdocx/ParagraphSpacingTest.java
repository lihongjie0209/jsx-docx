package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphSpacingTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesBeforeAfterAndLineSpacing() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph before={12} after={6} line={1.5}>Spacing Test</Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("spacing.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            assertEquals(240, p.getSpacingBefore()); // 12pt * 20
            assertEquals(120, p.getSpacingAfter());  // 6pt * 20
            assertNotNull(p.getSpacingBetween());
            assertEquals(1.5, p.getSpacingBetween(), 0.0001);
        }
    }
}
