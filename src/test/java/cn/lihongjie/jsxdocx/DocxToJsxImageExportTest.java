package cn.lihongjie.jsxdocx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for image export functionality in DocxToJsx.
 * Tests the ability to export images to a folder instead of embedding as base64.
 */
public class DocxToJsxImageExportTest {

    @TempDir
    Path tempDir;

    private final Compiler compiler = new Compiler();
    private final JsRuntime runtime = new JsRuntime();
    private final Renderer renderer = new Renderer();

    /**
     * Helper method to create a DOCX with an embedded image
     */
    private Path createDocxWithImage() throws Exception {
        // Create simple image data (1x1 PNG)
        byte[] pngData = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53,
            (byte)0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
            0x54, 0x08, (byte)0xD7, 0x63, (byte)0xF8, (byte)0xCF,
            (byte)0xC0, 0x00, 0x00, 0x03, 0x01, 0x01, 0x00, 0x18,
            (byte)0xDD, (byte)0x8D, (byte)0xB4, 0x00, 0x00, 0x00,
            0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60,
            (byte)0x82
        };

        // Save PNG to temp file
        Path pngPath = tempDir.resolve("test-image.png");
        Files.write(pngPath, pngData);

        // Create JSX with image
        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Text>Document with image:</Text>
                </Paragraph>
                <Paragraph>
                  <Image src="%s" width={100} height={100} />
                </Paragraph>
              </Section>
            </Document>
            """.formatted(pngPath.toString().replace("\\", "\\\\"));

        // Generate DOCX
        Path docxPath = tempDir.resolve("test-with-image.docx");
        String jsCode = compiler.compile(jsx);
        var vdom = runtime.run(jsCode, null);
        renderer.renderToDocx(vdom, docxPath.toString(), tempDir, null);

        return docxPath;
    }

    @Test
    void testImageExportDefault_EmbeddedBase64() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Convert without image export directory (default behavior)
        String jsxResult = DocxToJsx.convert(docxPath.toString(), null);

        // Verify image is embedded as base64
        assertTrue(jsxResult.contains("data:image/png;base64,"), 
            "Should contain base64-encoded image");
        assertFalse(jsxResult.contains("image-1.png"), 
            "Should not contain file path reference");

        System.out.println("✓ Default behavior: Images embedded as base64");
    }

    @Test
    void testImageExportToDirectory_FileReference() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Create image export directory
        Path imageExportDir = tempDir.resolve("exported-images");

        // Convert with image export directory
        String jsxResult = DocxToJsx.convert(docxPath.toString(), imageExportDir);

        // Verify image is referenced by file path
        assertFalse(jsxResult.contains("data:image/png;base64,"), 
            "Should not contain base64 data URI");
        assertTrue(jsxResult.contains("image-1.png"), 
            "Should contain file path reference");

        // Verify image file was created
        Path exportedImage = imageExportDir.resolve("image-1.png");
        assertTrue(Files.exists(exportedImage), 
            "Image file should be exported: " + exportedImage);
        assertTrue(Files.size(exportedImage) > 0, 
            "Exported image should have content");

        System.out.println("✓ Image exported to: " + exportedImage);
        System.out.println("✓ JSX references image by path");
    }

    @Test
    void testImageExportDirectory_CreatedAutomatically() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Use non-existent directory
        Path imageExportDir = tempDir.resolve("auto-created-dir");
        assertFalse(Files.exists(imageExportDir), "Directory should not exist yet");

        // Convert - should create directory automatically
        String jsxResult = DocxToJsx.convert(docxPath.toString(), imageExportDir);

        // Verify directory was created
        assertTrue(Files.exists(imageExportDir), 
            "Export directory should be created automatically");
        assertTrue(Files.isDirectory(imageExportDir), 
            "Path should be a directory");

        // Verify image was exported
        Path exportedImage = imageExportDir.resolve("image-1.png");
        assertTrue(Files.exists(exportedImage), 
            "Image should be exported to auto-created directory");

        System.out.println("✓ Export directory created automatically");
    }

    @Test
    void testMultipleImages_UniqueFilenames() throws Exception {
        // Create JSX with multiple images
        byte[] pngData = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53,
            (byte)0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
            0x54, 0x08, (byte)0xD7, 0x63, (byte)0xF8, (byte)0xCF,
            (byte)0xC0, 0x00, 0x00, 0x03, 0x01, 0x01, 0x00, 0x18,
            (byte)0xDD, (byte)0x8D, (byte)0xB4, 0x00, 0x00, 0x00,
            0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60,
            (byte)0x82
        };

        Path pngPath = tempDir.resolve("test-image.png");
        Files.write(pngPath, pngData);

        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Image src="%s" width={100} height={100} />
                </Paragraph>
                <Paragraph>
                  <Image src="%s" width={100} height={100} />
                </Paragraph>
                <Paragraph>
                  <Image src="%s" width={100} height={100} />
                </Paragraph>
              </Section>
            </Document>
            """.formatted(
                pngPath.toString().replace("\\", "\\\\"),
                pngPath.toString().replace("\\", "\\\\"),
                pngPath.toString().replace("\\", "\\\\")
            );

        // Generate DOCX
        Path docxPath = tempDir.resolve("test-multiple-images.docx");
        String jsCode = compiler.compile(jsx);
        var vdom = runtime.run(jsCode, null);
        renderer.renderToDocx(vdom, docxPath.toString(), tempDir, null);

        // Convert with image export
        Path imageExportDir = tempDir.resolve("multiple-images");
        String jsxResult = DocxToJsx.convert(docxPath.toString(), imageExportDir);

        // Verify unique filenames
        assertTrue(jsxResult.contains("image-1.png"), "Should have image-1.png");
        assertTrue(jsxResult.contains("image-2.png"), "Should have image-2.png");
        assertTrue(jsxResult.contains("image-3.png"), "Should have image-3.png");

        // Verify all files were created
        assertTrue(Files.exists(imageExportDir.resolve("image-1.png")));
        assertTrue(Files.exists(imageExportDir.resolve("image-2.png")));
        assertTrue(Files.exists(imageExportDir.resolve("image-3.png")));

        System.out.println("✓ Multiple images exported with unique filenames");
    }

    @Test
    void testImageExport_RoundTrip() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Convert to JSX with image export
        Path imageExportDir = tempDir.resolve("export-round-trip");
        String jsxResult = DocxToJsx.convert(docxPath.toString(), imageExportDir);

        // Verify image was exported
        Path exportedImage = imageExportDir.resolve("image-1.png");
        assertTrue(Files.exists(exportedImage), "Image should be exported");

        // Try to convert JSX back to DOCX
        Path newDocxPath = tempDir.resolve("round-trip-result.docx");
        String jsCode = compiler.compile(jsxResult);
        var vdom = runtime.run(jsCode, null);
        renderer.renderToDocx(vdom, newDocxPath.toString(), imageExportDir, null);

        // Verify new DOCX was created
        assertTrue(Files.exists(newDocxPath), "Round-trip DOCX should be created");
        assertTrue(Files.size(newDocxPath) > 0, "Round-trip DOCX should have content");

        System.out.println("✓ Round-trip conversion successful");
        System.out.println("  Original DOCX: " + Files.size(docxPath) + " bytes");
        System.out.println("  Round-trip DOCX: " + Files.size(newDocxPath) + " bytes");
    }

    @Test
    void testConvertToFile_WithImageExport() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Convert to JSX file with image export
        Path jsxPath = tempDir.resolve("output.jsx");
        Path imageExportDir = tempDir.resolve("export-to-file");

        DocxToJsx.convertToFile(docxPath.toString(), jsxPath.toString(), imageExportDir);

        // Verify JSX file was created
        assertTrue(Files.exists(jsxPath), "JSX file should be created");
        String jsxContent = Files.readString(jsxPath);

        // Verify content has file references
        assertTrue(jsxContent.contains("image-1.png"), 
            "JSX file should contain image file reference");
        assertFalse(jsxContent.contains("base64"), 
            "JSX file should not contain base64 encoding");

        // Verify image was exported
        assertTrue(Files.exists(imageExportDir.resolve("image-1.png")), 
            "Image should be exported");

        System.out.println("✓ convertToFile() with image export works correctly");
    }

    @Test
    void testImageExport_BackwardCompatibility() throws Exception {
        // Create DOCX with image
        Path docxPath = createDocxWithImage();

        // Test original convert() method (without image export parameter)
        String jsxResult1 = DocxToJsx.convert(docxPath.toString());

        // Should use base64 by default
        assertTrue(jsxResult1.contains("data:image/png;base64,"), 
            "Default behavior should embed as base64");

        // Test original convertToFile() method
        Path jsxPath = tempDir.resolve("backward-compat.jsx");
        DocxToJsx.convertToFile(docxPath.toString(), jsxPath.toString());

        String jsxContent = Files.readString(jsxPath);
        assertTrue(jsxContent.contains("data:image/png;base64,"), 
            "Default convertToFile should embed as base64");

        System.out.println("✓ Backward compatibility maintained");
    }

    @Test
    void testImageExport_FileExtensions() throws Exception {
        // This test verifies that different image formats get correct extensions
        // For now, we'll just verify PNG works, but the code supports multiple formats
        
        Path docxPath = createDocxWithImage();
        Path imageExportDir = tempDir.resolve("extensions-test");
        
        String jsxResult = DocxToJsx.convert(docxPath.toString(), imageExportDir);
        
        // Should have .png extension for PNG images
        assertTrue(jsxResult.contains(".png"), "Should have .png extension");
        
        // Verify file was created with correct extension
        List<Path> exportedFiles = Files.list(imageExportDir)
            .collect(Collectors.toList());
        
        assertEquals(1, exportedFiles.size(), "Should have one exported image");
        assertTrue(exportedFiles.get(0).toString().endsWith(".png"), 
            "Exported file should have .png extension");

        System.out.println("✓ Image file extensions are correct");
    }
}
