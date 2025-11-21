package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderFooterTest {
    @TempDir
    Path tempDir;

    @Test
    public void rendersHeaderFooterWithPageNumber() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Header>
                      <Paragraph><Text>Title</Text></Paragraph>
                    </Header>
                    <Footer>
                      <Paragraph><Text>FooterText </Text><PageNumber/></Paragraph>
                    </Footer>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("hf.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            Object policy = null;
            try { policy = XWPFDocument.class.getMethod("getHeaderFooterPolicy").invoke(doc); } catch (Throwable t) {}
            assertNotNull(policy);
            XWPFFooter footer = null;
            try { footer = (XWPFFooter) policy.getClass().getMethod("getDefaultFooter").invoke(policy); } catch (Throwable t) {}
            assertNotNull(footer);
            String footerPXml = footer.getParagraphArray(0).getCTP().xmlText();
            assertTrue(footerPXml.contains("PAGE"));
            assertTrue(footerPXml.toLowerCase().contains("footertext"));
        }
    }
}
