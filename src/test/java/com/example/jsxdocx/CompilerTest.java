package com.example.jsxdocx;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {
    @Test
    public void compilesJsxToJs() throws Exception {
        String jsx = "const x = <div>Hello</div>;";
        String js = new Compiler().compile(jsx);
        assertNotNull(js);
        assertFalse(js.isBlank());
    }
}
