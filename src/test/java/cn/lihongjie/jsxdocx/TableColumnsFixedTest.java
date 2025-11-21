package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TableColumnsFixedTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsTblGridAndFixedLayout() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table layout={"fixed"} columns={[50,100,50]}>
                      <Row>
                        <Cell><Paragraph><Text>A</Text></Paragraph></Cell>
                        <Cell><Paragraph><Text>B</Text></Paragraph></Cell>
                        <Cell><Paragraph><Text>C</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("tbl_columns.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            String xml = t.getCTTbl().xmlText().toLowerCase();
            assertTrue(xml.contains("tbllayout"));
            assertTrue(xml.contains("fixed"));
            assertTrue(xml.contains("tblgrid"));
            // 50pt = 1000 twips, 100pt = 2000 twips
            assertTrue(xml.contains("w=\"1000\""));
            assertTrue(xml.contains("w=\"2000\""));
        }
    }
}
