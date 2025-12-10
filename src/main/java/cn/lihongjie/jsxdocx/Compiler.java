package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.exception.JsxSyntaxException;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.options.Swc4jJsxRuntimeOption;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.net.URL;

public class Compiler {

    public String compile(String jsxCode) throws JsxSyntaxException {
        try {
            Swc4j swc4j = new Swc4j();

            URL specifier = new URL("file:///main.jsx");

            // Use Classic runtime with custom factories to avoid global React.
            // This prevents SWC from emitting ESM imports which can't be eval'd
            // while still allowing concise JSX usage.
            Swc4jJsxRuntimeOption jsxOption = Swc4jJsxRuntimeOption.Classic()
                .setFactory("React.createElement")
                .setFragmentFactory("React.Fragment");

            Swc4jTranspileOptions options = new Swc4jTranspileOptions()
                    .setSpecifier(specifier)
                    .setMediaType(Swc4jMediaType.Jsx)
                    .setSourceMap(Swc4jSourceMapOption.None)
                    .setJsx(jsxOption);

            Swc4jTranspileOutput output = swc4j.transpile(jsxCode, options);
            return output.getCode();
        } catch (Exception e) {
            // Parse error message to extract line/column information
            String errorMsg = e.getMessage();
            int line = -1;
            int column = -1;
            
            // Try to extract line and column from SWC error message
            // SWC error format typically includes position information
            if (errorMsg != null) {
                // Pattern: "at line X, column Y" or similar
                try {
                    if (errorMsg.contains("line")) {
                        String[] parts = errorMsg.split("line");
                        if (parts.length > 1) {
                            String numberPart = parts[1].trim().split("[^0-9]")[0];
                            if (!numberPart.isEmpty()) {
                                line = Integer.parseInt(numberPart);
                            }
                        }
                    }
                    if (errorMsg.contains("column")) {
                        String[] parts = errorMsg.split("column");
                        if (parts.length > 1) {
                            String numberPart = parts[1].trim().split("[^0-9]")[0];
                            if (!numberPart.isEmpty()) {
                                column = Integer.parseInt(numberPart);
                            }
                        }
                    }
                } catch (Exception parseEx) {
                    // If parsing fails, just use -1 for line and column
                }
            }
            
            throw new JsxSyntaxException(
                errorMsg != null ? errorMsg : "Unknown syntax error",
                line,
                column,
                jsxCode
            );
        }
    }
}
