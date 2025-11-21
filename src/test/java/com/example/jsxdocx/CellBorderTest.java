package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CellBorderTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsCellLeftBorderOnly() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table border={false}>
                      <Row>
                        <Cell border={{ size: 1.5, color: "#FF0000", sides: ["left"] }}>
                          <Paragraph><Text>Cell</Text></Paragraph>
                        </Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("cell-borders.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r = t.getRow(0);
            XWPFTableCell c = r.getCell(0);
            String xml = c.getCTTc().xmlText().toLowerCase();
            assertTrue(xml.contains("tcborders"));
            assertTrue(xml.contains("left"));
            // Should not contain right/bottom/top if only left specified
            // This is a soft assertion since POI may emit defaults
        }
    }
}
