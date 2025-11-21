# "Nested Text Components" 问题详细说明

## 问题定义

在 jsx-docx 中，当你在 `<Paragraph>` 内部嵌套多个 `<Text>` 组件时会出现问题。这与 Apache POI 的文本渲染模型和 jsx-docx 的组件设计有关。

## 问题根源

### 1. Apache POI 的文本模型

在 Microsoft Word 文档中，文本结构是这样的：
```
Paragraph (段落)
  ├── Run 1 (文本片段)
  ├── Run 2 (文本片段)
  └── Run 3 (文本片段)
```

- **Paragraph (XWPFParagraph)**: 段落是 Word 的基本块级元素
- **Run (XWPFRun)**: 运行是具有统一格式的文本片段
  - 每个 Run 可以有不同的颜色、字体、大小、粗体/斜体等
  - 但 **一个 Run 只能包含简单字符串，不能包含其他结构**

### 2. jsx-docx 的组件设计

在 jsx-docx 中定义的组件：

```
<Text> 组件：
  - 对应 Apache POI 的 "内容片段"
  - 作用是将文本内容包装成一个 XWPFRun
  - 可以应用文本级格式（颜色、字体、大小、粗体等）
  - 子元素只能是 **字符串**，不能是其他 VNode
```

查看 `Renderer.java` 第 240-248 行的处理逻辑：

```java
case "text":
    if (parent instanceof XWPFParagraph) {
        XWPFRun run = ((XWPFParagraph) parent).createRun();
        applyTextProps(run, node);
        for (Object c : node.getChildren()) {
            if (c instanceof String) {
                run.setText((String) c);  // ← 只接受字符串！
            }
        }
    }
    break;
```

**关键点**：
- `<Text>` 创建一个 `XWPFRun`
- 然后只遍历 **字符串类型** 的子元素
- **忽略** VNode 类型的子元素（即嵌套的 `<Text>` 组件）

## 问题场景

### ❌ 错误的做法（会导致问题）

```jsx
<Paragraph>
  <Text>
    <Text>Hello</Text>
    <Text>World</Text>
  </Text>
</Paragraph>
```

执行流程：
1. 创建 Paragraph
2. 创建第一个 Text → 生成 Run 1
3. Run 1 的循环遍历子元素，找到两个 `<Text>` VNode
4. **因为它们是 VNode 而不是字符串，被 `instanceof String` 检查过滤掉，直接忽略**
5. Run 1 的文本内容为空
6. 第二个和第三个 Text 组件 **没有父 Paragraph 直接支持它们**
7. **结果：两个 "Hello" 和 "World" 都消失了**

### ✅ 正确的做法（扁平化）

```jsx
<Paragraph>
  <Text>Hello</Text>
  <Text>World</Text>
</Paragraph>
```

执行流程：
1. 创建 Paragraph
2. 遍历 Paragraph 的子元素
3. 第一个 Text → 生成 Run 1，包含 "Hello"
4. 第二个 Text → 生成 Run 2，包含 "World"
5. **结果：Paragraph 包含两个 Run，文本完整显示**

## 为什么会在表格生成中出现？

在最初的乘法表实现中：

```jsx
const rows = [];
for (let i = 1; i <= 19; i++) {
  const cells = [];
  cells.push(<Paragraph align="center" bold="true"><Text>{i}</Text></Paragraph>);
  
  for (let j = 1; j <= 19; j++) {
    const product = i * j;
    cells.push(<Paragraph align="center"><Text>{product}</Text></Paragraph>);
  }
  
  rows.push(
    <Paragraph key={i}>
      {cells.map((cell, idx) => (
        <Text key={idx}>     {/* ← 问题在这里！*/}
          {cell}              {/* 试图在 Text 中嵌套 Paragraph */}
        </Text>
      ))}
    </Paragraph>
  );
}
```

问题分析：
```
<Paragraph>
  <Text>
    {/* cells 是 Paragraph VNode 数组 */}
    <Paragraph>...</Paragraph>
    <Paragraph>...</Paragraph>
    <Paragraph>...</Paragraph>
  </Text>
</Paragraph>
```

- 外层 `<Text>` 只能包含字符串
- 里面的 `<Paragraph>` VNode 被过滤掉
- 结果：所有单元格内容消失

## 解决方案

### 方案 1: 完全扁平化（推荐）

```jsx
<Paragraph>
  <Text>Row 1: 1  2  3  4  5  ... 19</Text>
</Paragraph>
```

### 方案 2: 多个并列的 Text

```jsx
<Paragraph>
  <Text>Hello</Text>
  <Text> </Text>
  <Text>World</Text>
</Paragraph>
```

### 方案 3: 使用表格组件代替嵌套段落

```jsx
<Table width="100%">
  <Row>
    <Cell><Paragraph><Text>1</Text></Paragraph></Cell>
    <Cell><Paragraph><Text>2</Text></Paragraph></Cell>
    <Cell><Paragraph><Text>3</Text></Paragraph></Cell>
  </Row>
</Table>
```

- 表格的 `<Cell>` 可以包含 `<Paragraph>`
- 这是正确的 Word 结构

## 技术原因总结

| 组件 | 可接纳的子元素类型 | 不能包含 |
|------|-------------------|--------|
| `<Document>` | `<Section>` | 其他块级元素 |
| `<Section>` | `<Paragraph>`, `<Table>`, `<PageBreak>` 等块级元素 | 文本、Run |
| `<Paragraph>` | `<Text>`, `<Br>`, `<Tab>` 及字符串 | 块级元素 |
| **`<Text>`** | **仅字符串** | **VNode、块级元素** ❌ |
| `<Table>` | `<Row>` | 其他元素 |
| `<Row>` | `<Cell>` | 其他元素 |
| `<Cell>` | `<Paragraph>` 和其他块级元素 | 直接文本（必须用 `<Paragraph>` 包装） |

## 在乘法表中的修复

最终正确的乘法表实现：

```jsx
<Table width="100%">
  <Row header="true">
    <Cell background="#4472C4"><Paragraph>...</Paragraph></Cell>
    <Cell background="#4472C4"><Paragraph>...</Paragraph></Cell>
    ...
  </Row>
  <Row>
    <Cell background="#D9E1F2"><Paragraph>...</Paragraph></Cell>
    <Cell background="#F2F2F2"><Paragraph>...</Paragraph></Cell>
    ...
  </Row>
</Table>
```

- 使用 `<Table>` → `<Row>` → `<Cell>` 结构
- 每个 `<Cell>` 包含 `<Paragraph>`
- 每个 `<Paragraph>` 包含 `<Text>`
- 这样完全避免了嵌套问题

## 最佳实践

✅ **DO**:
```jsx
// 使用表格来展示表格数据
<Table>
  <Row>
    <Cell><Paragraph><Text>Content</Text></Paragraph></Cell>
  </Row>
</Table>

// 扁平化文本结构
<Paragraph>
  <Text>Hello</Text>
  <Text>World</Text>
</Paragraph>

// 使用字符串拼接
<Paragraph><Text>{"Hello" + " " + "World"}</Text></Paragraph>
```

❌ **DON'T**:
```jsx
// 不要在 Text 中嵌套其他 VNode
<Paragraph>
  <Text>
    <Text>Nested</Text>
  </Text>
</Paragraph>

// 不要在 Text 中放块级元素
<Paragraph>
  <Text>
    <Paragraph>Block element</Paragraph>
  </Text>
</Paragraph>

// 不要期望 JSX 数组会自动扁平化
<Text>
  {[<Paragraph>a</Paragraph>, <Paragraph>b</Paragraph>]}
</Text>
```

## 参考

- Apache POI 文档：[XWPFParagraph](https://poi.apache.org/apidocs/4.1/org/apache/poi/xwpf/usermodel/XWPFParagraph.html) 和 [XWPFRun](https://poi.apache.org/apidocs/4.1/org/apache/poi/xwpf/usermodel/XWPFRun.html)
- jsx-docx Renderer: `src/main/java/com/example/jsxdocx/Renderer.java` 第 240-248 行
- Word 文档结构：W3C Office Open XML 标准
