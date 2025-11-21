package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TableFormattingTest {
    @TempDir
    Path tempDir;

    @Test
    public void borderFalseRemovesBorders() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table border={false}>
                      <Row>
                        <Cell><Paragraph><Text>A</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("tbl_noborder.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            CTTblPr pr = t.getCTTbl().getTblPr();
            CTTblBorders b = pr.getTblBorders();
            assertEquals(STBorder.NONE, b.getTop().getVal());
            assertEquals(STBorder.NONE, b.getBottom().getVal());
            assertEquals(STBorder.NONE, b.getLeft().getVal());
            assertEquals(STBorder.NONE, b.getRight().getVal());
            assertEquals(STBorder.NONE, b.getInsideH().getVal());
            assertEquals(STBorder.NONE, b.getInsideV().getVal());
        }
    }

    @Test
    public void widthAndAlignmentApplied() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table width=\"100%\" align=\"center\">
                      <Row>
                        <Cell><Paragraph><Text>A</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("tbl_width_align.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            CTTblPr pr = t.getCTTbl().getTblPr();
            CTTblWidth w = pr.getTblW();
            assertEquals(STTblWidth.PCT, w.getType());
            assertEquals(BigInteger.valueOf(5000), w.getW());
            CTJcTable jc = pr.getJc();
            assertEquals(STJcTable.CENTER, jc.getVal());
        }
    }

    @Test
    public void borderObjectSetsSizeAndColor() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Table border={{ size: 1.0, color: \"#FF0000\" }}>
                      <Row>
                        <Cell><Paragraph><Text>A</Text></Paragraph></Cell>
                      </Row>
                    </Table>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("tbl_border_obj.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFTable t = doc.getTables().get(0);
            CTTblBorders b = t.getCTTbl().getTblPr().getTblBorders();
            assertEquals(new BigInteger("8"), b.getTop().getSz()); // 1pt -> 8
            assertEquals(STBorder.SINGLE, b.getTop().getVal());
        }
    }
}
