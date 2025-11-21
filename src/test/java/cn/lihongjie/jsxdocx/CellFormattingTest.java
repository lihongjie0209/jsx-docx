package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CellFormattingTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsVerticalAlignAndPadding() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table>
                      <Row>
                        <Cell vAlign=\"center\" padding={{ top: 6, bottom: 6, left: 5, right: 5 }}>
                          <Paragraph><Text>A</Text></Paragraph>
                        </Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("cell_fmt.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            XWPFTableCell cell = t.getRow(0).getCell(0);
            CTTcPr pr = cell.getCTTc().getTcPr();
            assertNotNull(pr);
            assertEquals(STVerticalJc.CENTER, pr.getVAlign().getVal());
            CTTcMar mar = pr.getTcMar();
            assertNotNull(mar);
            assertEquals(STTblWidth.DXA, mar.getTop().getType());
            assertEquals(BigInteger.valueOf(120), mar.getTop().getW());   // 6pt -> 120 twips
            assertEquals(BigInteger.valueOf(120), mar.getBottom().getW());
            assertEquals(BigInteger.valueOf(100), mar.getLeft().getW());  // 5pt -> 100 twips
            assertEquals(BigInteger.valueOf(100), mar.getRight().getW());
        }
    }
}
