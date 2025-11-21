package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CellBackgroundTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsCellBackgroundFill() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row>
                        <Cell background={"#FFEE00"}><Paragraph><Text>A</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("cell_bg.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r0 = t.getRow(0);
            XWPFTableCell c0 = r0.getCell(0);
            String xml = c0.getCTTc().xmlText();
            assertTrue(xml.toLowerCase().contains("ffee00"));
        }
    }
}
