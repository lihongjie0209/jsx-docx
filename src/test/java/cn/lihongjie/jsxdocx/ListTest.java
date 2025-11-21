package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {
    @TempDir
    Path tempDir;

    @Test
    public void rendersBulletedList() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <BulletedList>
                      <ListItem>Item A</ListItem>
                      <ListItem>Item B</ListItem>
                    </BulletedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("bullets.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertTrue(doc.getParagraphs().size() >= 2);
            XWPFParagraph p = doc.getParagraphs().get(0);
            BigInteger numId = p.getNumID();
            assertNotNull(numId);
            assertEquals("Item A", p.getText());
        }
    }

    @Test
    public void rendersNumberedList() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <NumberedList>
                      <ListItem>First</ListItem>
                      <ListItem>Second</ListItem>
                    </NumberedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("numbers.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p1 = doc.getParagraphs().get(0);
            XWPFParagraph p2 = doc.getParagraphs().get(1);
            assertNotNull(p1.getNumID());
            assertNotNull(p2.getNumID());
            assertEquals("First", p1.getText());
            assertEquals("Second", p2.getText());
        }
    }
}
