package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ColspanRowspanTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesColspanAndRowspan() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row>
                        <Cell colspan={2} rowspan={2}><Paragraph><Text>A</Text></Paragraph></Cell>
                        <Cell><Paragraph><Text>B</Text></Paragraph></Cell>
                      </Row>
                      <Row>
                        <Cell><Paragraph><Text>C</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("span.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r0 = t.getRow(0);
            String r0c0 = r0.getCell(0).getCTTc().xmlText().toLowerCase();
            assertTrue(r0c0.contains("gridspan"));
            assertTrue(r0c0.contains("vmerge"));
            assertTrue(r0c0.contains("restart"));

            XWPFTableRow r1 = t.getRow(1);
            // due to rowspan=2 and colspan=2, the second row should start with two continue cells
            String r1c0 = r1.getCell(0).getCTTc().xmlText().toLowerCase();
            String r1c1 = r1.getCell(1).getCTTc().xmlText().toLowerCase();
            assertTrue(r1c0.contains("vmerge") && r1c0.contains("continue"));
            assertTrue(r1c1.contains("vmerge") && r1c1.contains("continue"));
        }
    }
}
