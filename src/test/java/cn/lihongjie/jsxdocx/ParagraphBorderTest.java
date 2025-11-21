package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphBorderTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsParagraphBordersPerSide() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph border={{ size: 2, color: "#FF0000", sides: ["top","bottom"] }}>
                      <Text>Bordered Paragraph</Text>
                    </Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("pbdr.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            String pXml = doc.getParagraphArray(0).getCTP().xmlText().toLowerCase();
            assertTrue(pXml.contains("w:ppr"));
            assertTrue(pXml.contains("w:pbdR".toLowerCase()));
            assertTrue(pXml.contains("w:top"));
            assertTrue(pXml.contains("w:bottom"));
            assertTrue(pXml.contains("color=\"ff0000\""));
            // size is in eighths of a point, 2pt => 16
            assertTrue(pXml.contains("w:sz=\"16\"") || pXml.contains("sz=\"16\""));
            // left/right not present
            assertFalse(pXml.contains("w:left\""));
            assertFalse(pXml.contains("w:right\""));
        }
    }
}
