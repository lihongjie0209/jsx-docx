package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SectionOrientationTest {
    @TempDir
    Path tempDir;

    @Test
    public void setsLandscapeOrientation() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section orientation="landscape" pageSize="A4">
                      <Paragraph><Text>Landscape</Text></Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("orient.docx");
        new Renderer().renderToDocx(v, out.toString());

        boolean hasLandscape = false;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(out.toFile()))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if ("word/document.xml".equals(e.getName())) {
                    String xml = new String(zis.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).toLowerCase();
                    hasLandscape = xml.contains("orient=\"landscape\"") || xml.contains("w:orient=\"landscape\"");
                    break;
                }
            }
        }
        assertTrue(hasLandscape, "Expected landscape orientation in pgSz");
    }
}
