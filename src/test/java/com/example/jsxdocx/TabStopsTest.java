package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TabStopsTest {
    @TempDir
    Path tempDir;

    @Test
    public void paragraphWithTabStopsRendersTabsXml() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph tabStops={[{pos:72, align:'center', leader:'dots'}, {pos:144, align:'right'}]}>
                      <Text>A</Text><Tab/><Text>B</Text>
                    </Paragraph>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("tabs.docx");
        new Renderer().renderToDocx(v, out.toString());

        // Inspect word/document.xml
        boolean foundTabs = false;
        boolean foundCenterPos = false;
        boolean foundRightPos = false;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(out.toFile()))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if ("word/document.xml".equals(e.getName())) {
                    String xml = new String(zis.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    String lower = xml.toLowerCase();
                    foundTabs = lower.contains("<w:tabs") || lower.contains(":tabs");
                    // 72pt -> 1440 twips, 144pt -> 2880 twips
                    foundCenterPos = lower.contains("val=\"center\"") || lower.contains("w:val=\"center\"");
                    foundCenterPos = foundCenterPos && (lower.contains("pos=\"1440\"") || lower.contains("w:pos=\"1440\""));
                    foundRightPos = lower.contains("val=\"right\"") || lower.contains("w:val=\"right\"");
                    foundRightPos = foundRightPos && (lower.contains("pos=\"2880\"") || lower.contains("w:pos=\"2880\""));
                    break;
                }
            }
        }
        assertTrue(foundTabs, "Expected <w:tabs> in document.xml");
        assertTrue(foundCenterPos, "Expected center tab at 1440 twips");
        assertTrue(foundRightPos, "Expected right tab at 2880 twips");
    }
}
