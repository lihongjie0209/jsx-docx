# jsx-docx MCP 快速入门

## 什么是 MCP？

MCP (Model Context Protocol) 是 Anthropic 开发的协议，允许 AI Agent（如 Claude）与外部工具和数据源交互。通过 MCP，Claude 可以直接调用 jsx-docx 生成 Word 文档。

## 安装配置

### 1. 构建项目

```powershell
cd d:\code\jsx-docx
mvn package
```

### 2. 配置 Claude Desktop

编辑 Claude Desktop 配置文件：

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`

添加以下内容：

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

**注意**：将路径替换为你的实际项目路径（使用正斜杠 `/`）。

### 3. 重启 Claude Desktop

关闭并重新启动 Claude Desktop，配置会自动加载。

## 使用示例

在 Claude Desktop 中，你可以直接对话：

### 示例 1：生成简单文档

**你说**：
> 用 jsx-docx 生成一个 Word 文档，标题是"测试报告"，加粗，24号字体。

**Claude 会调用**：
```json
{
  "name": "generate_docx",
  "arguments": {
    "jsxCode": "<Document><Section><Paragraph><Text bold={true} fontSize={24}>测试报告</Text></Paragraph></Section></Document>",
    "outputPath": "test-report.docx"
  }
}
```

### 示例 2：生成带数据的文档

**你说**：
> 生成一个周报，标题"本周工作总结"，包含3个要点：完成了功能A、修复了Bug B、开始了功能C的开发。

**Claude 会调用**：
```json
{
  "name": "generate_docx",
  "arguments": {
    "jsxCode": "<Document><Section><Paragraph><Text bold={true} fontSize={24}>{data.title}</Text></Paragraph><BulletedList>{data.items.map(item=>(<ListItem><Text>{item}</Text></ListItem>))}</BulletedList></Section></Document>",
    "outputPath": "weekly-report.docx",
    "data": {
      "title": "本周工作总结",
      "items": [
        "完成了功能A",
        "修复了Bug B",
        "开始了功能C的开发"
      ]
    }
  }
}
```

### 示例 3：生成表格

**你说**：
> 生成一个销售报告，包含表格，列出Q1、Q2、Q3、Q4的销售额。

**Claude 会调用**：
```json
{
  "name": "generate_docx",
  "arguments": {
    "jsxCode": "<Document><Section><Paragraph><Text bold={true} fontSize={24}>销售报告</Text></Paragraph><Table borders={true}><TableRow header={true}><TableCell><Paragraph><Text bold={true}>季度</Text></Paragraph></TableCell><TableCell><Paragraph><Text bold={true}>销售额</Text></Paragraph></TableCell></TableRow>{data.quarters.map(q=>(<TableRow><TableCell><Paragraph><Text>{q.name}</Text></Paragraph></TableCell><TableCell><Paragraph><Text>{q.sales}</Text></Paragraph></TableCell></TableRow>))}</Table></Section></Document>",
    "outputPath": "sales-report.docx",
    "data": {
      "quarters": [
        {"name": "Q1", "sales": "$125,000"},
        {"name": "Q2", "sales": "$145,000"},
        {"name": "Q3", "sales": "$168,000"},
        {"name": "Q4", "sales": "$192,000"}
      ]
    }
  }
}
```

## 可用工具

### 1. `get_component_spec` - 获取组件规范

**重要**：在生成文档之前，应首先调用此工具获取完整的组件规范和语法参考。

此工具返回 jsx-docx 的完整组件规范文档（spec.md），包括：
- 所有可用组件及其属性
- JSX 语法规则和示例
- 样式系统说明
- 数据上下文使用方法

**使用示例**：

```json
{
  "name": "get_component_spec",
  "arguments": {}
}
```

### 2. `generate_docx` - 生成 Word 文档

从 JSX 代码生成 Word 文档。支持所有 Word 功能（样式、表格、列表、图片、页眉页脚、目录等）。

**推荐工作流程**：
1. 首先调用 `get_component_spec` 了解语法
2. 然后调用 `generate_docx` 生成文档

## 支持的功能

所有 jsx-docx 组件都可以通过 MCP 使用：

- ✅ 文档结构：`<Document>`, `<Section>`, `<Paragraph>`, `<Text>`
- ✅ 样式：`<Styles>`, `<Style>`
- ✅ 列表：`<BulletedList>`, `<NumberedList>`, `<ListItem>`
- ✅ 表格：`<Table>`, `<TableRow>`, `<TableCell>`
- ✅ 图片：`<Image>`
- ✅ 页眉页脚：`<Header>`, `<Footer>`, `<PageNumber>`
- ✅ 目录：`<TOC>`
- ✅ 动态数据：通过 `data` 参数注入

## 命令行测试

不使用 Claude Desktop，直接测试 MCP 服务器：

### 初始化
```powershell
echo '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","clientInfo":{"name":"test","version":"1.0"}}}' | java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

### 列出工具
```powershell
echo '{"jsonrpc":"2.0","id":2,"method":"tools/list"}' | java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

### 获取组件规范
```powershell
echo '{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"get_component_spec","arguments":{}}}' | java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

### 生成文档
```powershell
echo '{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"generate_docx","arguments":{"jsxCode":"<Document><Section><Paragraph><Text>Hello MCP!</Text></Paragraph></Section></Document>","outputPath":"test.docx"}}}' | java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

### Python 测试脚本

```bash
python test-mcp.py
```

该脚本会自动测试：
1. 初始化连接
2. 列出工具
3. 获取组件规范
4. 生成简单文档
5. 生成带数据的文档
6. 错误处理

## 故障排查

### Claude Desktop 没有识别工具

1. 检查配置文件路径是否正确
2. 检查 JAR 文件路径是否正确（使用绝对路径）
3. 重启 Claude Desktop
4. 查看 Claude Desktop 的日志（通常在 `%APPDATA%\Claude\logs\`）

### 文档生成失败

1. 检查 JSX 语法是否正确（标签必须闭合）
2. 检查 `outputPath` 目录是否存在
3. 检查 `data` 对象结构是否匹配 JSX 代码
4. 查看错误消息的 `content[0].text` 字段

### 命令行测试正常但 Claude 无法使用

1. 确保 Java 在系统 PATH 中
2. 使用绝对路径指定 JAR 文件
3. 检查 Claude Desktop 的配置文件语法（JSON 格式）

## 高级用法

### 自定义样式

```jsx
<Document>
  <Styles>
    <Style name="Title" fontSize={48} color="#0066CC" bold={true}/>
    <Style name="Heading1" fontSize={32} color="#333333" bold={true}/>
  </Styles>
  <Section>
    <Paragraph style="Title">
      <Text>年度报告</Text>
    </Paragraph>
  </Section>
</Document>
```

### 页眉页脚

```jsx
<Document>
  <Section 
    header={<Header><Paragraph alignment="right"><Text color="#999999">机密文件</Text></Paragraph></Header>}
    footer={<Footer><Paragraph alignment="center"><Text>第 </Text><PageNumber/><Text> 页</Text></Paragraph></Footer>}
  >
    <Paragraph><Text>正文内容</Text></Paragraph>
  </Section>
</Document>
```

### 条件渲染

```jsx
<Table borders={true}>
  {data.metrics.map(m => (
    <TableRow>
      <TableCell><Paragraph><Text>{m.name}</Text></Paragraph></TableCell>
      <TableCell>
        <Paragraph>
          <Text color={m.trend === 'up' ? '#00AA00' : '#CC0000'}>
            {m.value}
          </Text>
        </Paragraph>
      </TableCell>
    </TableRow>
  ))}
</Table>
```

## 更多资源

- [完整 MCP 文档](docs/mcp.md)
- [MCP 示例](examples/mcp-examples.md)
- [组件规范](docs/spec.md)
- [普通示例](examples/)

## 反馈

如有问题或建议，请在 GitHub 提 Issue：
https://github.com/lihongjie0209/jsx-docx/issues
