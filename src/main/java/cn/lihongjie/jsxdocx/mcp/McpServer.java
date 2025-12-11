package cn.lihongjie.jsxdocx.mcp;

import cn.lihongjie.jsxdocx.Compiler;
import cn.lihongjie.jsxdocx.DocxToJsx;
import cn.lihongjie.jsxdocx.JsRuntime;
import cn.lihongjie.jsxdocx.Renderer;
import cn.lihongjie.jsxdocx.model.VNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * MCP (Model Context Protocol) Server for jsx-docx
 * Supports both stdio and SSE (Server-Sent Events) modes
 */
public class McpServer {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String VERSION = "1.0.0";
    private final Compiler compiler = new Compiler();
    private final JsRuntime runtime = new JsRuntime();
    private final Renderer renderer = new Renderer();

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "stdio";
        
        McpServer server = new McpServer();
        
        if ("stdio".equals(mode)) {
            server.runStdioMode();
        } else if ("sse".equals(mode) || "server".equals(mode)) {
            int port = args.length > 1 ? Integer.parseInt(args[1]) : 3000;
            server.runServerMode(port);
        } else {
            System.err.println("Unknown mode: " + mode);
            System.err.println("Usage: java -jar jsx-docx.jar mcp [stdio|server] [port]");
            System.exit(1);
        }
    }

    /**
     * Run in stdio mode (JSON-RPC over stdin/stdout)
     */
    public void runStdioMode() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(System.out, true)) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JsonNode request = mapper.readTree(line);
                    JsonNode response = handleRequest(request);
                    writer.println(mapper.writeValueAsString(response));
                } catch (Exception e) {
                    JsonNode errorResponse = createErrorResponse(null, -32700, "Parse error: " + e.getMessage());
                    writer.println(mapper.writeValueAsString(errorResponse));
                }
            }
        } catch (IOException e) {
            System.err.println("Stdio mode error: " + e.getMessage());
        }
    }

    /**
     * Run in server mode (HTTP with SSE)
     */
    public void runServerMode(int port) {
        System.err.println("Server mode not yet implemented. Port: " + port);
        System.err.println("Use stdio mode for now: java -jar jsx-docx.jar mcp stdio");
        System.exit(1);
    }

    /**
     * Handle JSON-RPC request
     */
    private JsonNode handleRequest(JsonNode request) {
        String method = request.has("method") ? request.get("method").asText() : "";
        JsonNode id = request.get("id");
        JsonNode params = request.get("params");

        try {
            return switch (method) {
                case "initialize" -> handleInitialize(id, params);
                case "tools/list" -> handleToolsList(id);
                case "tools/call" -> handleToolsCall(id, params);
                case "resources/list" -> handleResourcesList(id);
                case "prompts/list" -> handlePromptsList(id);
                default -> createErrorResponse(id, -32601, "Method not found: " + method);
            };
        } catch (Exception e) {
            return createErrorResponse(id, -32603, "Internal error: " + e.getMessage());
        }
    }

    private JsonNode handleInitialize(JsonNode id, JsonNode params) {
        ObjectNode result = mapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        result.put("serverInfo", createServerInfo());
        
        ObjectNode capabilities = mapper.createObjectNode();
        ObjectNode tools = mapper.createObjectNode();
        tools.put("listChanged", false);
        capabilities.set("tools", tools);
        result.set("capabilities", capabilities);

        return createSuccessResponse(id, result);
    }

    private JsonNode handleToolsList(JsonNode id) {
        ArrayNode tools = mapper.createArrayNode();
        
        // Tool 1: get_component_spec
        ObjectNode specTool = mapper.createObjectNode();
        specTool.put("name", "get_component_spec");
        specTool.put("description", "⚠️ MANDATORY FIRST STEP: Get the complete jsx-docx component specification and syntax reference. " +
                "You MUST call this tool BEFORE generating any documents to understand available components, " +
                "their properties, and correct JSX syntax. Do NOT attempt to generate documents without reading the spec first. " +
                "The spec includes: Document structure, Styles system, Text formatting, Tables, Lists, Images, Headers/Footers, TOC, " +
                "and all component properties with correct syntax examples.");
        
        ObjectNode specSchema = mapper.createObjectNode();
        specSchema.put("type", "object");
        specSchema.set("properties", mapper.createObjectNode());
        specSchema.set("required", mapper.createArrayNode());
        
        specTool.set("inputSchema", specSchema);
        tools.add(specTool);
        
        // Tool 2: generate_docx
        ObjectNode docTool = mapper.createObjectNode();
        docTool.put("name", "generate_docx");
        docTool.put("description", "Generate a Word document (.docx) from JSX code or JSX file. " +
                "Supports all Word features including styles, tables, lists, images, headers/footers, TOC, etc. " +
                "\n\n💡 RECOMMENDED WORKFLOW: " +
                "1) Use docx_to_jsx to convert existing template to JSX file, " +
                "2) Review and modify the JSX file as needed, " +
                "3) Use this tool with jsxFilePath parameter to generate final DOCX. " +
                "This approach works better than passing large JSX code directly, especially for complex documents with images. " +
                "\n\nYou can either provide JSX code directly (jsxCode parameter) or a path to a JSX file (jsxFilePath parameter). " +
                "Using jsxFilePath is strongly recommended as it ensures correct path resolution for images and included files. " +
                "\n\n⚠️ PREREQUISITE REQUIRED: You MUST call get_component_spec FIRST to read the complete specification " +
                "before using this tool. Generating documents without reading the spec will result in syntax errors.");
        
        ObjectNode inputSchema = mapper.createObjectNode();
        inputSchema.put("type", "object");
        
        ObjectNode properties = mapper.createObjectNode();
        
        ObjectNode jsxCode = mapper.createObjectNode();
        jsxCode.put("type", "string");
        jsxCode.put("description", "JSX code defining the document structure. " +
                "Use <Document>, <Section>, <Paragraph>, <Text>, <Table>, etc. " +
                "Either jsxCode or jsxFilePath must be provided (not both).");
        properties.set("jsxCode", jsxCode);
        
        ObjectNode jsxFilePath = mapper.createObjectNode();
        jsxFilePath.put("type", "string");
        jsxFilePath.put("description", "Path to a JSX file to use as input. " +
                "Either jsxCode or jsxFilePath must be provided (not both).");
        properties.set("jsxFilePath", jsxFilePath);
        
        ObjectNode outputPath = mapper.createObjectNode();
        outputPath.put("type", "string");
        outputPath.put("description", "Output file path for the generated .docx file");
        properties.set("outputPath", outputPath);
        
        ObjectNode data = mapper.createObjectNode();
        data.put("type", "object");
        data.put("description", "Optional data context object to inject into JSX (accessible as 'data' global)");
        properties.set("data", data);
        
        inputSchema.set("properties", properties);
        ArrayNode required = mapper.createArrayNode();
        required.add("outputPath");
        inputSchema.set("required", required);
        
        docTool.set("inputSchema", inputSchema);
        tools.add(docTool);
        
        // Tool 3: docx_to_jsx
        ObjectNode reverseTool = mapper.createObjectNode();
        reverseTool.put("name", "docx_to_jsx");
        reverseTool.put("description", "Convert an existing Word document (.docx) to JSX code. " +
                "Use this tool when the user wants to reference an existing document as a template. " +
                "Workflow: 1) Convert the template docx to JSX, 2) Review and understand the structure, " +
                "3) Modify the JSX as needed (change text, add/remove sections, update styles), " +
                "4) Generate new document using generate_docx. This is ideal for creating documents " +
                "based on existing templates or corporate formats. " +
                "By default, images are exported to a directory as separate files. " +
                "Use embedImages=true to embed images as base64 data URIs instead.");
        
        ObjectNode reverseSchema = mapper.createObjectNode();
        reverseSchema.put("type", "object");
        
        ObjectNode reverseProperties = mapper.createObjectNode();
        
        ObjectNode docxPath = mapper.createObjectNode();
        docxPath.put("type", "string");
        docxPath.put("description", "Path to the existing .docx file to convert to JSX");
        reverseProperties.set("docxPath", docxPath);
        
        ObjectNode imageExportDir = mapper.createObjectNode();
        imageExportDir.put("type", "string");
        imageExportDir.put("description", "Optional directory path to export images. " +
                "If not provided and embedImages is false, images will be exported to an 'images' subdirectory " +
                "next to the output JSX file. This parameter is ignored if embedImages is true.");
        reverseProperties.set("imageExportDir", imageExportDir);
        
        ObjectNode outputJsxPath = mapper.createObjectNode();
        outputJsxPath.put("type", "string");
        outputJsxPath.put("description", "Optional output file path for the JSX code. " +
                "If provided, JSX code will be written to this file instead of being returned in the response. " +
                "This is recommended for large documents to avoid returning too much data.");
        reverseProperties.set("outputJsxPath", outputJsxPath);
        
        ObjectNode embedImages = mapper.createObjectNode();
        embedImages.put("type", "boolean");
        embedImages.put("description", "If true, embed images as base64 data URIs in JSX instead of exporting to files. " +
                "Default is false (export images to directory).");
        reverseProperties.set("embedImages", embedImages);
        
        reverseSchema.set("properties", reverseProperties);
        ArrayNode reverseRequired = mapper.createArrayNode();
        reverseRequired.add("docxPath");
        reverseSchema.set("required", reverseRequired);
        
        reverseTool.set("inputSchema", reverseSchema);
        tools.add(reverseTool);

        ObjectNode result = mapper.createObjectNode();
        result.set("tools", tools);
        
        return createSuccessResponse(id, result);
    }

    private JsonNode handleToolsCall(JsonNode id, JsonNode params) {
        String toolName = params.get("name").asText();
        JsonNode arguments = params.get("arguments");

        return switch (toolName) {
            case "get_component_spec" -> handleGetComponentSpec(id);
            case "generate_docx" -> handleGenerateDocx(id, arguments);
            case "docx_to_jsx" -> handleDocxToJsx(id, arguments);
            default -> createErrorResponse(id, -32602, "Unknown tool: " + toolName);
        };
    }

    private JsonNode handleGetComponentSpec(JsonNode id) {
        try {
            // Read spec.md - component specification
            String specContent;
            
            // Try to read from file system first (for development)
            Path specPath = Paths.get("docs/spec.md");
            if (Files.exists(specPath)) {
                specContent = Files.readString(specPath);
            } else {
                // Fallback to classpath resource (for packaged JAR)
                InputStream specStream = getClass().getClassLoader().getResourceAsStream("spec.md");
                if (specStream != null) {
                    specContent = new String(specStream.readAllBytes());
                } else {
                    // If not found, return a helpful error
                    ArrayNode content = mapper.createArrayNode();
                    ObjectNode textContent = mapper.createObjectNode();
                    textContent.put("type", "text");
                    textContent.put("text", "Component specification not found. Please refer to: https://github.com/lihongjie0209/jsx-docx/blob/main/docs/spec.md");
                    content.add(textContent);

                    ObjectNode result = mapper.createObjectNode();
                    result.set("content", content);
                    result.put("isError", true);

                    return createSuccessResponse(id, result);
                }
            }

            // Success response with spec content
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", specContent);
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", false);

            return createSuccessResponse(id, result);

        } catch (Exception e) {
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", "Error reading component specification: " + e.getMessage());
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", true);

            return createSuccessResponse(id, result);
        }
    }

    private JsonNode handleGenerateDocx(JsonNode id, JsonNode arguments) {
        try {
            // Get JSX code - either from direct input or from file
            String jsxCode = null;
            Path jsxSourcePath = null;
            
            if (arguments.has("jsxCode") && !arguments.get("jsxCode").isNull()) {
                jsxCode = arguments.get("jsxCode").asText();
            } else if (arguments.has("jsxFilePath") && !arguments.get("jsxFilePath").isNull()) {
                String jsxFilePath = arguments.get("jsxFilePath").asText();
                jsxSourcePath = Paths.get(jsxFilePath).toAbsolutePath();
                if (!Files.exists(jsxSourcePath)) {
                    throw new FileNotFoundException("JSX file not found: " + jsxSourcePath);
                }
                jsxCode = Files.readString(jsxSourcePath);
            } else {
                throw new IllegalArgumentException("Either 'jsxCode' or 'jsxFilePath' must be provided");
            }
            
            String outputPath = arguments.get("outputPath").asText();
            Map<String, Object> dataContext = null;
            
            if (arguments.has("data") && !arguments.get("data").isNull()) {
                dataContext = mapper.convertValue(arguments.get("data"), Map.class);
            }

            // Compile JSX to JavaScript
            String jsCode = compiler.compile(jsxCode);

            // Execute JavaScript to get VNode tree
            VNode vdom = runtime.run(jsCode, dataContext);

            // Render to DOCX
            Path outputFile = Paths.get(outputPath).toAbsolutePath();
            Files.createDirectories(outputFile.getParent());
            
            // Use jsxSourcePath as base path if available, otherwise use output parent
            Path basePath = jsxSourcePath != null ? jsxSourcePath.getParent() : outputFile.getParent();
            renderer.renderToDocx(vdom, outputFile.toString(), basePath, dataContext);

            // Success response
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            String successMsg = "Successfully generated DOCX file: " + outputFile.toString() + 
                    "\nFile size: " + Files.size(outputFile) + " bytes";
            if (jsxSourcePath != null) {
                successMsg += "\nSource JSX: " + jsxSourcePath.toString();
            }
            textContent.put("text", successMsg);
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", false);

            return createSuccessResponse(id, result);

        } catch (Exception e) {
            // Error response
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", "Error generating DOCX: " + e.getMessage());
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", true);

            return createSuccessResponse(id, result);
        }
    }

    private JsonNode handleDocxToJsx(JsonNode id, JsonNode arguments) {
        try {
            String docxPath = arguments.get("docxPath").asText();
            
            // Get embedImages flag (default: false, meaning export to files)
            boolean embedImages = false;
            if (arguments.has("embedImages") && !arguments.get("embedImages").isNull()) {
                embedImages = arguments.get("embedImages").asBoolean();
            }
            
            // Determine image export directory
            Path imageExportDir = null;
            if (!embedImages) {
                // Default behavior: export images to directory
                if (arguments.has("imageExportDir") && !arguments.get("imageExportDir").isNull()) {
                    String imageExportDirStr = arguments.get("imageExportDir").asText();
                    if (imageExportDirStr != null && !imageExportDirStr.trim().isEmpty()) {
                        imageExportDir = Paths.get(imageExportDirStr).toAbsolutePath();
                    }
                }
                
                // If no imageExportDir specified and outputJsxPath is provided, derive from output path
                if (imageExportDir == null && arguments.has("outputJsxPath") && !arguments.get("outputJsxPath").isNull()) {
                    String outputJsxPathStr = arguments.get("outputJsxPath").asText();
                    if (outputJsxPathStr != null && !outputJsxPathStr.trim().isEmpty()) {
                        Path outputPath = Paths.get(outputJsxPathStr).toAbsolutePath();
                        imageExportDir = outputPath.getParent().resolve("images");
                    }
                }
                
                // Last resort: use current directory + images
                if (imageExportDir == null) {
                    imageExportDir = Paths.get("images").toAbsolutePath();
                }
                
                // Create image directory if it doesn't exist
                if (!Files.exists(imageExportDir)) {
                    Files.createDirectories(imageExportDir);
                }
            }
            
            // Get optional output JSX file path
            Path outputJsxPath = null;
            if (arguments.has("outputJsxPath") && !arguments.get("outputJsxPath").isNull()) {
                String outputJsxPathStr = arguments.get("outputJsxPath").asText();
                if (outputJsxPathStr != null && !outputJsxPathStr.trim().isEmpty()) {
                    outputJsxPath = Paths.get(outputJsxPathStr).toAbsolutePath();
                }
            }
            
            // Convert DOCX to JSX
            String jsxCode = DocxToJsx.convert(docxPath, imageExportDir);

            // Success response
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            
            String message = "Successfully converted DOCX to JSX";
            if (imageExportDir != null) {
                message += "\nImages exported to: " + imageExportDir.toString();
            } else {
                message += "\nImages embedded as base64";
            }
            
            // If output path specified, write to file instead of returning content
            if (outputJsxPath != null) {
                Files.createDirectories(outputJsxPath.getParent());
                Files.writeString(outputJsxPath, jsxCode);
                message += "\nJSX code written to: " + outputJsxPath.toString();
                message += "\nFile size: " + Files.size(outputJsxPath) + " bytes";
            } else {
                // Return JSX code in response (for small documents)
                message += ":\n\n" + jsxCode;
            }
            
            textContent.put("text", message);
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", false);

            return createSuccessResponse(id, result);

        } catch (Exception e) {
            // Error response
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", "Error converting DOCX to JSX: " + e.getMessage());
            content.add(textContent);

            ObjectNode result = mapper.createObjectNode();
            result.set("content", content);
            result.put("isError", true);

            return createSuccessResponse(id, result);
        }
    }

    private JsonNode handleResourcesList(JsonNode id) {
        ObjectNode result = mapper.createObjectNode();
        result.set("resources", mapper.createArrayNode());
        return createSuccessResponse(id, result);
    }

    private JsonNode handlePromptsList(JsonNode id) {
        ObjectNode result = mapper.createObjectNode();
        result.set("prompts", mapper.createArrayNode());
        return createSuccessResponse(id, result);
    }

    private ObjectNode createServerInfo() {
        ObjectNode info = mapper.createObjectNode();
        info.put("name", "jsx-docx-mcp");
        info.put("version", VERSION);
        return info;
    }

    private JsonNode createSuccessResponse(JsonNode id, ObjectNode result) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", id);
        response.set("result", result);
        return response;
    }

    private JsonNode createErrorResponse(JsonNode id, int code, String message) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        if (id != null) {
            response.set("id", id);
        } else {
            response.putNull("id");
        }
        
        ObjectNode error = mapper.createObjectNode();
        error.put("code", code);
        error.put("message", message);
        response.set("error", error);
        
        return response;
    }
}
