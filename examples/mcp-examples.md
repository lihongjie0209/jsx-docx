# MCP (Model Context Protocol) Examples

This directory contains examples for using jsx-docx through MCP (Model Context Protocol).

## Basic Usage

### Example 0: Get Component Specification (IMPORTANT - Always Call First!)

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 0,
  "method": "tools/call",
  "params": {
    "name": "get_component_spec",
    "arguments": {}
  }
}
```

**Response:** Returns the complete jsx-docx component specification with all available components, their properties, and JSX syntax rules. This is essential for understanding how to write correct JSX code.

**Why call this first?**
- Learn all available components (`<Document>`, `<Paragraph>`, `<Text>`, `<Table>`, etc.)
- Understand component properties and their types
- See correct JSX syntax examples
- Know which attributes are required vs optional
- Avoid common mistakes in JSX structure

### Example 1: Simple Document

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Section><Paragraph><Text bold={true}>Hello World!</Text></Paragraph></Section></Document>",
      "outputPath": "hello.docx"
    }
  }
}
```

### Example 2: Document with Styles

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Styles><Style name=\"Title\" fontSize={48} color=\"#0066CC\" bold={true}/></Styles><Section><Paragraph style=\"Title\"><Text>Annual Report 2024</Text></Paragraph></Section></Document>",
      "outputPath": "report.docx"
    }
  }
}
```

### Example 3: Dynamic Content with Data

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Section><Paragraph><Text bold={true} fontSize={24}>{data.title}</Text></Paragraph><Paragraph><Text>Author: {data.author}</Text></Paragraph><Paragraph><Text>Date: {data.date}</Text></Paragraph><BulletedList>{data.items.map(item=>(<ListItem><Text>{item}</Text></ListItem>))}</BulletedList></Section></Document>",
      "outputPath": "weekly-report.docx",
      "data": {
        "title": "Weekly Report",
        "author": "John Doe",
        "date": "2024-01-15",
        "items": [
          "Completed feature A",
          "Fixed bug B",
          "Started work on feature C"
        ]
      }
    }
  }
}
```

### Example 4: Table with Data

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 4,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Section><Paragraph><Text bold={true} fontSize={24}>{data.title}</Text></Paragraph><Table borders={true}><TableRow header={true}><TableCell><Paragraph><Text bold={true}>Metric</Text></Paragraph></TableCell><TableCell><Paragraph><Text bold={true}>Value</Text></Paragraph></TableCell><TableCell><Paragraph><Text bold={true}>Trend</Text></Paragraph></TableCell></TableRow>{data.metrics.map(m=>(<TableRow><TableCell><Paragraph><Text>{m.name}</Text></Paragraph></TableCell><TableCell><Paragraph><Text>{m.value}</Text></Paragraph></TableCell><TableCell><Paragraph><Text color={m.trend==='up'?'#00AA00':'#CC0000'}>{m.trend==='up'?'↑':'↓'}</Text></Paragraph></TableCell></TableRow>))}</Table></Section></Document>",
      "outputPath": "metrics.docx",
      "data": {
        "title": "Monthly Metrics",
        "metrics": [
          {"name": "Revenue", "value": "$125,000", "trend": "up"},
          {"name": "Costs", "value": "$45,000", "trend": "down"},
          {"name": "Profit", "value": "$80,000", "trend": "up"}
        ]
      }
    }
  }
}
```

### Example 5: Complete Document with Header/Footer

**Request:**
```json
{
  "jsonrpc": "2.0",
  "id": 5,
  "method": "tools/call",
  "params": {
    "name": "generate_docx",
    "arguments": {
      "jsxCode": "<Document><Styles><Style name=\"Title\" fontSize={48} color=\"#0066CC\" bold={true}/><Style name=\"Heading1\" fontSize={32} color=\"#333333\" bold={true}/></Styles><Section header={<Header><Paragraph alignment=\"right\"><Text color=\"#999999\">Confidential</Text></Paragraph></Header>} footer={<Footer><Paragraph alignment=\"center\"><Text>Page </Text><PageNumber/></Paragraph></Footer>}><Paragraph style=\"Title\"><Text>{data.title}</Text></Paragraph><Paragraph><Text>Date: {data.date}</Text></Paragraph><Paragraph style=\"Heading1\"><Text>Summary</Text></Paragraph><Paragraph><Text>{data.summary}</Text></Paragraph><Paragraph style=\"Heading1\"><Text>Key Points</Text></Paragraph><BulletedList>{data.points.map(p=>(<ListItem><Text>{p}</Text></ListItem>))}</BulletedList></Section></Document>",
      "outputPath": "full-report.docx",
      "data": {
        "title": "Q4 Report",
        "date": "2024-12-31",
        "summary": "This report summarizes the key achievements and metrics for Q4 2024.",
        "points": [
          "Achieved 125% of revenue target",
          "Launched 3 new products",
          "Expanded to 5 new markets",
          "Team grew from 50 to 75 members"
        ]
      }
    }
  }
}
```

## Testing with Command Line

You can test these examples using PowerShell:

```powershell
# Start MCP server
$process = Start-Process -FilePath "java" -ArgumentList "-jar", "target/jsx-docx-1.0-SNAPSHOT-fat.jar", "--mcp-stdio" -NoNewWindow -RedirectStandardInput -RedirectStandardOutput -PassThru

# Send request (single line JSON)
$request = '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"generate_docx","arguments":{"jsxCode":"<Document><Section><Paragraph><Text>Hello!</Text></Paragraph></Section></Document>","outputPath":"test.docx"}}}'
$request | Set-Content -Path input.json
Get-Content input.json | & java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

Or use the Python test script:

```bash
python test-mcp.py
```

## Integration with Claude Desktop

Add to your `claude_desktop_config.json`:

**Windows:** `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "jsx-docx": {
      "command": "java",
      "args": [
        "-jar",
        "D:/code/jsx-docx/target/jsx-docx-1.0-SNAPSHOT-fat.jar",
        "--mcp-stdio"
      ]
    }
  }
}
```

Then you can ask Claude to generate Word documents:

- "Generate a Word document with a title 'My Report' and 3 bullet points"
- "Create a weekly report document with a table showing metrics"
- "Generate a document with custom styles and formatting"

Claude will use the `generate_docx` tool to create the documents for you!

## Available MCP Tools

jsx-docx provides two MCP tools:

### 1. `get_component_spec` - Get Component Specification

**Always call this first!** Returns the complete jsx-docx component specification including:
- All available components and their properties
- JSX syntax rules and examples
- Data context usage
- Style system documentation
- Error handling guidelines

**Parameters:** None

**Usage:**
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "get_component_spec",
    "arguments": {}
  }
}
```

### 2. `generate_docx` - Generate Word Document

Generate a Word document from JSX code. Supports all Word features.

**Parameters:**
- `jsxCode` (string, required): JSX code defining document structure
- `outputPath` (string, required): Output .docx file path
- `data` (object, optional): Data context accessible as `data` global in JSX

**Recommended workflow:**
1. Call `get_component_spec` to learn syntax
2. Write JSX code based on specification
3. Call `generate_docx` with your JSX code

## Supported Components

All jsx-docx components are available through MCP:

- **Document structure**: `<Document>`, `<Section>`, `<Paragraph>`, `<Text>`
- **Styles**: `<Styles>`, `<Style>`
- **Lists**: `<BulletedList>`, `<NumberedList>`, `<ListItem>`
- **Tables**: `<Table>`, `<Row>`, `<Cell>`
- **Images**: `<Image>` (with path, base64, or URL)
- **Headers/Footers**: `<Header>`, `<Footer>`, `<PageNumber>`
- **Table of Contents**: `<Toc>`
- **Includes**: `<Include>` (for modular documents)

See [Component Specification](../docs/spec.md) for full documentation, or call `get_component_spec` tool.

## Tips

1. **Always get spec first**: Call `get_component_spec` before generating documents to understand available components and syntax
2. **Escape quotes in JSON**: Use `\"` for quotes inside JSX strings
3. **Single-line JSX**: Keep JSX code on a single line or escape newlines in JSON
4. **Data context**: Use the `data` parameter to inject dynamic content
5. **Error handling**: Check `isError` field in response for generation errors
6. **File paths**: Use absolute paths for `outputPath` to avoid confusion
7. **Learn from spec**: The specification includes working examples for all components

## See Also

- [MCP Documentation](../docs/mcp.md)
- [Main README](../README.md)
- [Component Specification](../docs/spec.md)
- [Regular Examples](../examples/)
