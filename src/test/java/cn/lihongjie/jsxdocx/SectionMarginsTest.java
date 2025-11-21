package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SectionMarginsTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsSectionMarginsInches() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section margins={{ top: 1, bottom: 1, left: 1.25, right: 1.25 }}>
                      <Paragraph>Content</Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("margins.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            CTPageMar mar = doc.getDocument().getBody().getSectPr().getPgMar();
            assertEquals(1440, Integer.parseInt(String.valueOf(mar.getTop())));
            assertEquals(1440, Integer.parseInt(String.valueOf(mar.getBottom())));
            assertEquals(1800, Integer.parseInt(String.valueOf(mar.getLeft())));
            assertEquals(1800, Integer.parseInt(String.valueOf(mar.getRight())));
        }
    }
}
