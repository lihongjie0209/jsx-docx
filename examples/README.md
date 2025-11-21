# JSX-DOCX 示例文件

本目录包含各种 JSX 文档示例，展示不同的功能和用法。

## 示例列表

### test.jsx
完整功能演示，包含：
- 标题、段落、文本样式
- 表格布局
- 章节设置

### test1.jsx / test2.jsx
简单示例：
- 基本文档结构
- 段落内容

### test-js-logic.jsx
JavaScript 逻辑演示：
- 使用 `const` 定义数组
- 使用 `items.map()` 动态生成列表项
- 展示如何在 JSX 中使用 JavaScript 表达式

### test-simple-list.jsx
简单列表示例：
- 硬编码的项目符号列表
- 无 JavaScript 逻辑的基本列表结构

### test-map-minimal.jsx
最简化的 map() 示例：
- 演示数组映射的最小用例
- 无额外 Section 包装的列表

## 运行示例

**单个文件：**
```powershell
java -jar ../target/jsx-docx-1.0-SNAPSHOT-fat.jar test.jsx
```

**批量转换：**
```powershell
java -jar ../target/jsx-docx-1.0-SNAPSHOT-fat.jar *.jsx -d output
```

**从项目根目录运行：**
```powershell
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test.jsx
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/*.jsx -d output
```
