package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsRuntimeTest {
    @Test
    public void runsCompiledJsAndReturnsVNode() throws Exception {
        String jsx = """
                render(
                  <Document>
                    <Paragraph>Hello World</Paragraph>
                  </Document>
                );
                """;
        String js = new Compiler().compile(jsx);
        VNode node = new JsRuntime().run(js);
        assertNotNull(node);
        assertEquals("document", node.getType());
    }
}
