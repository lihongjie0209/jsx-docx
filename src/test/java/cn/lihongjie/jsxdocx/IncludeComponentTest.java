package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class IncludeComponentTest {

    @TempDir
    Path tempDir;

    @Test
    public void testBasicInclude() throws Exception {
        // Create a reusable component file
        Path componentFile = tempDir.resolve("component.jsx");
        Files.writeString(componentFile, """
                <Document>
                  <Paragraph bold="true">
                    <Text>Included Content</Text>
                  </Paragraph>
                </Document>
                """);

        // Create main file that includes the component
        Path mainFile = tempDir.resolve("main.jsx");
        Files.writeString(mainFile, """
                render(
                  <Document>
                    <Paragraph><Text>Before Include</Text></Paragraph>
                    <Include path="./component.jsx" />
                    <Paragraph><Text>After Include</Text></Paragraph>
                  </Document>
                );
                """);

        // Compile and render
        String jsx = Files.readString(mainFile);
        String js = new Compiler().compile(jsx);
        VNode vdom = new JsRuntime().run(js);
        
        Path outputPath = tempDir.resolve("output.docx");
        new Renderer().renderToDocx(vdom, outputPath.toString(), mainFile, null);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    public void testNestedInclude() throws Exception {
        // Create innermost component
        Path innerFile = tempDir.resolve("inner.jsx");
        Files.writeString(innerFile, """
                <Document>
                  <Paragraph><Text>Inner Component</Text></Paragraph>
                </Document>
                """);

        // Create middle component that includes inner
        Path middleFile = tempDir.resolve("middle.jsx");
        Files.writeString(middleFile, """
                <Document>
                  <Paragraph><Text>Middle Start</Text></Paragraph>
                  <Include path="./inner.jsx" />
                  <Paragraph><Text>Middle End</Text></Paragraph>
                </Document>
                """);

        // Create main file that includes middle
        Path mainFile = tempDir.resolve("main.jsx");
        Files.writeString(mainFile, """
                render(
                  <Document>
                    <Paragraph><Text>Main Start</Text></Paragraph>
                    <Include path="./middle.jsx" />
                    <Paragraph><Text>Main End</Text></Paragraph>
                  </Document>
                );
                """);

        // Compile and render
        String jsx = Files.readString(mainFile);
        String js = new Compiler().compile(jsx);
        VNode vdom = new JsRuntime().run(js);
        
        Path outputPath = tempDir.resolve("nested-output.docx");
        new Renderer().renderToDocx(vdom, outputPath.toString(), mainFile, null);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }

    @Test
    public void testCircularIncludeDetection() throws Exception {
        // Create file A that includes B
        Path fileA = tempDir.resolve("a.jsx");
        Files.writeString(fileA, """
                <Document>
                  <Paragraph><Text>File A</Text></Paragraph>
                  <Include path="./b.jsx" />
                </Document>
                """);

        // Create file B that includes A (circular dependency)
        Path fileB = tempDir.resolve("b.jsx");
        Files.writeString(fileB, """
                <Document>
                  <Paragraph><Text>File B</Text></Paragraph>
                  <Include path="./a.jsx" />
                </Document>
                """);

        // Create main file
        Path mainFile = tempDir.resolve("main.jsx");
        Files.writeString(mainFile, """
                render(
                  <Document>
                    <Include path="./a.jsx" />
                  </Document>
                );
                """);

        // This should throw an exception due to circular dependency
        String jsx = Files.readString(mainFile);
        String js = new Compiler().compile(jsx);
        VNode vdom = new JsRuntime().run(js);
        
        Path outputPath = tempDir.resolve("circular-output.docx");
        
        assertThrows(IllegalStateException.class, () -> {
            new Renderer().renderToDocx(vdom, outputPath.toString(), mainFile, null);
        });
    }

    @Test
    public void testMissingIncludeFile() throws Exception {
        Path mainFile = tempDir.resolve("main.jsx");
        Files.writeString(mainFile, """
                render(
                  <Document>
                    <Paragraph><Text>Before Include</Text></Paragraph>
                    <Include path="./nonexistent.jsx" />
                    <Paragraph><Text>After Include</Text></Paragraph>
                  </Document>
                );
                """);

        // Should not throw, but should print error message and continue
        String jsx = Files.readString(mainFile);
        String js = new Compiler().compile(jsx);
        VNode vdom = new JsRuntime().run(js);
        
        Path outputPath = tempDir.resolve("missing-output.docx");
        new Renderer().renderToDocx(vdom, outputPath.toString(), mainFile, null);

        // Document should still be created (with error message printed to stderr)
        assertTrue(Files.exists(outputPath));
    }

    @Test
    public void testRelativePathResolution() throws Exception {
        // Create subdirectory structure
        Path subdir = tempDir.resolve("components");
        Files.createDirectory(subdir);

        Path componentFile = subdir.resolve("header.jsx");
        Files.writeString(componentFile, """
                <Document>
                  <Paragraph bold="true"><Text>Header Component</Text></Paragraph>
                </Document>
                """);

        Path mainFile = tempDir.resolve("main.jsx");
        Files.writeString(mainFile, """
                render(
                  <Document>
                    <Include path="./components/header.jsx" />
                    <Paragraph><Text>Main Content</Text></Paragraph>
                  </Document>
                );
                """);

        String jsx = Files.readString(mainFile);
        String js = new Compiler().compile(jsx);
        VNode vdom = new JsRuntime().run(js);
        
        Path outputPath = tempDir.resolve("relative-output.docx");
        new Renderer().renderToDocx(vdom, outputPath.toString(), mainFile, null);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.size(outputPath) > 0);
    }
}
