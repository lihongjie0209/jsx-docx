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

### weekly-report.jsx
动态项目周报示例（展示 JSX + JS 的强大能力）：
- **样式系统**：通过 `<Styles>` 定义可复用的段落样式
- **页眉页脚**：专业的文档标识和页码
- **动态列表**：使用 `.map()` 从数据生成项目符号列表
- **动态表格**：根据数据动态生成表格行，支持条件渲染（颜色根据趋势变化）
- **数据驱动**：支持通过 `--data` 参数注入 JSON 数据

**使用方式：**
```powershell
# 使用内置默认数据
java -jar ../target/jsx-docx-1.0-SNAPSHOT-fat.jar weekly-report.jsx -o report.docx

# 使用外部 JSON 数据文件
java -jar ../target/jsx-docx-1.0-SNAPSHOT-fat.jar weekly-report.jsx --data weekly-report-data.json -o report.docx
```

### 样式系统示例
- **test-simple-styles.jsx**: 基础样式定义和使用
- **test-custom-styles.jsx**: 自定义样式与直接属性组合
- **test-all-styles.jsx**: 完整的样式系统演示
- **test-multi-level-headings.jsx**: 多级标题样式

### 目录功能
- **test-toc.jsx**: Word 目录 (TOC) 自动生成和更新

### 组件复用
- **with-include.jsx**: 使用 `<Include>` 组件包含外部文件
- **nested-include.jsx**: 嵌套包含示例
- **components/**: 可复用的组件目录
  - **header.jsx**: 公司页眉
  - **footer.jsx**: 页脚免责声明
  - **section-with-header.jsx**: 带页眉的章节

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
