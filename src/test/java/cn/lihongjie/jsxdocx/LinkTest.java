package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LinkTest {
    @TempDir
    Path tempDir;

    @Test
    public void rendersExternalHyperlink() throws Exception {
        String href = "https://example.com";
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Link href=\"%s\">Open</Link>
                    </Paragraph>
                  </Document>
                );
                """.formatted(href);
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("link.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            assertEquals("Open", p.getText());
            boolean hasHyperlinkRun = p.getRuns().stream().anyMatch(r -> r instanceof XWPFHyperlinkRun);
            assertTrue(hasHyperlinkRun);
        }
    }
}
