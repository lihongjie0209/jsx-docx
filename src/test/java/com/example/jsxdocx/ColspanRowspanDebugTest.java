package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

@Disabled("Debug helper; do not run in CI")
public class ColspanRowspanDebugTest {
  @Test
    public void printSecondRowCells() throws Exception {
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
        Path out = Path.of("target", "span-debug.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(new File(out.toString()));
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r1 = t.getRow(1);
            for (int i = 0; i < r1.getTableCells().size(); i++) {
                String cellXml = r1.getCell(i).getCTTc().xmlText();
                System.out.println("ROW2 CELL " + i + ":\n" + cellXml + "\n");
            }
        }
    }
  }
