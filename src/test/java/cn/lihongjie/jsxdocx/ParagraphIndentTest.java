package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ParagraphIndentTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesIndents() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph indentLeft={24} indentRight={12} firstLine={18}>Indents</Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("indent.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            assertEquals(480, p.getIndentationLeft());   // 24pt * 20
            assertEquals(240, p.getIndentationRight());  // 12pt * 20
            assertEquals(360, p.getIndentationFirstLine()); // 18pt * 20
        }
    }
}
