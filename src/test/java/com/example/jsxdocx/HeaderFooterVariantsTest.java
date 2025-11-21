package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderFooterVariantsTest {
    @TempDir
    Path tempDir;

    @Test
    public void createsFirstEvenAndDefaultHeaderFooter() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Header type="first">
                      <Paragraph><Text>FirstHead</Text></Paragraph>
                    </Header>
                    <Header type="even">
                      <Paragraph><Text>EvenHead</Text></Paragraph>
                    </Header>
                    <Header>
                      <Paragraph><Text>DefaultHead</Text></Paragraph>
                    </Header>
                    <Footer type="first">
                      <Paragraph><Text>FirstFoot</Text></Paragraph>
                    </Footer>
                    <Footer type="even">
                      <Paragraph><Text>EvenFoot</Text></Paragraph>
                    </Footer>
                    <Footer>
                      <Paragraph><Text>DefaultFoot</Text></Paragraph>
                    </Footer>
                    <Paragraph><Text>Body</Text></Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("hfvar.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile()); XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFHeaderFooterPolicy policy = doc.getHeaderFooterPolicy();
            if (policy == null) policy = doc.createHeaderFooterPolicy();
            XWPFHeader firstH = policy.getFirstPageHeader();
            XWPFHeader evenH = policy.getEvenPageHeader();
            XWPFHeader defH = policy.getDefaultHeader();
            XWPFFooter firstF = policy.getFirstPageFooter();
            XWPFFooter evenF = policy.getEvenPageFooter();
            XWPFFooter defF = policy.getDefaultFooter();

            assertNotNull(defH);
            assertNotNull(defF);
            assertNotNull(firstH);
            assertNotNull(firstF);
            assertNotNull(evenH);
            assertNotNull(evenF);

            String h1 = firstH.getParagraphArray(0).getText();
            String h2 = evenH.getParagraphArray(0).getText();
            String h3 = defH.getParagraphArray(0).getText();
            String f1 = firstF.getParagraphArray(0).getText();
            String f2 = evenF.getParagraphArray(0).getText();
            String f3 = defF.getParagraphArray(0).getText();

            assertTrue(h1.contains("FirstHead"));
            assertTrue(h2.contains("EvenHead"));
            assertTrue(h3.contains("DefaultHead"));
            assertTrue(f1.contains("FirstFoot"));
            assertTrue(f2.contains("EvenFoot"));
            assertTrue(f3.contains("DefaultFoot"));
        }
    }
}
