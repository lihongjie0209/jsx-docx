package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that pageSize and orientation work together correctly.
 * When landscape orientation is specified, width and height should be swapped.
 */
public class SectionPageSizeLandscapeTest {
    @TempDir
    Path tempDir;

    @Test
    public void a4LandscapeSwapsDimensions() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section pageSize="A4" orientation="landscape">
                      <Paragraph><Text>A4 Landscape</Text></Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("a4-landscape.docx");
        new Renderer().renderToDocx(v, out.toString());
        
        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            CTPageSz sz = doc.getDocument().getBody().getSectPr().getPgSz();
            
            // In landscape, dimensions should be swapped
            // A4 portrait: w=11900, h=16840
            // A4 landscape: w=16840, h=11900
            int w = Integer.parseInt(String.valueOf(sz.getW()));
            int h = Integer.parseInt(String.valueOf(sz.getH()));
            
            assertEquals(16840, w, "Width should be the original height");
            assertEquals(11900, h, "Height should be the original width");
            assertEquals(STPageOrientation.LANDSCAPE, sz.getOrient(), "Orientation should be landscape");
        }
    }

    @Test
    public void letterLandscapeSwapsDimensions() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section pageSize="LETTER" orientation="landscape">
                      <Paragraph><Text>Letter Landscape</Text></Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("letter-landscape.docx");
        new Renderer().renderToDocx(v, out.toString());
        
        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            CTPageSz sz = doc.getDocument().getBody().getSectPr().getPgSz();
            
            // In landscape, dimensions should be swapped
            // Letter portrait: w=12240, h=15840
            // Letter landscape: w=15840, h=12240
            int w = Integer.parseInt(String.valueOf(sz.getW()));
            int h = Integer.parseInt(String.valueOf(sz.getH()));
            
            assertEquals(15840, w, "Width should be the original height");
            assertEquals(12240, h, "Height should be the original width");
            assertEquals(STPageOrientation.LANDSCAPE, sz.getOrient(), "Orientation should be landscape");
        }
    }

    @Test
    public void a4PortraitKeepsDimensions() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Section pageSize="A4" orientation="portrait">
                      <Paragraph><Text>A4 Portrait</Text></Paragraph>
                    </Section>
                  </Document>
                );
                """;
        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("a4-portrait.docx");
        new Renderer().renderToDocx(v, out.toString());
        
        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            CTPageSz sz = doc.getDocument().getBody().getSectPr().getPgSz();
            
            // Portrait dimensions should not be swapped
            int w = Integer.parseInt(String.valueOf(sz.getW()));
            int h = Integer.parseInt(String.valueOf(sz.getH()));
            
            assertEquals(11900, w, "Width should remain as set");
            assertEquals(16840, h, "Height should remain as set");
            assertEquals(STPageOrientation.PORTRAIT, sz.getOrient(), "Orientation should be portrait");
        }
    }
}
