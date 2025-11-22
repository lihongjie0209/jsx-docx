# 自定义组件支持说明

## ✅ jsx-docx 现在完整支持 React 风格的函数组件！

从 v1.0 版本开始，jsx-docx 支持完整的 React 函数组件语法，包括：
- 使用 `<Component />` 标签
- Props 传递
- Children 传递
- 嵌套组件
- 条件渲染
- 循环生成
- 数字、字符串等基础类型作为 children

---

## 基本示例

```jsx
// 定义一个简单的组件
const Greeting = ({ name }) => {
  return <Text>Hello, {name}!</Text>;
};

// 使用组件
<Document>
  <Paragraph>
    <Greeting name="World" />
  </Paragraph>
</Document>
```

### 工作原理

runtime.js 在 `React.createElement()` 中检测到 type 是函数时，会自动调用该函数并传入 props：

```javascript
// runtime.js 实现片段
if (typeof type === 'function') {
  const allProps = { ...(props || {}), children: flatChildren };
  const result = type(allProps);  // 调用组件函数
  return result;  // 返回组件生成的 VNode
}
```

---

## 完整功能示例

### 1. Props 传递

```jsx
const StyledText = ({ content, bold, size }) => {
  return <Text bold={bold} size={size}>{content}</Text>;
};

<Paragraph>
  <StyledText content="Important" bold={true} size={16} />
</Paragraph>
```

### 2. Children 传递

```jsx
const BoldParagraph = ({ children }) => {
  return (
    <Paragraph>
      <Text bold={true}>{children}</Text>
    </Paragraph>
  );
};

<BoldParagraph>This text will be bold</BoldParagraph>
```

### 3. 嵌套组件

```jsx
const BoldText = ({ children }) => {
  return <Text bold={true}>{children}</Text>;
};

const HighlightedParagraph = ({ children }) => {
  return (
    <Paragraph background="#FFFF00">
      <BoldText>{children}</BoldText>
    </Paragraph>
  );
};

<HighlightedParagraph>Important message</HighlightedParagraph>
```

### 4. 数字和计算

组件中可以直接使用数字，会自动转换为字符串：

```jsx
const MultiplicationCell = ({ row, col }) => {
  const result = row * col;  // 数字计算
  return (
    <Cell>
      <Paragraph>
        <Text>{row}×{col}={result}</Text>  {/* 数字自动转字符串 */}
      </Paragraph>
    </Cell>
  );
};

<Row>
  <MultiplicationCell row={2} col={3} />  {/* 显示 "2×3=6" */}
</Row>
```

### 5. 条件渲染

```jsx
const OptionalContent = ({ show, content }) => {
  if (show) {
    return <Text bold={true}>{content}</Text>;
  } else {
    return <Text italic={true}>{content}</Text>;
  }
};

<Paragraph>
  <OptionalContent show={true} content="This is bold" />
  <OptionalContent show={false} content="This is italic" />
</Paragraph>
```

### 6. 循环生成

```jsx
const TableRow = ({ row }) => {
  const cells = [];
  for (let col = 1; col <= row; col++) {
    cells.push(
      <Cell key={col}>
        <Paragraph><Text>{row}×{col}={row * col}</Text></Paragraph>
      </Cell>
    );
  }
  return <Row>{cells}</Row>;
};

<Table>
  <TableRow row={1} />
  <TableRow row={2} />
  <TableRow row={3} />
</Table>
```

---

## 完整示例：九九乘法表

```jsx
// 单个乘法单元格组件
const MultiCell = ({ row, col }) => {
  const result = row * col;
  return (
    <Cell vAlign="center" padding={{ top: 5, bottom: 5, left: 5, right: 5 }}>
      <Paragraph align="center">
        <Text bold={true}>{row}×{col}={result}</Text>
      </Paragraph>
    </Cell>
  );
};

// 空单元格组件
const EmptyCell = () => (
  <Cell background="#E0E0E0">
    <Paragraph><Text> </Text></Paragraph>
  </Cell>
);

// 表格行组件
const TableRow = ({ row }) => {
  const cells = [];
  
  // 添加有效的乘法单元格
  for (let col = 1; col <= row; col++) {
    cells.push(<MultiCell row={row} col={col} key={col} />);
  }
  
  // 添加空单元格
  for (let col = row + 1; col <= 9; col++) {
    cells.push(<EmptyCell key={`empty-${col}`} />);
  }
  
  return <Row>{cells}</Row>;
};

// 主文档
<Document>
  <Section pageSize="A4" orientation="portrait">
    <Paragraph align="center">
      <Text bold={true} size={20}>九九乘法表</Text>
    </Paragraph>
    
    <Table border={{ size: 1, color: "#000000" }}>
      <TableRow row={1} />
      <TableRow row={2} />
      <TableRow row={3} />
      <TableRow row={4} />
      <TableRow row={5} />
      <TableRow row={6} />
      <TableRow row={7} />
      <TableRow row={8} />
      <TableRow row={9} />
    </Table>
  </Section>
</Document>
```

---

## 技术实现

### runtime.js 处理流程

1. **组件检测**：`React.createElement()` 检测 type 是否为函数
2. **Props 合并**：将 props 和 children 合并为一个对象
3. **组件调用**：执行函数组件，传入合并后的 props
4. **返回 VNode**：组件返回的 JSX 会被递归处理为 VNode

### JsRuntime.java 类型处理

- **数字转字符串**：children 中的数字自动转换为字符串
- **布尔值处理**：true/false 转换为 "true"/"false"
- **递归处理**：嵌套组件返回的 VNode 递归转换为 Java 对象

---

## 实现的优势

1. **无需完整 React 运行时**：只实现组件调用机制，不需要虚拟 DOM、生命周期等
2. **GraalVM 中执行**：JavaScript 函数在编译时执行，生成 VNode 后在 Java 中渲染
3. **类型自动转换**：数字、布尔值等自动转换，简化使用
4. **完整的组合性**：支持嵌套、条件、循环等所有 React 模式

---

## 功能对比

| 特性 | React | jsx-docx |
|------|-------|----------|
| JSX 语法 | ✅ | ✅ |
| JavaScript 逻辑 | ✅ | ✅ |
| 函数定义 | ✅ | ✅ |
| 函数组件标签 `<Component />` | ✅ | ✅ |
| Props 传递 | ✅ | ✅ |
| Children 传递 | ✅ | ✅ |
| 嵌套组件 | ✅ | ✅ |
| 条件渲染 | ✅ | ✅ |
| 循环生成 | ✅ | ✅ |
| 数组操作 | ✅ | ✅ |
| 数字/布尔值自动转换 | ✅ | ✅ |
| 组件生命周期 | ✅ | ❌ |
| Hooks (useState, useEffect) | ✅ | ❌ |
| Context API | ✅ | ❌ |

**核心区别**：
- **jsx-docx**：组件在编译时执行，生成静态 VNode 树，然后渲染为 DOCX
- **React**：组件在运行时执行，生成虚拟 DOM，支持状态管理和交互

**jsx-docx 的设计目标**：
- ✅ 支持组件化代码组织
- ✅ 支持 Props 和 Children
- ✅ 支持条件和循环
- ❌ 不支持运行时状态（不需要，DOCX 是静态文档）
- ❌ 不支持生命周期和 Hooks（不需要，无交互）

---

## 最佳实践

### 1. 组件命名

使用 PascalCase 命名组件（首字母大写）：

```jsx
// ✅ 推荐
const MyComponent = ({ name }) => { ... };

// ❌ 不推荐（会被视为内置组件常量）
const mycomponent = ({ name }) => { ... };
```

### 2. Props 解构

使用解构语法提取 props：

```jsx
// ✅ 推荐
const Greeting = ({ name, title }) => {
  return <Text>{title} {name}</Text>;
};

// ✅ 也可以
const Greeting = (props) => {
  return <Text>{props.title} {props.name}</Text>;
};
```

### 3. Children 处理

使用 children prop 接收子元素：

```jsx
const Container = ({ children, background }) => {
  return (
    <Paragraph background={background}>
      {children}
    </Paragraph>
  );
};

<Container background="#FFFF00">
  <Text bold={true}>Highlighted text</Text>
</Container>
```

### 4. 条件渲染模式

```jsx
// 方式 1：if-else
const Status = ({ success }) => {
  if (success) {
    return <Text color="green">✓ Success</Text>;
  } else {
    return <Text color="red">✗ Failed</Text>;
  }
};

// 方式 2：三元运算符
const Status = ({ success }) => (
  <Text color={success ? "green" : "red"}>
    {success ? "✓ Success" : "✗ Failed"}
  </Text>
);

// 方式 3：逻辑与
const Warning = ({ show, message }) => (
  show && <Text color="orange">⚠ {message}</Text>
);
```

### 5. 循环生成模式

```jsx
// map 数组生成
const List = ({ items }) => (
  <BulletedList>
    {items.map((item, index) => (
      <ListItem key={index}>
        <Paragraph><Text>{item}</Text></Paragraph>
      </ListItem>
    ))}
  </BulletedList>
);

// for 循环生成
const Table = ({ rows, cols }) => {
  const tableRows = [];
  for (let i = 0; i < rows; i++) {
    const cells = [];
    for (let j = 0; j < cols; j++) {
      cells.push(<Cell key={j}><Paragraph><Text>Cell {i},{j}</Text></Paragraph></Cell>);
    }
    tableRows.push(<Row key={i}>{cells}</Row>);
  }
  return <Table>{tableRows}</Table>;
};
```

---

## 常见问题

### Q: 组件可以返回数组吗？

A: 可以！组件返回的数组会被自动展开：

```jsx
const MultiParagraphs = () => {
  return [
    <Paragraph key="1"><Text>First paragraph</Text></Paragraph>,
    <Paragraph key="2"><Text>Second paragraph</Text></Paragraph>
  ];
};
```

### Q: 可以在组件中使用 data context 吗？

A: 可以！全局 `data` 对象在组件中也可用：

```jsx
const UserInfo = () => {
  return (
    <Paragraph>
      <Text>Name: {data.user.name}</Text>
    </Paragraph>
  );
};
```

### Q: 组件可以调用其他组件吗？

A: 可以！支持任意深度的嵌套：

```jsx
const Inner = ({ text }) => <Text bold={true}>{text}</Text>;
const Middle = ({ text }) => <Paragraph><Inner text={text} /></Paragraph>;
const Outer = ({ text }) => <Section><Middle text={text} /></Section>;
```

### Q: 支持 JSX Fragment 吗？

A: 支持！可以使用 `<>...</>` 或数组：

```jsx
const Multi = () => (
  <>
    <Paragraph><Text>First</Text></Paragraph>
    <Paragraph><Text>Second</Text></Paragraph>
  </>
);
```

---

## 总结

jsx-docx 现在完整支持 React 风格的函数组件语法，让代码更加模块化和可复用。虽然不支持运行时特性（状态、生命周期），但对于生成静态文档来说，已经提供了足够的灵活性和表达能力。
