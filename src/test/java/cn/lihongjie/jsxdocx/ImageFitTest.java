package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import java.util.regex.*;

public class ImageFitTest {
    @TempDir
    Path tempDir;

    private static final int EMU_PER_PX = 9525;

    @Test
    public void containScalesWithinBounds() throws Exception {
        // Create a 200x100 test image
        BufferedImage img = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED); g.fillRect(0,0,200,100); g.dispose();
        File f = tempDir.resolve("img200x100.png").toFile();
        ImageIO.write(img, "png", f);

        String path = f.getAbsolutePath().replace("\\", "\\\\");
        String jsx = """
                render(
                  <Document>
                    <Paragraph>
                      <Image src="%s" fit="contain" maxWidth={100} maxHeight={100} />
                    </Paragraph>
                  </Document>
                );
                """.formatted(path);

        VNode v = new JsRuntime().run(new Compiler().compile(jsx));
        Path out = tempDir.resolve("img_contain.docx");
        new Renderer().renderToDocx(v, out.toString());

        try (FileInputStream fis = new FileInputStream(out.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFParagraph p = doc.getParagraphs().get(0);
            String xml = p.getCTP().xmlText();
            Pattern cxp = Pattern.compile("cx=\\\"(\\d+)\\\"");
            Pattern cyp = Pattern.compile("cy=\\\"(\\d+)\\\"");
            Matcher mx = cxp.matcher(xml);
            Matcher my = cyp.matcher(xml);
            assertTrue(mx.find());
            assertTrue(my.find());
            long cx = Long.parseLong(mx.group(1));
            long cy = Long.parseLong(my.group(1));
            double ratio = (double) cx / Math.max(1, cy);
            assertEquals(2.0, ratio, 0.05);
        }
    }
}
