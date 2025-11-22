# jsx-docx

将 JSX 文档转换为 DOCX。支持 CLI 命令行和 MCP (Model Context Protocol) 两种模式。

## 快速开始

### 构建项目

```powershell
mvn package
```

生成的文件：`target/jsx-docx-1.0-SNAPSHOT-fat.jar`

### 使用方式

#### 1. CLI 模式（命令行）

**单文件转换：**
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar test.jsx
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar test.jsx -o output.docx
```

**使用 JSON 数据上下文：**
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar template.jsx --data context.json -o output.docx
```

**批量转换：**
```powershell
# 转换多个文件到当前目录
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar file1.jsx file2.jsx file3.jsx

# 转换多个文件到指定目录
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar *.jsx -d output --verbose
```

#### 2. MCP 模式（AI Agent 集成）

**stdio 模式（推荐）：**
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

**服务器模式：**
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-server --mcp-port=3000
```

查看 [MCP 文档](docs/mcp.md) 了解如何配置 Claude Desktop 或其他 MCP 客户端。

## 命令行参数

- `<inputs>...` 一个或多个输入 JSX 文件
- `-o, --output <file>` 输出文件（仅单文件模式）
- `-d, --output-dir <dir>` 输出目录（批量模式，使用输入文件名 + .docx）
- `--data <file>` JSON 数据文件路径（可在 JSX 中通过 `data` 全局变量访问）
- `--mcp-stdio` 启动 MCP stdio 模式
- `--mcp-server` 启动 MCP 服务器模式
- `--mcp-port <port>` MCP 服务器端口（默认 3000）
- `--verbose` 显示详细过程
- `-V/--version` 显示版本

**JSX 语法说明：**

支持两种写法：

1. 直接返回 JSX 表达式（推荐）：
```jsx
<Document>
  <Section pageSize="A4" orientation="portrait">
    <Paragraph>Hello World</Paragraph>
  </Section>
</Document>
```

2. 使用 `render()` 函数（向后兼容）：
```jsx
render(
  <Document>
    <Section pageSize="A4">
      <Paragraph>Hello World</Paragraph>
    </Section>
  </Document>
);
```

3. 支持完整的 JavaScript 逻辑：
```jsx
// 可以在开头编写任意 JS 代码
const items = ['Apple', 'Banana', 'Orange'];

<Document>
  <Section pageSize="A4">
    <BulletedList>
      {items.map(item => (
        <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>
  </Section>
</Document>
```

4. 支持运行时数据上下文（从 JSON 文件或程序调用）：
```jsx
<Document>
  <Section pageSize="A4">
    <Paragraph><Text>标题：{data.title}</Text></Paragraph>
    <Paragraph><Text>作者：{data.author}</Text></Paragraph>
    <BulletedList>
      {data.items.map(item => (
        <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>
  </Section>
</Document>
```

使用 JSON 数据文件运行：
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar template.jsx --data data.json -o output.docx
```

## 支持的组件

jsx-docx 提供了完整的 JSX 组件库用于生成 Word 文档。以下是所有支持的组件列表：

| 组件 | 说明 | 主要属性 |
|------|------|----------|
| `<Document>` | 文档根节点 | - |
| `<Section>` | 文档节，设置页面布局 | `pageSize`, `orientation`, `margins` |
| `<Styles>` | 样式定义容器 | - |
| `<Style>` | 单个样式定义 | `styleId`, `type`, `bold`, `fontSize`, `color`, 等 |
| `<Paragraph>` | 段落 | `styleId`, `align`, `before`, `after`, `line`, `indent*`, 等 |
| `<Text>` | 文本运行（带格式） | `styleId`, `bold`, `italic`, `size`, `color`, `underline`, 等 |
| `<Heading>` | 标题段落 | `styleId` (必需) |
| `<Table>` | 表格 | `styleId`, `width`, `border`, `align`, `layout`, `columns` |
| `<Row>` | 表格行 | `header`, `height` |
| `<Cell>` | 表格单元格 | `styleId`, `vAlign`, `padding`, `width`, `background`, `border`, `colspan`, `rowspan` |
| `<BulletedList>` | 项目符号列表 | `bulletChar`, `bulletFont`, `indentLeft`, `indentIncrement`, `indentHanging` |
| `<NumberedList>` | 有序列表 | `start`, `format`, `levelConfig` |
| `<ListItem>` | 列表项 | `level` |
| `<Link>` | 超链接 | `href` |
| `<Image>` | 图片 | `src`, `width`, `height`, `fit`, `maxWidth`, `maxHeight` |
| `<Header>` | 页眉 | `type` (`default`/`first`/`even`/`odd`) |
| `<Footer>` | 页脚 | `type` (`default`/`first`/`even`/`odd`) |
| `<PageBreak>` | 分页符 | - |
| `<PageNumber>` | 页码域 | - |
| `<Toc>` | 目录 | `title`, `maxLevel`, `hyperlink`, `showPageNumbers` |
| `<Br>` | 段落内换行 | - |
| `<Tab>` | 制表符 | - |
| `<Include>` | 包含外部文件 | `path` |

### 完整组件规范

查看 **[完整组件规范文档](docs/spec.md)** 了解每个组件的详细说明、所有属性、子节点约束、使用示例和实现细节。

## 示例文件

查看 `examples/` 目录获取各种功能演示：

```powershell
# 转换单个示例
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test.jsx

# 批量转换所有示例
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/*.jsx -d output
```

## MCP (Model Context Protocol) 支持

jsx-docx 支持通过 MCP 协议与 AI Agent（如 Claude）集成，允许 AI 直接生成 Word 文档。

### 快速开始

1. **配置 Claude Desktop**（参见 [MCP 快速入门](MCP-QUICKSTART.md)）
2. **启动 MCP 服务器**：
   ```powershell
   java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
   ```
3. **在 Claude 中使用**：
   > "用 jsx-docx 生成一个周报，标题是本周工作总结，包含3个要点"

### 文档资源

- 📖 [MCP 快速入门指南](MCP-QUICKSTART.md) - 中文快速配置和使用说明
- 📘 [完整 MCP 文档](docs/mcp.md) - 详细的 API 和协议说明
- 💡 [MCP 使用示例](examples/mcp-examples.md) - 各种场景的 JSON-RPC 请求示例

### 测试脚本

```bash
# Python 测试脚本（自动化测试）
python test-mcp.py

# 命令行手动测试
echo '{"jsonrpc":"2.0","id":1,"method":"tools/list"}' | java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar --mcp-stdio
```

## Python Click CLI（可选，已弃用）

提供一个使用 Click 的包装脚本（不再推荐使用，直接使用 Java CLI 即可）。

安装依赖：

```powershell
pip install -r python-cli/requirements.txt
```

使用：

```powershell
python python-cli/jsx_docx_cli.py examples/test.jsx out.docx
```

## 开发说明

若需要扩展 CLI：
- 增加参数时在 `python-cli/jsx_docx_cli.py` 中添加 `@click.option`。
- 可以加入输出格式（例如 JSON 元数据）或调试模式。

## 后续改进建议
- 进一步减小 fat jar 体积（排除非当前平台 swc4j 原生包）。
- 增加批量转换功能（Python CLI 支持多文件）。
- 支持从标准输入读取 JSX。