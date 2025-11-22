package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test custom function components support
 * Verifies that function components can be defined and used like React components
 */
public class CustomComponentTest {

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
    public void simpleCustomComponent(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const Greeting = ({ name }) => {
                  return <Text>Hello, {name}!</Text>;
                };
                
                <Document>
                  <Paragraph>
                    <Greeting name="World" />
                  </Paragraph>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        assertEquals("document", vnode.getType());
        
        // Should render successfully
        Path output = tempDir.resolve("simple-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
        assertTrue(output.toFile().length() > 0);
    }

    @Test
    public void customComponentWithMultipleProps(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const StyledText = ({ content, bold, size }) => {
                  return <Text bold={bold} size={size}>{content}</Text>;
                };
                
                <Document>
                  <Paragraph>
                    <StyledText content="Important" bold={true} size={16} />
                  </Paragraph>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        
        Path output = tempDir.resolve("styled-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
    }

    @Test
    public void nestedCustomComponents(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const BoldText = ({ children }) => {
                  return <Text bold={true}>{children}</Text>;
                };
                
                const Paragraph1 = ({ children }) => {
                  return <Paragraph>{children}</Paragraph>;
                };
                
                <Document>
                  <Paragraph1>
                    <BoldText>This is bold text</BoldText>
                  </Paragraph1>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        
        Path output = tempDir.resolve("nested-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
    }

    @Test
    public void customComponentWithNumbersInText(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const MultiCell = ({ row, col }) => {
                  const result = row * col;
                  return (
                    <Cell>
                      <Paragraph>
                        <Text>{row}×{col}={result}</Text>
                      </Paragraph>
                    </Cell>
                  );
                };
                
                <Document>
                  <Table border={{ size: 1, color: "#000000" }}>
                    <Row>
                      <MultiCell row={1} col={1} />
                      <MultiCell row={1} col={2} />
                      <MultiCell row={1} col={3} />
                    </Row>
                  </Table>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        assertEquals("document", vnode.getType());
        
        Path output = tempDir.resolve("number-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
    }

    @Test
    public void customComponentWithLoop(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const TableRow = ({ row }) => {
                  const cells = [];
                  for (let col = 1; col <= row; col++) {
                    const result = row * col;
                    cells.push(
                      <Cell key={col}>
                        <Paragraph>
                          <Text>{row}×{col}={result}</Text>
                        </Paragraph>
                      </Cell>
                    );
                  }
                  return <Row>{cells}</Row>;
                };
                
                <Document>
                  <Table border={{ size: 1, color: "#000000" }}>
                    <TableRow row={1} />
                    <TableRow row={2} />
                    <TableRow row={3} />
                  </Table>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        
        Path output = tempDir.resolve("loop-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
    }

    @Test
    public void customComponentWithConditional(@TempDir Path tempDir) throws Exception {
        String jsxSource = """
                const OptionalText = ({ show, content }) => {
                  if (show) {
                    return <Text bold={true}>{content}</Text>;
                  } else {
                    return <Text italic={true}>{content}</Text>;
                  }
                };
                
                <Document>
                  <Paragraph>
                    <OptionalText show={true} content="This is bold" />
                  </Paragraph>
                  <Paragraph>
                    <OptionalText show={false} content="This is italic" />
                  </Paragraph>
                </Document>
                """;

        String compiledJs = compiler.compile(jsxSource);
        VNode vnode = runtime.run(compiledJs);
        
        assertNotNull(vnode);
        
        Path output = tempDir.resolve("conditional-component.docx");
        renderer.renderToDocx(vnode, output.toString());
        assertTrue(output.toFile().exists());
    }
}
