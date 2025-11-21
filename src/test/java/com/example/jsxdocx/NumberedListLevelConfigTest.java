package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class NumberedListLevelConfigTest {
    @TempDir
    Path tempDir;

    @Test
    public void appliesPerLevelIndentAndLvlText() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <NumberedList format="decimal" levelConfig={[
                      { lvlText: "%1)", indent: { left: 24, hanging: 12 } },
                      { format: "upperRoman", lvlText: "%1.%2)", indent: { left: 48, hanging: 24 } }
                    ]}>
                      <ListItem level={0}><Text>A</Text></ListItem>
                      <ListItem level={1}><Text>B</Text></ListItem>
                    </NumberedList>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("lvlcfg.docx");
        new Renderer().renderToDocx(v, out.toString());

        boolean lvl1Indent = false;
        boolean lvl1TextPattern = false;
        boolean lvl2FormatRoman = false;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(out.toFile()))) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if ("word/numbering.xml".equals(e.getName())) {
                    String xml = new String(zis.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).toLowerCase();
                    // 24pt => 480 twips; 12pt => 240 twips
                    lvl1Indent = xml.contains("ilvl=\"0\"") && (xml.contains("w:left=\"480\"") || xml.contains("left=\"480\"")) && (xml.contains("w:hanging=\"240\"") || xml.contains("hanging=\"240\""));
                    lvl1TextPattern = xml.contains("ilvl=\"0\"") && xml.contains("%1)");
                    // roman may appear as "upperRoman" or similar, rely on numFmt value
                    lvl2FormatRoman = xml.contains("ilvl=\"1\"") && (xml.contains("val=\"upper-roman\"") || xml.contains("val=\"upper_roman\"") || xml.contains("upperroman"));
                }
            }
        }
        assertTrue(lvl1Indent, "Expected level 0 left/hanging indents");
        assertTrue(lvl1TextPattern, "Expected level 0 lvlText pattern %1)");
        assertTrue(lvl2FormatRoman, "Expected level 1 format upper-roman");
    }
}
