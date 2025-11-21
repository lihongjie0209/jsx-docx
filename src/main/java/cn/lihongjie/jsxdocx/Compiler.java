package cn.lihongjie.jsxdocx;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jSourceMapOption;
import com.caoccao.javet.swc4j.options.Swc4jJsxRuntimeOption;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.net.URL;

public class Compiler {

    public String compile(String jsxCode) throws Exception {
        Swc4j swc4j = new Swc4j();

        URL specifier = new URL("file:///main.jsx");

        // Use Classic runtime with custom factories to avoid global React.
        // This prevents SWC from emitting ESM imports (which GraalJS can't eval)
        // while still allowing concise JSX usage in tests.
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
    }
}
