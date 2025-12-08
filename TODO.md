# jsx-docx 待办事项清单

> 本文档列出具体的待办任务，包含可操作的步骤和技术细节。  
> 对应 ROADMAP.md 中的规划目标。

---

## ✅ 已完成（v0.2.0）

- [x] **标准输入支持** ✅ 完成于 2024-12-08
  - [x] 在 `Main.java` 添加 `--stdin` 参数
  - [x] 实现从 `System.in` 读取 JSX 内容
  - [x] 支持管道操作：`cat template.jsx | java -jar jsx-docx.jar --stdin -o output.docx`
  - [x] 更新 README 文档

- [x] **优化批量转换进度显示** ✅ 完成于 2024-12-08
  - [x] 添加 `--progress` 参数（默认启用，`--no-progress` 禁用）
  - [x] 实现进度条显示
  - [x] 显示：当前文件名、已完成/总数、百分比

- [x] **生成报告模式** ✅ 完成于 2024-12-08
  - [x] 添加 `--report <file.json>` 参数
  - [x] 记录每个文件的转换结果（状态、输出路径、错误、耗时）
  - [x] 使用 Jackson 生成 JSON

- [x] **版本更新到 0.2.0** ✅ 完成于 2024-12-08
  - [x] 更新 pom.xml 版本号
  - [x] 更新 Main.java 版本信息
  - [x] 创建 CHANGELOG.md
  - [x] 更新所有文档中的版本引用

---

## 🔥 当前冲刺（Sprint 2 - v0.3.0）

### P0：必须完成

- [ ] **减小 Fat JAR 体积**
  - [ ] 实现平台检测逻辑（Windows/macOS/Linux, x86_64/arm64）
  - [ ] 修改 pom.xml，使用 Maven profiles 按平台构建
  - [ ] 创建构建脚本：`build-windows.sh`, `build-mac.sh`, `build-linux.sh`
  - [ ] 更新 GitHub Actions 工作流，发布多平台 JAR
  - [ ] 测试：验证每个平台包都能正常运行
  - **预计工作量**：4-6 小时
  - **预期结果**：JAR 从 120MB 降到 40-60MB

- [ ] **标准输入支持**
  - [ ] 在 `Main.java` 添加 `--stdin` 参数
  - [ ] 实现从 `System.in` 读取 JSX 内容
  - [ ] 支持管道操作：`cat template.jsx | java -jar jsx-docx.jar --stdin -o output.docx`
  - [ ] 添加单元测试（模拟标准输入）
  - [ ] 更新 README 文档
  - **预计工作量**：2-3 小时
  - **技术要点**：
    ```java
    if (stdin) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        String jsxContent = sb.toString();
        // 处理 jsxContent...
    }
    ```

- [ ] **优化批量转换进度显示**
  - [ ] 添加 `--progress` 参数（默认启用，`--no-progress` 禁用）
  - [ ] 使用 ASCII 进度条或百分比显示
  - [ ] 显示：当前文件名、已完成/总数、预估剩余时间
  - [ ] 依赖：`me.tongfei:progressbar` 或手动实现
  - **预计工作量**：2 小时
  - **示例输出**：
    ```
    Converting files: [===>      ] 3/10 (30%) - test.jsx - ETA: 12s
    ```

### P1：重要任务

- [ ] **生成报告模式**
  - [ ] 添加 `--report <file.json>` 参数
  - [ ] 记录每个文件的转换结果：
    ```json
    {
      "total": 10,
      "success": 9,
      "failed": 1,
      "results": [
        {"file": "test.jsx", "status": "success", "time_ms": 234, "output": "test.docx"},
        {"file": "broken.jsx", "status": "error", "error": "Syntax error at line 5", "time_ms": 45}
      ]
    }
    ```
  - [ ] 使用 Jackson 生成 JSON
  - [ ] 添加集成测试
  - **预计工作量**：3 小时

- [ ] **改进错误消息**
  - [ ] JSX 语法错误：显示行号、列号、错误代码片段
  - [ ] 运行时错误：提供修复建议和文档链接
  - [ ] 组件错误：指出哪个组件、哪个属性不正确
  - [ ] 创建 `ErrorFormatter` 工具类
  - **预计工作量**：4 小时
  - **示例**：
    ```
    Error: Invalid component property
      at <Paragraph> line 15, column 8
      
      <Paragraph align="justify" invalidProp="value">
                                  ^^^^^^^^^^^
      
      Unknown property 'invalidProp' for <Paragraph>
      Valid properties: align, styleId, before, after, line, indent*
      See: https://github.com/lihongjie0209/jsx-docx/docs/spec.md#paragraph
    ```

---

## 📋 下一个冲刺（Sprint 2）

### MCP 增强

- [ ] **实现 MCP HTTP 服务器模式**
  - [ ] 选择 HTTP 框架：Javalin（推荐）或 Undertow
  - [ ] 实现 SSE 端点：`/mcp/sse`
  - [ ] 实现 JSON-RPC 端点：`/mcp/rpc`
  - [ ] 添加健康检查端点：`/health`
  - [ ] 添加 `--mcp-server --mcp-port=3000` 参数
  - [ ] 添加 CORS 支持
  - [ ] 编写 MCP 服务器测试
  - **预计工作量**：8-10 小时
  - **依赖**：
    ```xml
    <dependency>
        <groupId>io.javalin</groupId>
        <artifactId>javalin</artifactId>
        <version>5.6.3</version>
    </dependency>
    ```

- [ ] **新增 MCP 工具：validate_jsx**
  - [ ] 验证 JSX 语法（编译测试）
  - [ ] 检查组件名称和属性
  - [ ] 返回错误和警告列表
  - [ ] 不生成 DOCX，只验证
  - **预计工作量**：3 小时

- [ ] **新增 MCP 工具：list_examples**
  - [ ] 扫描 `examples/` 目录
  - [ ] 提取每个示例的描述（从 JSX 注释）
  - [ ] 返回 JSON 格式的示例列表
  - **预计工作量**：2 小时

### 文档组件补充

- [ ] **条件文本组件**
  - [ ] 在 `runtime.js` 添加辅助函数：
    ```javascript
    function ConditionalText({ condition, then, otherwise }) {
        return condition ? then : (otherwise || '');
    }
    ```
  - [ ] 添加示例：`examples/test-conditional.jsx`
  - [ ] 更新 `docs/spec.md`
  - **预计工作量**：2 小时

- [ ] **文档属性组件**
  - [ ] 实现 `<DocumentProperties>` 组件
  - [ ] 在 `Renderer.java` 中设置 XWPFDocument 属性
  - [ ] 支持属性：title, author, subject, keywords, description, created, modified
  - [ ] 添加测试和示例
  - **预计工作量**：3 小时
  - **技术要点**：
    ```java
    case "documentproperties":
        POIXMLProperties props = doc.getProperties();
        CoreProperties coreProps = props.getCoreProperties();
        coreProps.setTitle(String.valueOf(node.getProps().get("title")));
        coreProps.setCreator(String.valueOf(node.getProps().get("author")));
        // ...
        break;
    ```

---

## 🔨 技术债务

### 代码质量

- [ ] **重构 Renderer.java**
  - [ ] 拆分为多个类：
    - `ParagraphRenderer.java`
    - `TableRenderer.java`
    - `ListRenderer.java`
    - `StyleRenderer.java`
    - `HeaderFooterRenderer.java`
  - [ ] 提取公共方法到 `RenderUtils.java`
  - [ ] 使用策略模式或责任链模式
  - **预计工作量**：12-16 小时
  - **优先级**：P2（不紧急但重要）

- [ ] **统一错误处理**
  - [ ] 创建自定义异常类：
    - `JsxCompilationException`
    - `JsxRuntimeException`
    - `DocxRenderException`
  - [ ] 全局异常处理器
  - [ ] 日志记录标准化（SLF4J）
  - **预计工作量**：4 小时

- [ ] **提升测试覆盖率**
  - [ ] 当前测试覆盖率：约 60%（估算）
  - [ ] 目标：80%+
  - [ ] 添加边缘情况测试
  - [ ] 添加负面测试（错误输入）
  - [ ] 添加性能基准测试
  - **预计工作量**：持续进行

### 文档改进

- [ ] **翻译 README 为英文**
  - [ ] 创建 `README.en.md`
  - [ ] 翻译所有章节
  - [ ] 同步更新两个版本
  - **预计工作量**：2 小时

- [ ] **完善 API 文档**
  - [ ] 为所有 public 类和方法添加 Javadoc
  - [ ] 生成 Javadoc HTML
  - [ ] 发布到 GitHub Pages
  - **预计工作量**：6 小时

- [ ] **编写入门教程**
  - [ ] 教程 1：基础文档生成
  - [ ] 教程 2：样式和格式
  - [ ] 教程 3：表格和列表
  - [ ] 教程 4：高级特性（函数组件、数据上下文）
  - [ ] 教程 5：MCP 集成
  - **预计工作量**：8-10 小时

---

## 🚀 生态建设

### Docker 支持

- [ ] **创建 Dockerfile**
  - [ ] 基础镜像：`eclipse-temurin:17-jre-alpine`
  - [ ] 安装 SWC4J 原生库（按平台）
  - [ ] 复制 Fat JAR
  - [ ] 设置入口点
  - **预计工作量**：2 小时
  - **Dockerfile 示例**：
    ```dockerfile
    FROM eclipse-temurin:17-jre-alpine
    WORKDIR /app
    COPY target/jsx-docx-1.0-SNAPSHOT-fat.jar jsx-docx.jar
    ENTRYPOINT ["java", "-jar", "jsx-docx.jar"]
    ```

- [ ] **创建 docker-compose.yml**
  - [ ] 示例：批量转换
  - [ ] 示例：MCP 服务器
  - [ ] 示例：REST API（未来）
  - **预计工作量**：1 小时

- [ ] **发布 Docker 镜像到 Docker Hub**
  - [ ] 配置 GitHub Actions 自动构建
  - [ ] 支持多平台：linux/amd64, linux/arm64
  - [ ] 版本标签：latest, v0.1.0, v0.2.0...
  - **预计工作量**：3 小时

### npm 包装器

- [ ] **创建 Node.js 包装器**
  - [ ] 项目结构：
    ```
    npm-package/
    ├── package.json
    ├── index.js
    ├── index.d.ts (TypeScript 类型)
    └── bin/
        └── jsx-docx (CLI wrapper)
    ```
  - [ ] 使用 `child_process` 调用 Java JAR
  - [ ] 异步 API：`convert(jsxFile, docxFile, options)`
  - [ ] CLI 包装器：`npx jsx-docx template.jsx`
  - **预计工作量**：4-6 小时

- [ ] **发布到 npm**
  - [ ] 包名：`jsx-docx` 或 `@jsx-docx/core`
  - [ ] 自动发布工作流（GitHub Actions）
  - [ ] README 和文档
  - **预计工作量**：2 小时

---

## 📊 性能优化任务

- [ ] **实现 GraalVM 上下文池**
  - [ ] 创建上下文池（最大 10 个实例）
  - [ ] 复用上下文减少初始化开销
  - [ ] 添加性能基准测试对比
  - **预计工作量**：6 小时
  - **预期收益**：批量转换速度提升 30-50%

- [ ] **优化编译缓存**
  - [ ] 缓存编译后的 JavaScript（基于文件内容 hash）
  - [ ] 使用 Caffeine 或 Guava Cache
  - [ ] 添加 `--no-cache` 参数禁用缓存
  - **预计工作量**：4 小时

- [ ] **并行处理支持**
  - [ ] 批量转换时使用线程池
  - [ ] 添加 `--parallel <n>` 参数（并发数）
  - [ ] 确保线程安全
  - **预计工作量**：5 小时

---

## 🎨 开发者工具

### VSCode 扩展

- [ ] **创建扩展项目**
  - [ ] 使用 Yeoman 生成器：`yo code`
  - [ ] 语言 ID：`jsx-docx`
  - [ ] 文件扩展名：`.jsx`
  - **预计工作量**：1 小时

- [ ] **语法高亮**
  - [ ] 基于 JSX/JavaScript 语法
  - [ ] 高亮 jsx-docx 组件名称
  - [ ] 创建 TextMate 语法文件
  - **预计工作量**：3 小时

- [ ] **代码片段**
  - [ ] 常用组件片段（`doc`, `para`, `table`...）
  - [ ] 创建 `snippets.json`
  - **预计工作量**：2 小时

- [ ] **实时预览**
  - [ ] 添加命令：`jsx-docx.preview`
  - [ ] 调用 CLI 生成 DOCX
  - [ ] 使用系统默认程序打开
  - **预计工作量**：3 小时

- [ ] **发布到 VSCode Marketplace**
  - [ ] 配置发布账号
  - [ ] 编写 README 和截图
  - [ ] 自动发布工作流
  - **预计工作量**：2 小时

---

## 📦 依赖更新计划

- [ ] **GraalVM Polyglot**：24.1.1 → 25.0.x（等待稳定版本）
- [ ] **Apache POI**：5.2.5 → 5.3.0（关注发布）
- [ ] **SWC4J**：1.8.0 → 最新版本（定期检查）
- [ ] **Jackson**：2.15.3 → 2.16.x
- [ ] **JUnit**：5.10.1 → 5.11.x
- [ ] **Picocli**：4.7.5 → 4.7.6（如果有）

**更新策略**：
- 每月检查依赖更新
- 优先更新安全补丁
- 主要版本更新需要充分测试

---

## 🎯 关键指标追踪

### 性能指标
- **JAR 包大小**：120MB → 目标 40-60MB
- **单文件转换时间**：当前 ~2s → 目标 <1.5s
- **批量转换吞吐量**：当前 ~5 文件/秒 → 目标 10 文件/秒
- **内存占用**：当前 ~300MB → 目标 <200MB
- **冷启动时间**：当前 ~3s → 目标 <1s

### 质量指标
- **测试覆盖率**：当前 ~60% → 目标 80%+
- **已知 Bug 数量**：当前 0 → 保持 <5
- **文档完整度**：当前 70% → 目标 95%

### 生态指标
- **GitHub Stars**：追踪增长
- **npm 下载量**：发布后追踪
- **Docker 拉取次数**：发布后追踪
- **社区贡献者**：目标 5+ 贡献者

---

## 📅 时间线

### 2024 年 12 月
- ✅ 完成 P0 任务（JAR 优化、标准输入、进度显示）
- ✅ 发布 v0.2.0

### 2025 年 1 月
- ✅ 完成 MCP HTTP 服务器
- ✅ Docker 支持
- ✅ 发布 v0.3.0

### 2025 年 2 月
- ✅ npm 包装器
- ✅ VSCode 扩展基础版本
- ✅ 代码重构

### 2025 年 3 月
- ✅ REST API 服务
- ✅ 模板库初版
- ✅ 发布 v0.5.0

---

## 💡 其他想法（Backlog）

以下是一些尚未排期的想法，可根据用户需求调整优先级：

- [ ] 支持 EPUB 输出
- [ ] 支持 ODT（OpenDocument Text）格式
- [ ] 实现文档比较和合并功能
- [ ] 支持宏和 VBA（安全考虑）
- [ ] 移动端适配（React Native 绑定）
- [ ] Python 绑定（基于 Jep 或子进程）
- [ ] 可视化文档编辑器（Web 版）
- [ ] AI 智能模板推荐
- [ ] 文档搜索和索引功能
- [ ] 与 Git 集成（版本控制）
- [ ] 支持 WebAssembly 编译（浏览器端运行）

---

**注意**：本清单持续更新，完成的任务会标记为 ✅。新任务会根据用户反馈和项目需求添加。
