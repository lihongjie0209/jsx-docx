package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.exception.ComponentValidationException;
import cn.lihongjie.jsxdocx.exception.JsxRuntimeException;
import cn.lihongjie.jsxdocx.exception.JsxSyntaxException;
import cn.lihongjie.jsxdocx.mcp.McpServer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

@Command(
        name = "jsx-docx",
        mixinStandardHelpOptions = true,
        version = "jsx-docx 0.2.0",
        description = "Convert JSX document(s) to DOCX. Supports single file or batch conversion."
)
public class Main implements Callable<Integer> {

    @Parameters(arity = "0..*", description = "Input JSX file path(s). For batch conversion, provide multiple files.")
    private List<File> inputs;

    @Option(names = {"-o", "--output"}, description = "Output DOCX file path (single file mode only). If omitted uses input basename.")
    private File output;

    @Option(names = {"-d", "--output-dir"}, description = "Output directory for batch mode. Files use input basename + .docx extension.")
    private File outputDir;

    @Option(names = {"--data"}, description = "Path to JSON file to use as data context in JSX (accessible as 'data' global variable)")
    private File dataFile;

    @Option(names = "--stdin", description = "Read JSX content from standard input")
    private boolean stdin;

    @Option(names = "--verbose", description = "Enable verbose output")
    private boolean verbose;

    @Option(names = "--progress", description = "Show progress bar for batch conversion (default: true)", defaultValue = "true")
    private boolean progress;

    @Option(names = "--no-progress", description = "Disable progress bar")
    private boolean noProgress;

    @Option(names = "--report", description = "Generate JSON report file with conversion results")
    private File reportFile;

    @Option(names = {"--mcp", "--mcp-stdio"}, description = "Run in MCP (Model Context Protocol) stdio mode")
    private boolean mcpStdio;

    @Option(names = "--mcp-server", description = "Run in MCP server mode (HTTP with SSE)")
    private boolean mcpServer;

    @Option(names = "--mcp-port", description = "Port for MCP server mode (default: 3000)", defaultValue = "3000")
    private int mcpPort;

    @Override
    public Integer call() {
        // MCP mode takes precedence
        if (mcpStdio || mcpServer) {
            return runMcpMode();
        }

        // Handle stdin mode
        if (stdin) {
            return processStdin();
        }

        // Require inputs for normal mode
        if (inputs == null || inputs.isEmpty()) {
            System.err.println("Error: No input files specified.");
            System.err.println("Usage: jsx-docx [OPTIONS] <input-files...>");
            System.err.println("   or: jsx-docx --stdin [-o output.docx]");
            System.err.println("   or: jsx-docx --mcp-stdio");
            System.err.println("   or: jsx-docx --mcp-server [--mcp-port=3000]");
            return 2;
        }

        // Validate options
        if (inputs.size() > 1 && output != null) {
            System.err.println("Error: --output (-o) can only be used with a single input file.");
            System.err.println("For batch mode, use --output-dir (-d) instead.");
            return 4;
        }

        // Load data context if provided
        Map<String, Object> dataContext = null;
        if (dataFile != null) {
            try {
                dataContext = loadJsonFile(dataFile);
                if (verbose) {
                    System.out.println("Loaded data context from: " + dataFile.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error loading data file: " + e.getMessage());
                return 3;
            }
        }

        // Initialize report tracking
        List<ConversionResult> results = new ArrayList<>();
        boolean shouldShowProgress = !noProgress && progress && inputs.size() > 1 && !verbose;
        
        if (inputs.size() > 1 && verbose) {
            System.out.println("Batch mode: converting " + inputs.size() + " files...");
        }

        int successCount = 0;
        int failureCount = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < inputs.size(); i++) {
            File input = inputs.get(i);
            long fileStartTime = System.currentTimeMillis();
            try {
                if (!input.exists()) {
                    String errorMsg = "Input file not found";
                    if (!shouldShowProgress) {
                        System.err.println("✗ " + input.getName() + ": " + errorMsg);
                    }
                    if (reportFile != null) {
                        results.add(new ConversionResult(input.getName(), "error", null, errorMsg, 
                            System.currentTimeMillis() - fileStartTime));
                    }
                    failureCount++;
                    if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
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
                var vDom = runtime.run(jsCode, dataContext);
                
                if (verbose) {
                    System.out.println("[" + input.getName() + "] Rendering DOCX to " + outFile.getAbsolutePath());
                }
                Renderer renderer = new Renderer();
                renderer.renderToDocx(vDom, outFile.getAbsolutePath(), inPath, dataContext);
                
                long elapsed = System.currentTimeMillis() - fileStartTime;
                if (!shouldShowProgress) {
                    System.out.println("✓ Generated: " + outFile.getAbsolutePath());
                }
                
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "success", outFile.getAbsolutePath(), 
                        null, elapsed));
                }
                successCount++;
                
                if (shouldShowProgress) {
                    showProgress(i + 1, inputs.size(), input.getName());
                }
                
            } catch (JsxSyntaxException jse) {
                long elapsed = System.currentTimeMillis() - fileStartTime;
                if (!shouldShowProgress) {
                    System.err.println("\n" + "=".repeat(80));
                    System.err.println("✗ [" + input.getName() + "]");
                    System.err.println(jse.getMessage());
                    System.err.println("=".repeat(80));
                }
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "error", null, 
                        "JSX Syntax Error: " + jse.getErrorMessage(), elapsed));
                }
                failureCount++;
                if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
            } catch (JsxRuntimeException jre) {
                long elapsed = System.currentTimeMillis() - fileStartTime;
                if (!shouldShowProgress) {
                    System.err.println("\n" + "=".repeat(80));
                    System.err.println("✗ [" + input.getName() + "]");
                    System.err.println(jre.getMessage());
                    System.err.println("=".repeat(80));
                }
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "error", null, 
                        "Runtime Error: " + jre.getMessage(), elapsed));
                }
                failureCount++;
                if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
            } catch (ComponentValidationException cve) {
                long elapsed = System.currentTimeMillis() - fileStartTime;
                if (!shouldShowProgress) {
                    System.err.println("\n" + "=".repeat(80));
                    System.err.println("✗ [" + input.getName() + "]");
                    System.err.println(cve.getMessage());
                    System.err.println("=".repeat(80));
                }
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "error", null, 
                        "Component Error: " + cve.getMessage(), elapsed));
                }
                failureCount++;
                if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
            } catch (IOException ioe) {
                long elapsed = System.currentTimeMillis() - fileStartTime;
                String errorMsg = "I/O error: " + ioe.getMessage();
                if (!shouldShowProgress) {
                    System.err.println("✗ [" + input.getName() + "] " + errorMsg);
                }
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "error", null, errorMsg, elapsed));
                }
                failureCount++;
                if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
            } catch (Exception e) {
                long elapsed = System.currentTimeMillis() - fileStartTime;
                String errorMsg = e.getMessage();
                if (!shouldShowProgress) {
                    System.err.println("✗ [" + input.getName() + "] Failed: " + errorMsg);
                }
                if (verbose && !shouldShowProgress) e.printStackTrace();
                if (reportFile != null) {
                    results.add(new ConversionResult(input.getName(), "error", null, errorMsg, elapsed));
                }
                failureCount++;
                if (shouldShowProgress) showProgress(i + 1, inputs.size(), input.getName());
            }
        }

        // Clear progress line if shown
        if (shouldShowProgress) {
            System.out.print("\r" + " ".repeat(80) + "\r");
        }

        // Summary for batch mode
        if (inputs.size() > 1) {
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("\nBatch conversion complete: " + successCount + " succeeded, " + failureCount + " failed. (" + totalTime + "ms)");
        }

        // Generate report if requested
        if (reportFile != null) {
            try {
                generateReport(results, successCount, failureCount, reportFile);
                System.out.println("Report generated: " + reportFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error generating report: " + e.getMessage());
            }
        }

        return failureCount > 0 ? 1 : 0;
    }

    /**
     * Run MCP (Model Context Protocol) mode
     */
    private Integer runMcpMode() {
        if (mcpStdio) {
            if (verbose) {
                System.err.println("Starting MCP stdio mode...");
            }
            McpServer server = new McpServer();
            server.runStdioMode();
            return 0;
        } else if (mcpServer) {
            if (verbose) {
                System.err.println("Starting MCP server mode on port " + mcpPort + "...");
            }
            McpServer server = new McpServer();
            server.runServerMode(mcpPort);
            return 0;
        }
        return 0;
    }

    /**
     * Load JSON file and return as Map<String, Object>
     */
    private Map<String, Object> loadJsonFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, Map.class);
    }

    /**
     * Process JSX from stdin
     */
    private Integer processStdin() {
        try {
            // Read from stdin
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            String jsxContent = sb.toString();
            
            if (jsxContent.trim().isEmpty()) {
                System.err.println("Error: No input received from stdin");
                return 2;
            }

            // Load data context if provided
            Map<String, Object> dataContext = null;
            if (dataFile != null) {
                try {
                    dataContext = loadJsonFile(dataFile);
                    if (verbose) {
                        System.err.println("Loaded data context from: " + dataFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.err.println("Error loading data file: " + e.getMessage());
                    return 3;
                }
            }

            // Determine output file
            File outFile = (output != null) ? output : new File("output.docx");

            if (verbose) {
                System.err.println("Compiling JSX from stdin (length=" + jsxContent.length() + ")...");
            }
            Compiler compiler = new Compiler();
            String jsCode = compiler.compile(jsxContent);

            if (verbose) {
                System.err.println("Executing JS runtime...");
            }
            JsRuntime runtime = new JsRuntime();
            var vDom = runtime.run(jsCode, dataContext);

            if (verbose) {
                System.err.println("Rendering DOCX to " + outFile.getAbsolutePath());
            }
            Renderer renderer = new Renderer();
            renderer.renderToDocx(vDom, outFile.getAbsolutePath(), null, dataContext);

            System.out.println("✓ Generated: " + outFile.getAbsolutePath());
            return 0;

        } catch (JsxSyntaxException jse) {
            System.err.println("\n" + "=".repeat(80));
            System.err.println("✗ JSX Syntax Error:");
            System.err.println(jse.getMessage());
            System.err.println("=".repeat(80));
            return 1;
        } catch (JsxRuntimeException jre) {
            System.err.println("\n" + "=".repeat(80));
            System.err.println("✗ Runtime Error:");
            System.err.println(jre.getMessage());
            System.err.println("=".repeat(80));
            return 1;
        } catch (ComponentValidationException cve) {
            System.err.println("\n" + "=".repeat(80));
            System.err.println("✗ Component Validation Error:");
            System.err.println(cve.getMessage());
            System.err.println("=".repeat(80));
            return 1;
        } catch (IOException ioe) {
            System.err.println("✗ I/O error: " + ioe.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("✗ Failed: " + e.getMessage());
            if (verbose) e.printStackTrace();
            return 1;
        }
    }

    /**
     * Show progress bar for batch conversion
     */
    private void showProgress(int current, int total, String currentFile) {
        int percent = (current * 100) / total;
        int barLength = 30;
        int filled = (current * barLength) / total;
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "=" : " ");
        }
        bar.append("]");
        
        String progress = String.format("\rConverting: %s %d/%d (%d%%) - %s", 
            bar.toString(), current, total, percent, currentFile);
        
        // Truncate filename if too long
        if (progress.length() > 80) {
            String truncatedFile = currentFile.length() > 20 ? 
                "..." + currentFile.substring(currentFile.length() - 17) : currentFile;
            progress = String.format("\rConverting: %s %d/%d (%d%%) - %s", 
                bar.toString(), current, total, percent, truncatedFile);
        }
        
        System.out.print(progress);
        System.out.flush();
    }

    /**
     * Generate JSON report
     */
    private void generateReport(List<ConversionResult> results, int successCount, 
                                 int failureCount, File reportFile) throws IOException {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("total", results.size());
        report.put("success", successCount);
        report.put("failed", failureCount);
        report.put("results", results);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(reportFile, report);
    }

    /**
     * Conversion result for reporting
     */
    private static class ConversionResult {
        public String file;
        public String status;
        public String output;
        public String error;
        public long time_ms;

        public ConversionResult(String file, String status, String output, String error, long time_ms) {
            this.file = file;
            this.status = status;
            this.output = output;
            this.error = error;
            this.time_ms = time_ms;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
