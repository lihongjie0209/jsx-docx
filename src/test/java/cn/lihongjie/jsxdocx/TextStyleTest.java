package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TextStyleTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesUnderlineStrikeHighlightFont() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Text underline="double" strike={true} highlight="yellow" font="Arial">Styled</Text>
                    </Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("styled.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphArray(0);
            XWPFRun r = p.getRuns().get(0);
            String xml = r.getCTR().xmlText().toLowerCase();
            assertTrue(xml.contains("underline") || xml.contains("u w:val=\"double\""));
            assertTrue(xml.contains("strike") || xml.contains("strike"));
            assertTrue(xml.contains("highlight") || xml.contains("w:highlight"));
            // Font family may be stored as ascii/hAnsi
            assertTrue(xml.contains("arial"));
        }
    }
}
