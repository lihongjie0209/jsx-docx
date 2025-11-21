package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RowHeightTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsRowHeightInTwips() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row height={24}>
                        <Cell><Paragraph><Text>A</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("row_height.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableRow r0 = t.getRow(0);
            String xml = r0.getCtRow().xmlText();
            assertTrue(xml.contains("trHeight"));
        }
    }
}
