package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class LineBreakTabTest {
    @TempDir
    Path tempDir;

    @Test
    public void rendersBrAndTabInParagraph() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Text>First</Text><Br /><Text>Second</Text><Tab /><Text>Third</Text>
                    </Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("brtab.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            String pXml = doc.getParagraphArray(0).getCTP().xmlText().toLowerCase();
            assertTrue(pXml.contains("<w:br/>") || pXml.contains("<w:br />"));
            assertTrue(pXml.contains("<w:tab/>") || pXml.contains("<w:tab />"));
            assertTrue(pXml.contains("first"));
            assertTrue(pXml.contains("second"));
            assertTrue(pXml.contains("third"));
        }
    }
}
