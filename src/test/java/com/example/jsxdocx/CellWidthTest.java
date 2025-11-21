package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CellWidthTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsPercentWidthAndPtWidth() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row>
                        <Cell width=\"70%\"><Paragraph><Text>A</Text></Paragraph></Cell>
                        <Cell width={72}><Paragraph><Text>B</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("cell_width.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableCell c0 = t.getRow(0).getCell(0);
            XWPFTableCell c1 = t.getRow(0).getCell(1);
            CTTblWidth w0 = c0.getCTTc().getTcPr().getTcW();
            CTTblWidth w1 = c1.getCTTc().getTcPr().getTcW();
            assertEquals(STTblWidth.PCT, w0.getType());
            assertEquals(BigInteger.valueOf(3500), w0.getW()); // 70% => 3500
            assertEquals(STTblWidth.DXA, w1.getType());
            assertEquals(BigInteger.valueOf(1440), w1.getW()); // 72pt => 1440 twips
        }
    }
}
