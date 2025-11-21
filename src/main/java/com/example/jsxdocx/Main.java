package com.example.jsxdocx;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "jsx-docx",
        mixinStandardHelpOptions = true,
        version = "jsx-docx 1.0-SNAPSHOT",
        description = "Convert JSX document(s) to DOCX. Supports single file or batch conversion."
)
public class Main implements Callable<Integer> {

    @Parameters(arity = "1..*", description = "Input JSX file path(s). For batch conversion, provide multiple files.")
    private List<File> inputs;

    @Option(names = {"-o", "--output"}, description = "Output DOCX file path (single file mode only). If omitted uses input basename.")
    private File output;

    @Option(names = {"-d", "--output-dir"}, description = "Output directory for batch mode. Files use input basename + .docx extension.")
    private File outputDir;

    @Option(names = "--verbose", description = "Enable verbose output")
    private boolean verbose;

    @Override
    public Integer call() {
        // Validate options
        if (inputs.size() > 1 && output != null) {
            System.err.println("Error: --output (-o) can only be used with a single input file.");
            System.err.println("For batch mode, use --output-dir (-d) instead.");
            return 4;
        }

        if (inputs.size() > 1 && verbose) {
            System.out.println("Batch mode: converting " + inputs.size() + " files...");
        }

        int successCount = 0;
        int failureCount = 0;

        for (File input : inputs) {
            try {
                if (!input.exists()) {
                    System.err.println("Input file not found: " + input);
                    failureCount++;
                    continue;
                }

                Path inPath = input.toPath();
                String effectiveJsx = Files.readString(inPath);

                // Determine output file
                File outFile;
                if (inputs.size() == 1 && output != null) {
                    // Single file mode with explicit output
                    outFile = output;
                } else {
                    // Batch mode or single file with auto-naming
                    String base = input.getName();
                    int dot = base.lastIndexOf('.');
                    if (dot != -1) base = base.substring(0, dot);
                    String outName = base + ".docx";

                    if (outputDir != null) {
                        if (!outputDir.exists()) {
                            outputDir.mkdirs();
                        }
                        outFile = new File(outputDir, outName);
                    } else {
                        outFile = new File(outName);
                    }
                }

                if (verbose) {
                    System.out.println("[" + input.getName() + "] Compiling JSX (length=" + effectiveJsx.length() + ")...");
                }
                Compiler compiler = new Compiler();
                String jsCode = compiler.compile(effectiveJsx);
                
                if (verbose) {
                    System.out.println("[" + input.getName() + "] Executing JS runtime...");
                }
                JsRuntime runtime = new JsRuntime();
                var vDom = runtime.run(jsCode);
                
                if (verbose) {
                    System.out.println("[" + input.getName() + "] Rendering DOCX to " + outFile.getAbsolutePath());
                }
                Renderer renderer = new Renderer();
                renderer.renderToDocx(vDom, outFile.getAbsolutePath());
                
                System.out.println("✓ Generated: " + outFile.getAbsolutePath());
                successCount++;
                
            } catch (IOException ioe) {
                System.err.println("✗ [" + input.getName() + "] I/O error: " + ioe.getMessage());
                failureCount++;
            } catch (Exception e) {
                System.err.println("✗ [" + input.getName() + "] Failed: " + e.getMessage());
                if (verbose) e.printStackTrace();
                failureCount++;
            }
        }

        // Summary for batch mode
        if (inputs.size() > 1) {
            System.out.println("\nBatch conversion complete: " + successCount + " succeeded, " + failureCount + " failed.");
        }

        return failureCount > 0 ? 1 : 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
