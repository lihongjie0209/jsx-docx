package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TableHeaderRowTest {
    @TempDir
    Path tempDir;

    @Test
    public void repeatsHeaderRow() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row header={true}>
                        <Cell><Paragraph><Text>H1</Text></Paragraph></Cell>
                        <Cell><Paragraph><Text>H2</Text></Paragraph></Cell>
                      </Row>
                      <Row>
                        <Cell><Paragraph><Text>A1</Text></Paragraph></Cell>
                        <Cell><Paragraph><Text>A2</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("header_row.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r0 = t.getRow(0);
            CTTrPr pr = r0.getCtRow().getTrPr();
            assertNotNull(pr);
            String xml = r0.getCtRow().xmlText();
            assertTrue(xml.contains("tblHeader"));
        }
    }
}
