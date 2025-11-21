package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphBackgroundTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsParagraphBackgroundShading() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph background="#FFFF00"><Text>Highlight Paragraph</Text></Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("pbg.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            String pXml = doc.getParagraphArray(0).getCTP().xmlText().toLowerCase();
            assertTrue(pXml.contains("w:shd"));
            assertTrue(pXml.contains("fill=\"ffff00\""));
        }
    }
}
