# 贡献指南 / Contributing Guide

感谢您考虑为 jsx-docx 做出贡献！本文档提供了如何参与项目的指导。

## 🌟 贡献方式

您可以通过多种方式为项目做出贡献：

### 1. 报告 Bug
- 使用 GitHub Issues 报告问题
- 提供详细的复现步骤
- 包含您的环境信息（OS、Java 版本、jsx-docx 版本）
- 如果可能，提供最小化的复现示例

### 2. 功能建议
- 使用 GitHub Issues 提出新功能建议
- 说明该功能的使用场景和价值
- 讨论可能的实现方案

### 3. 改进文档
- 修复文档中的错误
- 添加缺失的文档
- 翻译文档到其他语言
- 添加示例和教程

### 4. 贡献代码
- 实现新功能
- 修复已知 Bug
- 优化性能
- 改进代码质量

### 5. 分享经验
- 编写博客文章
- 创建视频教程
- 分享使用案例
- 在社交媒体推广

---

## 🚀 快速开始

### 环境要求

- **Java**: 17 或更高版本
- **Maven**: 3.9 或更高版本
- **Git**: 任意版本
- **IDE**: IntelliJ IDEA 或 Eclipse（推荐）

### 克隆项目

```bash
git clone https://github.com/lihongjie0209/jsx-docx.git
cd jsx-docx
```

### 构建项目

```bash
# 编译源码
mvn clean compile

# 运行测试
mvn test

# 打包 Fat JAR
mvn package
```

### 运行示例

```bash
# 转换示例文件
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test.jsx -o output.docx

# 批量转换
java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/*.jsx -d output --verbose
```

---

## 📝 开发流程

### 1. Fork 和分支

```bash
# Fork 项目到您的 GitHub 账户
# 然后克隆您的 fork
git clone https://github.com/YOUR_USERNAME/jsx-docx.git
cd jsx-docx

# 添加上游仓库
git remote add upstream https://github.com/lihongjie0209/jsx-docx.git

# 创建新分支
git checkout -b feature/your-feature-name
# 或
git checkout -b fix/your-bug-fix
```

### 2. 编码规范

#### Java 代码风格
- 遵循 Google Java Style Guide
- 使用 4 个空格缩进（不使用 Tab）
- 行长度限制 120 字符
- 类和方法添加 Javadoc 注释
- 变量命名使用驼峰命名法（camelCase）

#### 提交信息规范
遵循 Conventional Commits 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式（不影响功能）
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建过程或辅助工具变动

**示例**：
```
feat(renderer): add support for DocumentProperties component

Implement <DocumentProperties> component to set document metadata
including title, author, subject, keywords, etc.

Closes #123
```

### 3. 编写测试

所有新功能和 Bug 修复都应该包含测试：

```java
@Test
void testYourFeature(@TempDir Path tempDir) throws Exception {
    String jsx = """
        <Document>
            <Section>
                <Paragraph><Text>Test content</Text></Paragraph>
            </Section>
        </Document>
        """;
    
    String compiled = new Compiler().compile(jsx);
    VNode vnode = new JsRuntime().run(compiled, null);
    XWPFDocument doc = new Renderer().render(vnode);
    
    // 验证结果
    assertNotNull(doc);
    assertEquals(1, doc.getParagraphs().size());
    
    // 保存文件（用于手动验证）
    Path outputPath = tempDir.resolve("output.docx");
    try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
        doc.write(out);
    }
}
```

### 4. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=YourTestClass

# 运行特定测试方法
mvn test -Dtest=YourTestClass#testMethod
```

### 5. 提交代码

```bash
# 添加修改的文件
git add .

# 提交（使用规范的提交信息）
git commit -m "feat(component): add new component"

# 推送到您的 fork
git push origin feature/your-feature-name
```

### 6. 创建 Pull Request

1. 访问您的 fork 页面
2. 点击 "New Pull Request"
3. 选择 base: `main` ← compare: `your-branch`
4. 填写 PR 标题和描述：
   - 清楚说明做了什么改动
   - 为什么需要这个改动
   - 如何测试
   - 相关的 Issue 编号
5. 等待代码审查

---

## 🎯 贡献任务列表

### 🔰 适合新手的任务

这些任务适合首次贡献者：

- [ ] 添加新的示例文档（`examples/` 目录）
- [ ] 改进现有示例的注释
- [ ] 修复文档中的拼写错误
- [ ] 翻译文档到英文或其他语言
- [ ] 为现有功能编写单元测试
- [ ] 改进错误消息的清晰度

查看标记为 `good first issue` 的 Issue。

### 🔧 中级任务

适合有一定经验的贡献者：

- [ ] 实现新的文档组件
- [ ] 优化现有组件的性能
- [ ] 添加新的 CLI 参数和功能
- [ ] 改进测试覆盖率
- [ ] 重构复杂的代码
- [ ] 添加集成测试

### 🚀 高级任务

适合经验丰富的贡献者：

- [ ] 架构重构和优化
- [ ] 实现新的主要特性（参考 ROADMAP.md）
- [ ] 性能优化和基准测试
- [ ] 创建开发者工具（VSCode 扩展等）
- [ ] 生态集成（Docker、npm 等）

---

## 🧪 测试指南

### 测试类型

1. **单元测试**：测试单个组件或方法
   - 位置：`src/test/java/cn/lihongjie/jsxdocx/`
   - 命名：`*Test.java`

2. **集成测试**：测试完整的转换流程
   - 包含 JSX 编译 → JS 运行 → DOCX 渲染

3. **示例测试**：验证示例文件能正常转换
   - 可以生成实际的 DOCX 文件用于手动验证

### 测试最佳实践

- 使用 `@TempDir` 创建临时目录
- 验证文档结构（段落数、表格行数等）
- 不要依赖 POI 的 XML 格式细节（容易变化）
- 提供有意义的断言消息
- 保持测试独立（不依赖其他测试）

---

## 📚 资源

### 项目文档
- [组件规范](docs/spec.md)
- [MCP 文档](docs/mcp.md)
- [函数组件说明](docs/function-components.md)
- [样式支持](docs/style-support.md)
- [项目路线图](ROADMAP.md)
- [待办事项](TODO.md)

### 技术文档
- [Apache POI 文档](https://poi.apache.org/components/document/)
- [GraalVM Polyglot](https://www.graalvm.org/latest/reference-manual/polyglot-programming/)
- [SWC4J 文档](https://github.com/caoccao/swc4j)
- [Picocli 文档](https://picocli.info/)

### 相关标准
- [Office Open XML (OOXML) 标准](https://www.ecma-international.org/publications-and-standards/standards/ecma-376/)
- [JSX 语法](https://react.dev/learn/writing-markup-with-jsx)
- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/)

---

## 🤝 代码审查

所有提交都需要经过代码审查：

### 审查关注点

1. **功能正确性**：代码是否实现了预期功能
2. **测试覆盖**：是否有充分的测试
3. **代码质量**：是否清晰、可维护
4. **性能影响**：是否引入性能问题
5. **文档完整性**：是否更新了相关文档
6. **向后兼容**：是否破坏了现有 API

### 审查流程

1. 自动化检查：CI/CD 运行测试和检查
2. 人工审查：维护者审查代码
3. 讨论和修改：根据反馈调整
4. 批准和合并：审查通过后合并

### 审查时间

我们会尽快审查 PR，通常在 2-3 个工作日内。如果超过 5 个工作日没有回应，请随时在 PR 中留言提醒。

---

## 🎉 贡献者致谢

感谢所有为 jsx-docx 做出贡献的人！

贡献者名单会在 README.md 和发布说明中展示。

---

## ❓ 获取帮助

如果您有任何问题：

- 📖 查看文档：[README.md](README.md) 和 [docs/](docs/)
- 💬 提问：在 GitHub Discussions 中提问
- 🐛 报告问题：创建 GitHub Issue
- 📧 联系维护者：通过 GitHub 私信

---

## 📜 许可证

通过提交代码，您同意您的贡献将在与项目相同的许可证下发布。

---

**再次感谢您的贡献！每一个贡献都让 jsx-docx 变得更好。** 🙏
