#!/usr/bin/env python3
"""
Test script for jsx-docx MCP server
Tests stdio mode with JSON-RPC 2.0 protocol
"""

import json
import subprocess
import sys
import os

def send_request(proc, request):
    """Send a JSON-RPC request to the MCP server"""
    request_str = json.dumps(request)
    print(f"\n→ Sending: {request_str}")
    proc.stdin.write(request_str + "\n")
    proc.stdin.flush()
    
    response_str = proc.stdout.readline()
    print(f"← Received: {response_str.strip()}")
    
    return json.loads(response_str)

def test_initialize(proc):
    """Test initialize method"""
    print("\n=== Test: initialize ===")
    request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": {
            "protocolVersion": "2024-11-05",
            "clientInfo": {
                "name": "test-client",
                "version": "1.0"
            }
        }
    }
    
    response = send_request(proc, request)
    assert response.get("jsonrpc") == "2.0"
    assert "result" in response
    assert response["result"]["protocolVersion"] == "2024-11-05"
    assert response["result"]["serverInfo"]["name"] == "jsx-docx-mcp"
    print("✓ Initialize successful")
    return response

def test_tools_list(proc):
    """Test tools/list method"""
    print("\n=== Test: tools/list ===")
    request = {
        "jsonrpc": "2.0",
        "id": 2,
        "method": "tools/list"
    }
    
    response = send_request(proc, request)
    assert response.get("jsonrpc") == "2.0"
    assert "result" in response
    assert "tools" in response["result"]
    
    tools = response["result"]["tools"]
    assert len(tools) == 1
    assert tools[0]["name"] == "generate_docx"
    print(f"✓ Found {len(tools)} tool(s): {tools[0]['name']}")
    return response

def test_generate_simple_docx(proc):
    """Test generate_docx with simple content"""
    print("\n=== Test: generate_docx (simple) ===")
    
    jsx_code = """
<Document>
  <Section>
    <Paragraph>
      <Text bold={true} fontSize={24}>MCP Test Document</Text>
    </Paragraph>
    <Paragraph>
      <Text>This document was generated via MCP protocol.</Text>
    </Paragraph>
  </Section>
</Document>
"""
    
    request = {
        "jsonrpc": "2.0",
        "id": 3,
        "method": "tools/call",
        "params": {
            "name": "generate_docx",
            "arguments": {
                "jsxCode": jsx_code,
                "outputPath": "test-mcp-simple.docx"
            }
        }
    }
    
    response = send_request(proc, request)
    assert response.get("jsonrpc") == "2.0"
    assert "result" in response
    assert response["result"]["isError"] == False
    
    # Check file was created
    assert os.path.exists("test-mcp-simple.docx")
    file_size = os.path.getsize("test-mcp-simple.docx")
    print(f"✓ Document created: test-mcp-simple.docx ({file_size} bytes)")
    return response

def test_generate_with_data(proc):
    """Test generate_docx with data context"""
    print("\n=== Test: generate_docx (with data) ===")
    
    jsx_code = """
<Document>
  <Section>
    <Paragraph>
      <Text bold={true} fontSize={24}>{data.title}</Text>
    </Paragraph>
    <Paragraph>
      <Text>Author: {data.author}</Text>
    </Paragraph>
    <Paragraph>
      <Text>Date: {data.date}</Text>
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
"""
    
    request = {
        "jsonrpc": "2.0",
        "id": 4,
        "method": "tools/call",
        "params": {
            "name": "generate_docx",
            "arguments": {
                "jsxCode": jsx_code,
                "outputPath": "test-mcp-data.docx",
                "data": {
                    "title": "Weekly Report",
                    "author": "Test User",
                    "date": "2024-01-15",
                    "items": [
                        "Completed MCP integration",
                        "Added stdio mode support",
                        "Created test scripts"
                    ]
                }
            }
        }
    }
    
    response = send_request(proc, request)
    assert response.get("jsonrpc") == "2.0"
    assert "result" in response
    assert response["result"]["isError"] == False
    
    # Check file was created
    assert os.path.exists("test-mcp-data.docx")
    file_size = os.path.getsize("test-mcp-data.docx")
    print(f"✓ Document created: test-mcp-data.docx ({file_size} bytes)")
    return response

def test_error_handling(proc):
    """Test error handling with invalid JSX"""
    print("\n=== Test: error handling ===")
    
    jsx_code = "<Document><InvalidTag></Document>"  # Missing closing tag
    
    request = {
        "jsonrpc": "2.0",
        "id": 5,
        "method": "tools/call",
        "params": {
            "name": "generate_docx",
            "arguments": {
                "jsxCode": jsx_code,
                "outputPath": "test-error.docx"
            }
        }
    }
    
    response = send_request(proc, request)
    assert response.get("jsonrpc") == "2.0"
    assert "result" in response
    # Should return error in content, not throw exception
    print("✓ Error handled gracefully")
    return response

def main():
    """Main test runner"""
    # Check if JAR exists
    jar_path = "target/jsx-docx-1.0-SNAPSHOT-fat.jar"
    if not os.path.exists(jar_path):
        print(f"Error: JAR not found at {jar_path}")
        print("Please run: mvn clean package")
        sys.exit(1)
    
    print("Starting jsx-docx MCP server...")
    proc = subprocess.Popen(
        ["java", "-jar", jar_path, "--mcp-stdio"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1
    )
    
    try:
        # Run tests
        test_initialize(proc)
        test_tools_list(proc)
        test_generate_simple_docx(proc)
        test_generate_with_data(proc)
        test_error_handling(proc)
        
        print("\n" + "="*60)
        print("All tests passed! ✓")
        print("="*60)
        print("\nGenerated files:")
        for filename in ["test-mcp-simple.docx", "test-mcp-data.docx"]:
            if os.path.exists(filename):
                size = os.path.getsize(filename)
                print(f"  - {filename} ({size} bytes)")
        
    except Exception as e:
        print(f"\n✗ Test failed: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
    finally:
        proc.terminate()
        proc.wait()

if __name__ == "__main__":
    main()
