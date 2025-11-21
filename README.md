# jsx-docx

将 JSX 文档转换为 DOCX。

## Java 构建 & Picocli CLI

构建 fat jar：

```powershell
mvn package
```

生成的文件：`target/jsx-docx-1.0-SNAPSHOT-fat.jar`

运行（Picocli CLI 支持 `-h` 查看帮助）：

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

主要参数（Java CLI）：

- `<inputs>...` 一个或多个输入 JSX 文件
- `-o, --output <file>` 输出文件（仅单文件模式）
- `-d, --output-dir <dir>` 输出目录（批量模式，使用输入文件名 + .docx）
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

## 示例文件

查看 `examples/` 目录获取各种功能演示：

```powershell
# 转换单个示例
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test.jsx

# 批量转换所有示例
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/*.jsx -d output
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