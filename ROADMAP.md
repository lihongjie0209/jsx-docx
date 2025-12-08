# jsx-docx 项目发展规划

> **当前版本**: v0.1.0  
> **最后更新**: 2024-12-08

本文档规划 jsx-docx 项目的后续发展方向，包括功能增强、性能优化、生态建设和长期愿景。

---

## 📊 当前状态评估

### ✅ 已完成的核心功能

#### 1. 基础架构
- ✅ JSX → JavaScript 编译（SWC4J）
- ✅ JavaScript 运行时（GraalVM Polyglot）
- ✅ DOCX 渲染引擎（Apache POI）
- ✅ CLI 命令行工具（Picocli）
- ✅ MCP 协议支持（stdio 模式）
- ✅ 运行时数据上下文（`--data` 参数）

#### 2. 文档组件（已实现 30+ 组件）
- ✅ 文档结构：`<Document>`, `<Section>`, `<Paragraph>`
- ✅ 文本格式：`<Text>` 支持粗体、斜体、颜色、字体等
- ✅ 样式系统：`<Styles>`, `<Style>` 支持自定义样式
- ✅ 标题：`<Heading>` 多级标题
- ✅ 表格：`<Table>`, `<Row>`, `<Cell>` 支持合并单元格、边框、背景色
- ✅ 列表：`<BulletedList>`, `<NumberedList>` 支持多级嵌套和自定义格式
- ✅ 图片：`<Image>` 支持自适应和最大尺寸
- ✅ 超链接：`<Link>` 
- ✅ 页眉页脚：`<Header>`, `<Footer>` 支持奇偶页和首页
- ✅ 目录：`<Toc>` 自动生成目录
- ✅ 分页：`<PageBreak>`
- ✅ 页码：`<PageNumber>`
- ✅ 文件包含：`<Include>` 支持外部文件引用
- ✅ 书签与引用：`<Bookmark>`, `<BookmarkRef>`, `<PageRef>`, `<Ref>`
- ✅ 脚注尾注：`<Footnote>`, `<Endnote>`
- ✅ 批注：`<Comment>`
- ✅ 水印：`<Watermark>`
- ✅ 图表：`<Chart>` 支持柱状图、折线图、饼图

#### 3. 高级特性
- ✅ React 风格函数组件（Props、Children、嵌套）
- ✅ 完整的 JavaScript 逻辑（循环、条件、函数）
- ✅ 数组映射和动态生成
- ✅ 自定义子弹符号和编号格式
- ✅ 表格固定列宽和自动布局
- ✅ 段落边框和背景色
- ✅ 制表位和制表符

#### 4. 文档和示例
- ✅ 完整组件规范文档（`docs/spec.md`）
- ✅ MCP 集成文档（`docs/mcp.md`, `MCP-QUICKSTART.md`）
- ✅ 函数组件说明（`docs/function-components.md`）
- ✅ 28+ 示例文件
- ✅ 49+ 单元测试
- ✅ CI/CD 工作流（GitHub Actions）

---

## 🎯 短期目标（1-2 个月）

### 优先级 P0：关键改进

#### 1. 性能优化
**目标**：减小 JAR 包体积，提升启动速度

- [ ] **减小 Fat JAR 体积**（当前约 120MB）
  - 排除非当前平台的 SWC4J 原生库
  - 实现平台检测和动态加载
  - 目标：减小到 40-60MB（仅包含当前平台）
  - **预期收益**：更快的下载和启动，更好的容器化部署体验

- [ ] **优化 GraalVM 上下文创建**
  - 实现上下文池或复用机制
  - 减少重复编译和初始化开销
  - **预期收益**：批量转换速度提升 30-50%

#### 2. 用户体验改进

- [ ] **标准输入支持**
  ```bash
  cat template.jsx | java -jar jsx-docx.jar --stdin -o output.docx
  echo "<Document>...</Document>" | java -jar jsx-docx.jar --stdin
  ```
  - **用途**：管道操作、脚本集成、临时文档生成

- [ ] **进度显示和日志优化**
  - 批量转换显示进度条
  - 结构化日志输出（JSON 格式选项）
  - 更清晰的错误信息和调试信息

- [ ] **生成报告模式**
  ```bash
  java -jar jsx-docx.jar *.jsx --report report.json
  ```
  - 输出包含文件名、状态、错误、耗时的 JSON 报告
  - 便于 CI/CD 集成和自动化测试

#### 3. 文档组件补充

- [ ] **条件格式组件**
  - `<ConditionalText>` - 基于条件显示不同内容
  - `<Switch>/<Case>` - 类似 JavaScript 的条件渲染

- [ ] **数学公式支持** ⚠️ 复杂度高
  - `<Equation>` - 支持 LaTeX 或 MathML
  - 基于 Apache POI 的 Office MathML 渲染

### 优先级 P1：重要功能

#### 4. MCP 功能完善

- [ ] **实现 HTTP/SSE 服务器模式**
  ```bash
  java -jar jsx-docx.jar --mcp-server --mcp-port=3000
  ```
  - 基于 Undertow 或 Javalin 的轻量级 HTTP 服务器
  - 支持 SSE (Server-Sent Events) 流式响应

- [ ] **新增 MCP 工具**
  - `validate_jsx` - 验证 JSX 语法和组件结构
  - `list_examples` - 列出所有示例文件和描述
  - `preview_template` - 生成预览（返回文档结构摘要）

- [ ] **提升 MCP 集成体验**
  - 更智能的错误提示和修复建议
  - 提供组件代码片段和模板库
  - AI Agent 友好的文档格式

---

## 🚀 中期目标（3-6 个月）

### 优先级 P1：功能扩展

#### 5. 高级文档特性

- [ ] **文档属性和元数据**
  - `<DocumentProperties>` - 作者、标题、关键词、版本等
  - 文档加密和权限控制
  - 自定义文档属性

- [ ] **样式模板系统**
  ```jsx
  <StyleTemplate name="corporate">
    <Style styleId="Heading1" fontSize={18} color="#003366" bold />
    <Style styleId="Normal" fontSize={11} fontFamily="Arial" />
  </StyleTemplate>
  ```
  - 预定义主题（商务、学术、创意等）
  - 样式继承和覆盖
  - 样式导入导出

- [ ] **内容控件支持**
  - `<ContentControl>` - 富文本、下拉、日期选择器
  - 表单域和数据绑定
  - 文档模板填充

#### 6. 扩展生态集成

- [ ] **Docker 镜像发布**
  ```bash
  docker run -v $(pwd):/workspace jsx-docx template.jsx -o output.docx
  ```
  - 多平台支持（amd64, arm64）
  - 精简基础镜像（Alpine + GraalVM native）
  - Docker Compose 示例

- [ ] **npm 包装器**
  ```javascript
  const jsxDocx = require('jsx-docx');
  await jsxDocx.convert('template.jsx', 'output.docx', { data: {...} });
  ```
  - Node.js 绑定（通过子进程调用 JAR）
  - TypeScript 类型定义
  - Webpack/Vite 插件

- [ ] **REST API 服务**
  ```bash
  java -jar jsx-docx.jar --rest-server --port=8080
  ```
  - RESTful API 端点
  - 文件上传和下载
  - 任务队列和异步处理
  - OpenAPI/Swagger 文档

#### 7. 开发者工具

- [ ] **VSCode 扩展**
  - JSX 语法高亮（针对 jsx-docx 组件）
  - 组件自动完成和属性提示
  - 实时预览（生成 DOCX 并用默认程序打开）
  - 语法检查和错误提示

- [ ] **在线 Playground**
  - 基于 Web 的 JSX 编辑器
  - 实时渲染和预览
  - 示例库和分享功能
  - 基于 WebAssembly + GraalVM

- [ ] **语言服务器协议（LSP）**
  - 支持多种编辑器（VSCode, Vim, Emacs, etc.）
  - 智能代码补全
  - 组件文档悬停提示
  - 定义跳转和重构

---

## 🌟 长期愿景（6-12 个月）

### 优先级 P2：生态建设

#### 8. 模板市场和社区

- [ ] **官方模板库**
  - 常用文档模板（简历、报告、合同、发票等）
  - 行业模板（法律、医疗、教育、金融）
  - 设计模板（海报、宣传册）

- [ ] **社区贡献平台**
  - GitHub Discussions 或论坛
  - 模板分享和评分
  - 最佳实践文档
  - 用户案例展示

#### 9. 企业级功能

- [ ] **批处理引擎**
  - 高性能并行处理
  - 任务调度和优先级
  - 分布式处理支持
  - 监控和告警

- [ ] **模板验证和测试**
  - 单元测试框架（针对 JSX 模板）
  - 视觉回归测试（对比 DOCX 渲染结果）
  - 性能基准测试

- [ ] **与 CMS/文档管理系统集成**
  - WordPress 插件
  - Confluence 集成
  - SharePoint 适配器

#### 10. 高级渲染特性

- [ ] **PDF 直接输出**
  - 跳过 DOCX 中间格式
  - 基于 Apache PDFBox 或 iText
  - 保留所有格式和布局

- [ ] **HTML 输出支持**
  - JSX → HTML 转换
  - 保留样式和结构
  - 用于 Web 预览

- [ ] **Markdown 双向转换**
  - JSX ↔ Markdown
  - 简化的写作流程
  - 与 Git 友好的版本控制

#### 11. 国际化和本地化

- [ ] **多语言文档生成**
  - 国际化（i18n）支持
  - 本地化（l10n）资源管理
  - RTL（从右到左）语言支持

- [ ] **字体嵌入和管理**
  - 自定义字体包
  - 字体子集化（减小文件大小）
  - 跨平台字体兼容性

---

## 🔧 技术债务和优化

### 代码质量

- [ ] **代码重构**
  - 拆分大型 `Renderer.java`（当前 2000+ 行）
  - 提取组件渲染器为独立类
  - 统一错误处理和日志记录

- [ ] **测试覆盖率提升**
  - 增加边缘情况测试
  - 性能回归测试
  - 集成测试增强

- [ ] **依赖更新**
  - GraalVM 升级到最新 LTS
  - Apache POI 5.3.x
  - SWC4J 跟进最新版本

### 文档改进

- [ ] **API 参考文档**
  - Java API 文档（Javadoc）
  - RESTful API 文档
  - 完整的组件属性表

- [ ] **教程和指南**
  - 入门教程系列
  - 进阶特性指南
  - 性能优化建议
  - 常见问题解答（FAQ）

- [ ] **多语言文档**
  - 英文版本（翻译 README 和主要文档）
  - 日语、韩语版本

---

## 📈 性能目标

### 短期目标（v0.2.0）
- JAR 包体积：120MB → 40-60MB
- 单文件转换：< 2 秒（简单文档）
- 批量转换：10 文件/秒

### 中期目标（v0.5.0）
- 冷启动时间：< 1 秒
- 内存占用：< 200MB（批量模式）
- 并发处理：支持 10+ 并发请求

### 长期目标（v1.0.0）
- 大文档支持：1000+ 页无压力
- 流式处理：边生成边写入
- 分布式处理：水平扩展支持

---

## 🎓 学习和研究方向

### 探索性项目

- [ ] **AI 辅助文档生成**
  - 基于用户意图自动生成 JSX 模板
  - 智能样式建议
  - 内容优化建议

- [ ] **可视化编辑器**
  - 所见即所得（WYSIWYG）编辑器
  - 拖拽式文档构建
  - 自动生成 JSX 代码

- [ ] **文档协作功能**
  - 实时多人编辑
  - 版本控制和历史
  - 评论和审阅

---

## 🗓️ 里程碑计划

### v0.2.0（预计 1 个月）
- ✅ 性能优化（减小 JAR 体积）
- ✅ 标准输入支持
- ✅ 进度显示优化
- ✅ 新增 2-3 个常用组件

### v0.3.0（预计 2-3 个月）
- ✅ MCP HTTP 服务器模式
- ✅ Docker 镜像
- ✅ npm 包装器
- ✅ REST API 基础版本

### v0.5.0（预计 4-6 个月）
- ✅ VSCode 扩展
- ✅ 官方模板库
- ✅ 样式模板系统
- ✅ 在线 Playground

### v1.0.0（预计 8-12 个月）
- ✅ 企业级功能完整
- ✅ 文档和示例完善
- ✅ 性能达标
- ✅ 生态系统成熟

---

## 🤝 社区贡献

我们欢迎社区贡献！以下是一些可以参与的方向：

### 初学者友好的任务
- 📝 添加新的示例文档
- 🐛 修复文档中的错误
- 🌐 翻译文档到其他语言
- ✅ 编写单元测试

### 中级任务
- 🎨 实现新的文档组件
- 🔧 优化现有组件性能
- 📦 创建官方模板
- 🧪 增强测试覆盖率

### 高级任务
- 🚀 架构重构和优化
- 🌟 实现新的主要特性
- 🔌 生态集成（插件、扩展）
- 📊 性能分析和优化

---

## 📞 反馈和建议

如果您有任何建议或想法，请通过以下方式联系我们：

- 📧 提交 GitHub Issue
- 💬 参与 GitHub Discussions
- 🐦 社交媒体 (@jsx-docx)

---

## 📝 变更日志

### 2024-12-08 - v0.1.0
- ✅ 初始版本发布
- ✅ 核心功能实现
- ✅ 30+ 文档组件
- ✅ MCP stdio 模式
- ✅ 28+ 示例文件
- ✅ CI/CD 工作流

---

**注意**：本路线图是动态文档，会根据用户反馈、技术发展和社区需求持续更新。优先级和时间表可能会调整。
