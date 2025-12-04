package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Watermark component rendering.
 * Watermarks are implemented using VML shapes in document headers.
 */
public class WatermarkTest {

    private Compiler compiler;
    private JsRuntime runtime;
    private Renderer renderer;

    @BeforeEach
    public void setUp() {
        compiler = new Compiler();
        runtime = new JsRuntime();
        renderer = new Renderer();
    }

    @Test
    void testBasicWatermark(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark text="CONFIDENTIAL" />
                <Section>
                    <Paragraph>
                        <Text>This document has a watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("basic-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        // Verify document was created
        assertTrue(java.nio.file.Files.exists(output));
        assertTrue(java.nio.file.Files.size(output) > 0);
        
        // Verify headers were created (watermarks are in headers)
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Basic watermark test passed: " + output);
    }

    @Test
    void testWatermarkWithCustomColor(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark text="DRAFT" color="#FF0000" />
                <Section>
                    <Paragraph>
                        <Text>This document has a red watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("color-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Color watermark test passed: " + output);
    }

    @Test
    void testWatermarkWithFontSize(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark text="机密" fontSize={72} />
                <Section>
                    <Paragraph>
                        <Text>This document has a large Chinese watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("large-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Font size watermark test passed: " + output);
    }

    @Test
    void testWatermarkWithRotation(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark text="SAMPLE" rotation={-30} />
                <Section>
                    <Paragraph>
                        <Text>This document has a rotated watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("rotated-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Rotated watermark test passed: " + output);
    }

    @Test
    void testWatermarkWithAllProperties(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark 
                    text="TOP SECRET" 
                    color="#0000FF" 
                    fontSize={60} 
                    rotation={-45}
                    fontFamily="Arial Black"
                />
                <Section>
                    <Paragraph>
                        <Text>This document has a fully customized watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("full-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Full watermark test passed: " + output);
    }

    @Test
    void testMultiplePagesWithWatermark(@TempDir Path tempDir) throws Exception {
        String jsx = """
            <Document>
                <Watermark text="DRAFT" color="#AAAAAA" />
                <Section>
                    <Paragraph>
                        <Text>Page 1 content.</Text>
                    </Paragraph>
                </Section>
                <Section>
                    <Paragraph>
                        <Text>Page 2 content.</Text>
                    </Paragraph>
                </Section>
                <Section>
                    <Paragraph>
                        <Text>Page 3 content.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("multipage-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Multi-page watermark test passed: " + output);
    }

    @Test
    void testDefaultWatermarkValues(@TempDir Path tempDir) throws Exception {
        // Test watermark with minimal props - should use defaults
        String jsx = """
            <Document>
                <Watermark />
                <Section>
                    <Paragraph>
                        <Text>This document has a default watermark.</Text>
                    </Paragraph>
                </Section>
            </Document>
            """;

        String js = compiler.compile(jsx);
        VNode vnode = runtime.run(js);
        Path output = tempDir.resolve("default-watermark.docx");
        
        renderer.renderToDocx(vnode, output.toString());
        
        assertTrue(java.nio.file.Files.exists(output));
        
        try (XWPFDocument doc = new XWPFDocument(java.nio.file.Files.newInputStream(output))) {
            List<XWPFHeader> headers = doc.getHeaderList();
            assertFalse(headers.isEmpty(), "Document should have headers for watermark");
        }
        
        System.out.println("Default watermark test passed: " + output);
    }
}
