package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NumberedListStartTest {
    @TempDir
    Path tempDir;

    @Test
    public void supportsCustomStart() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <NumberedList start={5}>
                      <ListItem>First</ListItem>
                      <ListItem>Second</ListItem>
                    </NumberedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("num_start.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p1 = doc.getParagraphs().get(0);
            BigInteger numId = p1.getNumID();
            assertNotNull(numId);
            XWPFNumbering numbering = doc.getNumbering();
            XWPFNum num = numbering.getNum(numId);
            assertNotNull(num);
            assertEquals(BigInteger.valueOf(5), num.getCTNum().getLvlOverrideArray(0).getStartOverride().getVal());
        }
    }
}
