@echo off
REM Test MCP initialize
echo {"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","clientInfo":{"name":"test","version":"1.0"}}}

REM Test tools/list
echo {"jsonrpc":"2.0","id":2,"method":"tools/list"}

REM Test generate_docx
echo {"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"generate_docx","arguments":{"jsxCode":"<Document><Section><Paragraph><Text bold={true}>MCP Test</Text></Paragraph></Section></Document>","outputPath":"test-mcp.docx"}}}
