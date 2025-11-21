package cn.lihongjie.jsxdocx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsxDocxGenerator {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar jsx-docx.jar <input.jsx> <output.docx>");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        try {
            System.out.println("Reading " + inputPath + "...");
            String jsxCode = Files.readString(Paths.get(inputPath));

            System.out.println("Compiling JSX...");
            Compiler compiler = new Compiler();
            String jsCode = compiler.compile(jsxCode);
            // System.out.println("Compiled JS:\n" + jsCode);

            System.out.println("Executing JS...");
            JsRuntime runtime = new JsRuntime();
            var vDom = runtime.run(jsCode);

            System.out.println("Rendering to DOCX...");
            Renderer renderer = new Renderer();
            renderer.renderToDocx(vDom, outputPath, Paths.get(inputPath), null);

            System.out.println("Done! Saved to " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
