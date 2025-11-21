// Test TOC (Table of Contents) component
// Demonstrates Word TOC field with multi-level headings

<Document>
  <Styles>
    {/* Define heading styles with outline levels */}
    <Style 
      styleId="H1" 
      name="Heading 1"
      type="paragraph"
      outlineLevel={0}
      bold={true}
      fontSize={44}
      color="#2E75B5"
      spacingBefore={480}
      spacingAfter={240}
    />
    <Style 
      styleId="H2" 
      name="Heading 2"
      type="paragraph"
      outlineLevel={1}
      bold={true}
      fontSize={36}
      color="#2E75B5"
      spacingBefore={360}
      spacingAfter={180}
    />
    <Style 
      styleId="H3" 
      name="Heading 3"
      type="paragraph"
      outlineLevel={2}
      bold={true}
      fontSize={28}
      color="#2E75B5"
      spacingBefore={280}
      spacingAfter={140}
    />
    <Style 
      styleId="H4" 
      name="Heading 4"
      type="paragraph"
      outlineLevel={3}
      bold={true}
      fontSize={24}
      color="#2E75B5"
      spacingBefore={240}
      spacingAfter={120}
    />
    <Style 
      styleId="BodyText"
      name="Body Text"
      type="paragraph"
      spacingAfter={200}
    />
  </Styles>

  {/* Insert TOC with default settings */}
  <Toc title="目录" maxLevel={3} />
  
  <PageBreak />

  {/* Chapter 1 */}
  <Heading styleId="H1"><Text>第一章 项目概述</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>本章介绍项目的背景、目标和整体架构。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>1.1 项目背景</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>随着现代办公软件的发展，文档自动化生成变得越来越重要。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>1.1.1 市场需求</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>企业需要高效的文档生成工具来提高工作效率。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>1.1.2 技术演进</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>从模板填充到代码化文档生成，技术不断进步。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>1.2 项目目标</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>提供一个基于 JSX 的 Word 文档生成框架，支持现代化的编程范式。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>1.2.1 核心功能</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>支持样式定义、多级标题、表格、列表、图片等丰富组件。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>1.2.2 易用性</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>使用熟悉的 JSX 语法，降低学习成本。</Text>
  </Paragraph>

  <PageBreak />

  {/* Chapter 2 */}
  <Heading styleId="H1"><Text>第二章 系统架构</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>本章详细介绍系统的技术架构和实现细节。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>2.1 核心组件</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>系统由编译器、运行时和渲染器三大核心组件组成。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.1.1 编译器模块</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>使用 SWC4J 将 JSX 转换为可执行的 JavaScript 代码。</Text>
  </Paragraph>

  <Heading styleId="H4"><Text>2.1.1.1 语法转换</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>支持 JSX 语法糖、ES6+ 特性和 TypeScript 类型标注。</Text>
  </Paragraph>

  <Heading styleId="H4"><Text>2.1.1.2 优化处理</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>自动压缩代码、移除调试信息。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.1.2 运行时环境</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>基于 GraalVM Polyglot 提供 JavaScript 执行环境。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.1.3 渲染引擎</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>使用 Apache POI 生成符合 OOXML 标准的 DOCX 文件。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>2.2 数据流</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>JSX → JavaScript → VNode 树 → DOCX 文件的完整处理流程。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.2.1 编译阶段</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>JSX 文件通过编译器转换为标准 JavaScript 代码。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.2.2 执行阶段</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>JavaScript 代码在 GraalVM 中执行，生成 VNode 虚拟 DOM 树。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>2.2.3 渲染阶段</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>渲染引擎遍历 VNode 树，调用 POI API 生成 Word 文档。</Text>
  </Paragraph>

  <PageBreak />

  {/* Chapter 3 */}
  <Heading styleId="H1"><Text>第三章 功能特性</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>详细介绍系统支持的各种文档功能。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>3.1 样式系统</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>通过 Styles 和 Style 组件定义样式，支持段落、字符和表格三种样式类型。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>3.2 目录功能</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>使用 Toc 组件插入目录域，在 Word 中打开后自动更新。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>3.2.1 目录配置</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>支持自定义标题、级别范围、超链接和页码显示。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>3.2.2 自动更新</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>右键点击目录区域选择"更新域"即可刷新目录内容。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>3.3 列表支持</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>支持项目符号列表和编号列表，可配置缩进、符号字体等。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>3.4 表格功能</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>支持复杂表格布局、单元格合并、边框样式等。</Text>
  </Paragraph>

  <PageBreak />

  {/* Chapter 4 */}
  <Heading styleId="H1"><Text>第四章 使用指南</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>提供详细的使用说明和最佳实践。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>4.1 快速开始</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>从安装到生成第一个文档的完整步骤。</Text>
  </Paragraph>

  <Heading styleId="H2"><Text>4.2 高级用法</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>数据绑定、条件渲染、循环生成等高级技巧。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>4.2.1 数据绑定</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>通过全局 data 对象传递动态数据。</Text>
  </Paragraph>

  <Heading styleId="H3"><Text>4.2.2 条件渲染</Text></Heading>
  <Paragraph styleId="BodyText">
    <Text>使用 JavaScript 表达式控制内容是否显示。</Text>
  </Paragraph>
</Document>
