package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class NumberedListFormatTest {
    @TempDir
    Path tempDir;

    @Test
    public void supportsUpperRomanFormat() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <NumberedList format="upperRoman">
                      <ListItem>One</ListItem>
                      <ListItem>Two</ListItem>
                    </NumberedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("numfmt.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(out.toFile())) {
          java.util.zip.ZipEntry entry = zip.getEntry("word/numbering.xml");
          assertNotNull(entry);
          try (java.io.InputStream is = zip.getInputStream(entry)) {
            String xml = new String(is.readAllBytes()).toLowerCase();
            assertTrue(xml.contains("upperroman") || xml.contains("upper-roman") || xml.contains("upper_roman"));
          }
        }
    }
}
