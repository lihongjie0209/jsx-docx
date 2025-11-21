package cn.lihongjie.jsxdocx.mcp;

import cn.lihongjie.jsxdocx.Compiler;
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
        
        // Tool: generate_docx
        ObjectNode tool = mapper.createObjectNode();
        tool.put("name", "generate_docx");
        tool.put("description", "Generate a Word document (.docx) from JSX code. " +
                "Supports all Word features including styles, tables, lists, images, headers/footers, TOC, etc.");
        
        ObjectNode inputSchema = mapper.createObjectNode();
        inputSchema.put("type", "object");
        
        ObjectNode properties = mapper.createObjectNode();
        
        ObjectNode jsxCode = mapper.createObjectNode();
        jsxCode.put("type", "string");
        jsxCode.put("description", "JSX code defining the document structure. " +
                "Use <Document>, <Section>, <Paragraph>, <Text>, <Table>, etc.");
        properties.set("jsxCode", jsxCode);
        
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
        required.add("jsxCode");
        required.add("outputPath");
        inputSchema.set("required", required);
        
        tool.set("inputSchema", inputSchema);
        tools.add(tool);

        ObjectNode result = mapper.createObjectNode();
        result.set("tools", tools);
        
        return createSuccessResponse(id, result);
    }

    private JsonNode handleToolsCall(JsonNode id, JsonNode params) {
        String toolName = params.get("name").asText();
        JsonNode arguments = params.get("arguments");

        if ("generate_docx".equals(toolName)) {
            return handleGenerateDocx(id, arguments);
        }

        return createErrorResponse(id, -32602, "Unknown tool: " + toolName);
    }

    private JsonNode handleGenerateDocx(JsonNode id, JsonNode arguments) {
        try {
            String jsxCode = arguments.get("jsxCode").asText();
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
            
            renderer.renderToDocx(vdom, outputFile.toString(), outputFile.getParent(), dataContext);

            // Success response
            ArrayNode content = mapper.createArrayNode();
            ObjectNode textContent = mapper.createObjectNode();
            textContent.put("type", "text");
            textContent.put("text", "Successfully generated DOCX file: " + outputFile.toString() + 
                    "\nFile size: " + Files.size(outputFile) + " bytes");
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
