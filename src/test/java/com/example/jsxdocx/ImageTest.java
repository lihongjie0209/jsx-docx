package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ImageTest {
    @TempDir
    Path tempDir;

    // 1x1 transparent PNG
    private static final String DATA_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQMAAAAl21bKAAAAA1BMVEUAAACnej3aAAAAAXRSTlMAQObYZgAAAApJREFUCNdjYAAAAAIAAeIhvDMAAAAASUVORK5CYII=";

    @Test
    public void insertsInlineImage() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Image src=\"%s\" width={16} height={16} />
                    </Paragraph>
                  </Document>
                );
                """.formatted(DATA_URI);
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("img.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertTrue(doc.getAllPictures().size() >= 1);
        }
    }
}
