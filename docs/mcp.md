# MCP (Model Context Protocol) Support

## Overview

jsx-docx now supports MCP (Model Context Protocol), allowing AI agents to generate Word documents dynamically.

## Usage Modes

### 1. stdio Mode (Standard Input/Output)

This mode uses JSON-RPC 2.0 over stdin/stdout, ideal for process-based integration:

```bash
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

Or with the CLI shorthand:

```bash
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp
```

### 2. Server Mode (HTTP with SSE)

Run as an HTTP server (not yet implemented):

```bash
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-server --mcp-port=3000
```

## MCP Configuration

For Claude Desktop or other MCP clients, add to your MCP settings file:

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`

```json
{
  "mcpServers": {
    "jsx-docx": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/jsx-docx-1.0-SNAPSHOT-fat.jar",
        "--mcp-stdio"
      ]
    }
  }
}
```

## Available Tools

### `generate_docx`

Generate a Word document from JSX code.

**Parameters:**

- `jsxCode` (string, required): JSX code defining the document structure
- `outputPath` (string, required): Output file path for the generated .docx file
- `data` (object, optional): Data context object accessible as `data` global in JSX

**Example Request:**

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Section><Paragraph><Text>Hello World!</Text></Paragraph></Section></Document>",
      "outputPath": "output.docx"
    }
  }
}
```

**Example with Data Context:**

```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Section><Paragraph><Text>{data.title}</Text></Paragraph></Section></Document>",
      "outputPath": "report.docx",
      "data": {
        "title": "Monthly Report",
        "author": "John Doe"
      }
    }
  }
}
```

## Protocol Details

jsx-docx implements MCP protocol version `2024-11-05` with the following capabilities:

- **tools**: List and call document generation tools
- **resources**: Not implemented (returns empty list)
- **prompts**: Not implemented (returns empty list)

### Supported Methods

1. **initialize**: Initialize MCP session
2. **tools/list**: List available tools
3. **tools/call**: Call a tool (generate_docx)
4. **resources/list**: List resources (empty)
5. **prompts/list**: List prompts (empty)

## Testing

You can test MCP mode manually using stdin/stdout:

```bash
# Start the server
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio

# Send initialize request (paste this and press Enter)
{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","clientInfo":{"name":"test-client","version":"1.0"}}}

# List tools
{"jsonrpc":"2.0","id":2,"method":"tools/list"}

# Generate a document
{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"generate_docx","arguments":{"jsxCode":"<Document><Section><Paragraph><Text>Hello MCP!</Text></Paragraph></Section></Document>","outputPath":"test-mcp.docx"}}}
```

## JSX Code Examples

All jsx-docx components are supported in MCP mode. Here are some examples:

### Simple Document

```jsx
<Document>
  <Section>
    <Paragraph>
      <Text>Hello World!</Text>
    </Paragraph>
  </Section>
</Document>
```

### Document with Styles

```jsx
<Document>
  <Styles>
    <Style name="Title" fontSize={48} color="#0066CC" bold={true} />
  </Styles>
  <Section>
    <Paragraph style="Title">
      <Text>Annual Report 2024</Text>
    </Paragraph>
  </Section>
</Document>
```

### Dynamic Content with Data

```jsx
<Document>
  <Section>
    <Paragraph>
      <Text bold={true}>{data.title}</Text>
    </Paragraph>
    <BulletedList>
      {data.items.map(item => (
        <ListItem>
          <Text>{item}</Text>
        </ListItem>
      ))}
    </BulletedList>
  </Section>
</Document>
```

### Table with Data

```jsx
<Document>
  <Section>
    <Table borders={true}>
      <TableRow header={true}>
        <TableCell><Paragraph><Text bold={true}>Name</Text></Paragraph></TableCell>
        <TableCell><Paragraph><Text bold={true}>Value</Text></Paragraph></TableCell>
      </TableRow>
      {data.metrics.map(m => (
        <TableRow>
          <TableCell><Paragraph><Text>{m.name}</Text></Paragraph></TableCell>
          <TableCell><Paragraph><Text>{m.value}</Text></Paragraph></TableCell>
        </TableRow>
      ))}
    </Table>
  </Section>
</Document>
```

## Limitations

- Server mode (HTTP/SSE) is not yet implemented
- Resources and prompts capabilities are not implemented
- Only the `generate_docx` tool is available

## Troubleshooting

### Error: "Parse error"

Check that your JSON-RPC request is valid JSON and includes the required fields:
- `jsonrpc`: Must be "2.0"
- `id`: Unique identifier for the request
- `method`: The method to call
- `params`: Parameters for the method

### Error: "Method not found"

Verify you're using one of the supported methods: `initialize`, `tools/list`, `tools/call`, `resources/list`, `prompts/list`.

### Error: "Unknown tool"

Only `generate_docx` is available. Check the tool name in your `tools/call` request.

### JSX Compilation Error

If your JSX code fails to compile, check:
- All tags are properly closed
- Component names start with capital letters
- Expressions use curly braces `{}`
- Strings are properly quoted

### Runtime Error

If JavaScript execution fails:
- Verify `data` object structure matches your JSX code
- Check for undefined variables or properties
- Ensure array methods (map, filter) are used correctly

## See Also

- [Main Documentation](../README.md)
- [Component Specification](../docs/spec.md)
- [Examples](../examples/)
- [MCP Protocol Specification](https://spec.modelcontextprotocol.io/)
