package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.exception.ComponentValidationException;
import cn.lihongjie.jsxdocx.exception.JsxRuntimeException;
import cn.lihongjie.jsxdocx.exception.JsxSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test improved error messages for JSX syntax, runtime, and component validation errors
 */
public class ErrorHandlingTest {

    @TempDir
    Path tempDir;

    @Test
    public void testJsxSyntaxError_UnclosedTag() {
        // JSX with unclosed tag
        String badJsx = "<Document><Section><Paragraph>Hello";
        
        Compiler compiler = new Compiler();
        JsxSyntaxException exception = assertThrows(JsxSyntaxException.class, () -> {
            compiler.compile(badJsx);
        });
        
        // Verify error message contains helpful information
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("JSX Syntax Error"), "Should mention JSX Syntax Error");
        assertTrue(message.contains("Documentation:"), "Should include documentation link");
    }

    @Test
    public void testJsxSyntaxError_InvalidJavaScript() {
        // JSX with invalid JavaScript syntax
        String badJsx = "<Document>{const x = }</Document>";
        
        Compiler compiler = new Compiler();
        assertThrows(JsxSyntaxException.class, () -> {
            compiler.compile(badJsx);
        });
    }

    @Test
    public void testRuntimeError_UndefinedVariable() throws Exception {
        // JSX referencing undefined variable
        String jsx = "<Document><Section><Paragraph><Text>{undefinedVar}</Text></Paragraph></Section></Document>";
        
        Compiler compiler = new Compiler();
        String compiled = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        JsxRuntimeException exception = assertThrows(JsxRuntimeException.class, () -> {
            runtime.run(compiled);
        });
        
        // Verify error message is helpful
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("Runtime Error"), "Should mention Runtime Error");
        assertTrue(message.contains("💡"), "Should include suggestion");
        assertTrue(message.contains("Documentation:"), "Should include documentation link");
    }

    @Test
    public void testRuntimeError_NoDocumentReturned() throws Exception {
        // JSX that doesn't return anything
        String jsx = "const x = 5;";
        
        Compiler compiler = new Compiler();
        String compiled = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        JsxRuntimeException exception = assertThrows(JsxRuntimeException.class, () -> {
            runtime.run(compiled);
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("No document returned"), "Should mention no document");
        assertTrue(message.contains("<Document>"), "Should suggest Document element");
    }

    @Test
    public void testComponentValidationError_InvalidRootElement() throws Exception {
        // JSX with non-Document root
        String jsx = "<Section><Paragraph>Test</Paragraph></Section>";
        
        Compiler compiler = new Compiler();
        String compiled = compiler.compile(jsx);
        
        JsRuntime runtime = new JsRuntime();
        var vdom = runtime.run(compiled);
        
        Renderer renderer = new Renderer();
        String outputPath = tempDir.resolve("output.docx").toString();
        
        ComponentValidationException exception = assertThrows(ComponentValidationException.class, () -> {
            renderer.renderToDocx(vdom, outputPath);
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("Component Validation Error"), "Should mention validation error");
        assertTrue(message.contains("Root"), "Should mention root component");
        assertTrue(message.contains("<Document>"), "Should mention Document requirement");
    }

    @Test
    public void testJsxSyntaxException_LineAndColumnInfo() {
        // Test that line and column information is properly formatted
        String errorMsg = "Unexpected token";
        int line = 5;
        int column = 10;
        String sourceCode = "line1\nline2\nline3\nline4\nline5\nline6";
        
        JsxSyntaxException exception = new JsxSyntaxException(errorMsg, line, column, sourceCode);
        
        String message = exception.getMessage();
        assertTrue(message.contains("line 5"), "Should show line number");
        assertTrue(message.contains("column 10"), "Should show column number");
        assertTrue(message.contains("line5"), "Should show error line content");
        assertTrue(message.contains("→"), "Should show error indicator");
    }

    @Test
    public void testJsxRuntimeException_CustomSuggestion() {
        String errorMsg = "Cannot read property 'name' of null";
        String suggestion = "Add null check before accessing object properties";
        
        JsxRuntimeException exception = new JsxRuntimeException(errorMsg, suggestion);
        
        String message = exception.getMessage();
        assertTrue(message.contains("💡"), "Should include suggestion icon");
        assertTrue(message.contains(suggestion), "Should include custom suggestion");
    }

    @Test
    public void testComponentValidationException_PropertyInfo() {
        String component = "Include";
        String property = "path";
        String error = "Missing required property";
        
        ComponentValidationException exception = new ComponentValidationException(
            component, property, error
        );
        
        String message = exception.getMessage();
        assertTrue(message.contains("<Include>"), "Should mention component");
        assertTrue(message.contains("'path'"), "Should mention property");
        assertTrue(message.contains("💡"), "Should include fix suggestion");
        assertTrue(message.contains("documentation:"), "Should include documentation link");
    }

    @Test
    public void testValidJsxCompiles() throws Exception {
        // Verify that valid JSX still works
        String validJsx = "<Document><Section><Paragraph><Text>Hello World</Text></Paragraph></Section></Document>";
        
        Compiler compiler = new Compiler();
        String compiled = compiler.compile(validJsx);
        assertNotNull(compiled);
        assertTrue(compiled.length() > 0);
        
        JsRuntime runtime = new JsRuntime();
        var vdom = runtime.run(compiled);
        assertNotNull(vdom);
        assertEquals("document", vdom.getType());
    }
}
