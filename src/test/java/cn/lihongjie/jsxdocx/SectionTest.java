package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsPageSizeA4() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section pageSize=\"A4\">
                      <Paragraph>Content</Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("a4.docx");
        new Renderer().renderToDocx(v, out.toString());
        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            CTPageSz sz = doc.getDocument().getBody().getSectPr().getPgSz();
            int w = Integer.parseInt(String.valueOf(sz.getW()));
            int h = Integer.parseInt(String.valueOf(sz.getH()));
            assertEquals(11900, w);
            assertEquals(16840, h);
        }
    }
}
