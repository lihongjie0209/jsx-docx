package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class MultiLevelListTest {
    @TempDir
    Path tempDir;

    @Test
    public void numberedListSupportsMultipleLevels() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <NumberedList format="upperRoman">
                      <ListItem level={0}><Text>Top</Text></ListItem>
                      <ListItem level={1}><Text>Sub</Text></ListItem>
                    </NumberedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("mlist.docx");
        new Renderer().renderToDocx(v, out.toString());

        boolean hasLevel1InNumbering = false;
        boolean paraHasIlvl1 = false;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(out.toFile()))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if ("word/numbering.xml".equals(e.getName())) {
                    String xml = new String(zis.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).toLowerCase();
                    hasLevel1InNumbering = xml.contains("ilvl=\"1\"") || xml.contains("w:ilvl=\"1\"");
                }
                if ("word/document.xml".equals(e.getName())) {
                    String xml = new String(zis.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).toLowerCase();
                    // paragraph properties for list items include ilvl entries, expect one with ilvl=1
                    paraHasIlvl1 = xml.contains("<w:ilvl w:val=\"1\"") || xml.contains("ilvl w:val=\"1\"");
                }
            }
        }
        assertTrue(hasLevel1InNumbering, "Expected level 1 defined in numbering.xml");
        assertTrue(paraHasIlvl1, "Expected a paragraph with ilvl=1");
    }
}
