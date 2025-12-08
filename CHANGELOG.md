# Changelog

所有值得注意的项目更改都将记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [0.2.0] - 2024-12-08

### 新增 (Added)
- **标准输入支持** (`--stdin`)：现在可以从管道或标准输入读取 JSX 内容 [#PR]
  ```bash
  cat template.jsx | java -jar jsx-docx.jar --stdin -o output.docx
  ```
- **进度条显示**：批量转换时自动显示进度条，显示当前处理的文件、完成度和百分比
  - 默认启用，通过 `--no-progress` 可以禁用
  - 在 verbose 模式下自动禁用以避免输出冲突
- **报告生成功能** (`--report <file>`)：生成 JSON 格式的转换结果报告
  ```json
  {
    "total": 10,
    "success": 9,
    "failed": 1,
    "results": [
      {"file": "test.jsx", "status": "success", "output": "test.docx", "time_ms": 234}
    ]
  }
  ```

### 改进 (Changed)
- 版本号从 0.1.0 更新到 0.2.0
- 批量转换完成后显示总耗时
- 改进了错误输出格式，在进度条模式下不会干扰显示
- 更新了 README 文档，添加了新功能的使用示例

### 文档 (Documentation)
- 添加了 v0.2.0 功能的完整文档
- 创建了测试脚本 `test-v0.2.0-features.sh`
- 更新了所有示例命令中的 JAR 文件名

---

## [0.1.0] - 2024-12-06

### 新增 (Added)
- **核心功能**
  - JSX → DOCX 完整转换流程
  - JSX 编译（基于 SWC4J）
  - JavaScript 运行时（基于 GraalVM Polyglot）
  - DOCX 渲染（基于 Apache POI）

- **文档组件** (30+)
  - 文档结构：`<Document>`, `<Section>`, `<Paragraph>`
  - 文本格式：`<Text>` 支持粗体、斜体、颜色等
  - 样式系统：`<Styles>`, `<Style>`
  - 标题：`<Heading>` 多级标题
  - 表格：`<Table>`, `<Row>`, `<Cell>` 支持合并单元格
  - 列表：`<BulletedList>`, `<NumberedList>` 支持嵌套
  - 图片：`<Image>` 支持自适应
  - 超链接：`<Link>`
  - 页眉页脚：`<Header>`, `<Footer>`
  - 目录：`<Toc>` 自动生成
  - 分页：`<PageBreak>`
  - 页码：`<PageNumber>`
  - 文件包含：`<Include>`
  - 书签与引用：`<Bookmark>`, `<BookmarkRef>`, `<PageRef>`, `<Ref>`
  - 脚注尾注：`<Footnote>`, `<Endnote>`
  - 批注：`<Comment>`
  - 水印：`<Watermark>`
  - 图表：`<Chart>` 支持多种图表类型

- **高级特性**
  - React 风格函数组件
  - 完整的 JavaScript 逻辑支持
  - 数组映射和动态生成
  - 运行时数据上下文 (`--data` 参数)
  - 自定义子弹符号和编号格式

- **CLI 功能**
  - 单文件转换
  - 批量转换
  - 输出目录指定
  - Verbose 模式
  - 版本信息

- **MCP 支持**
  - MCP stdio 模式
  - `get_component_spec` 工具
  - `generate_docx` 工具
  - `convert_docx_to_jsx` 工具

- **文档和示例**
  - 完整组件规范 (`docs/spec.md`)
  - MCP 文档 (`docs/mcp.md`)
  - 函数组件说明 (`docs/function-components.md`)
  - 28+ 示例文件
  - 49+ 单元测试

- **CI/CD**
  - GitHub Actions 自动化测试
  - 自动发布工作流

### 技术栈 (Technical)
- Java 17
- Maven 3.9+
- SWC4J 1.8.0 (JSX 编译)
- GraalVM Polyglot 24.1.1 (JS 运行时)
- Apache POI 5.2.5 (DOCX 生成)
- Picocli 4.7.5 (CLI 框架)
- Jackson 2.15.3 (JSON 处理)

---

## 未来计划

查看 [ROADMAP.md](ROADMAP.md) 了解项目的长期发展规划。

### 即将到来 (v0.3.0)
- JAR 包体积优化（平台特定构建）
- MCP HTTP 服务器模式
- Docker 镜像支持
- npm 包装器
- 更好的错误提示

### 长期目标 (v1.0.0)
- VSCode 扩展
- 在线 Playground
- 官方模板库
- 企业级批处理功能
- PDF 直接输出

---

## 链接

- [项目主页](https://github.com/lihongjie0209/jsx-docx)
- [问题反馈](https://github.com/lihongjie0209/jsx-docx/issues)
- [贡献指南](CONTRIBUTING.md)
- [项目路线图](ROADMAP.md)

---

**格式说明：**
- `[新增]` - 新功能
- `[改进]` - 对现有功能的改进
- `[修复]` - Bug 修复
- `[废弃]` - 即将移除的功能
- `[移除]` - 已移除的功能
- `[安全]` - 安全相关的修复
