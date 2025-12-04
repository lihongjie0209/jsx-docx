/**
 * 书签与交叉引用示例 (Bookmark & Cross-Reference Examples)
 * 
 * 演示如何使用 <Bookmark> 和 <BookmarkRef> 组件创建文档内部导航和引用
 */

// 示例1：基础书签和页码引用
const BasicBookmarkExample = (
  <Document>
    <Section>
      <Heading level={1}>
        <Text>书签与交叉引用示例</Text>
      </Heading>
      
      {/* 创建书签 */}
      <Paragraph>
        <Bookmark name="introduction">
          <Text bold={true}>第一章：引言</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>这是引言部分的内容。书签已在标题处创建，可以在文档其他位置引用此位置。</Text>
      </Paragraph>
      
      <Paragraph>
        <Bookmark name="methods">
          <Text bold={true}>第二章：方法</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>这是方法论部分的内容。</Text>
      </Paragraph>
      
      <Paragraph>
        <Bookmark name="results">
          <Text bold={true}>第三章：结果</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>这是结果部分的内容。</Text>
      </Paragraph>
      
      {/* 在文档中创建交叉引用 */}
      <Paragraph>
        <Text bold={true}>内部引用示例：</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 引言位于第 </Text>
        <BookmarkRef name="introduction" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 方法论位于第 </Text>
        <BookmarkRef name="methods" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 结果位于第 </Text>
        <BookmarkRef name="results" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
    </Section>
  </Document>
);

// 示例2：内容引用 (REF 类型)
const ContentRefExample = (
  <Document>
    <Section>
      <Heading level={1}>
        <Text>内容引用示例</Text>
      </Heading>
      
      <Paragraph>
        <Bookmark name="key_finding">
          <Text italic={true}>"本研究发现，使用JSX语法可以显著提高文档生成效率"</Text>
        </Bookmark>
      </Paragraph>
      
      <Paragraph>
        <Text>上述研究结论 (</Text>
        <BookmarkRef name="key_finding" type="ref" />
        <Text>) 已被多项后续研究验证。</Text>
      </Paragraph>
      
      <Paragraph>
        <Bookmark name="important_data">
          <Text bold={true}>95%</Text>
        </Bookmark>
        <Text> 的用户表示满意。</Text>
      </Paragraph>
      
      <Paragraph>
        <Text>用户满意度达到 </Text>
        <BookmarkRef name="important_data" type="ref" />
        <Text>，这是一个令人振奋的结果。</Text>
      </Paragraph>
    </Section>
  </Document>
);

// 示例3：自定义文本引用
const CustomTextExample = (
  <Document>
    <Section>
      <Heading level={1}>
        <Text>自定义文本引用</Text>
      </Heading>
      
      <Paragraph>
        <Text>以下是本文档的章节导航：</Text>
      </Paragraph>
      
      <Paragraph>
        <Bookmark name="appendix_a">
          <Text bold={true}>附录A：技术规格</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>详细的技术参数说明...</Text>
      </Paragraph>
      
      <Paragraph>
        <Bookmark name="appendix_b">
          <Text bold={true}>附录B：数据表格</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>完整的实验数据...</Text>
      </Paragraph>
      
      <Paragraph>
        <Text>如需查看技术规格，请点击</Text>
        <BookmarkRef name="appendix_a" type="text" text="【技术规格】" />
        <Text>；如需查看实验数据，请点击</Text>
        <BookmarkRef name="appendix_b" type="text" text="【数据表格】" />
        <Text>。</Text>
      </Paragraph>
    </Section>
  </Document>
);

// 示例4：不带超链接的引用
const NoHyperlinkExample = (
  <Document>
    <Section>
      <Heading level={1}>
        <Text>无超链接引用</Text>
      </Heading>
      
      <Paragraph>
        <Bookmark name="table1">
          <Text bold={true}>表1：销售数据</Text>
        </Bookmark>
      </Paragraph>
      
      <Table>
        <Row header={true}>
          <Cell><Text bold={true}>产品</Text></Cell>
          <Cell><Text bold={true}>销量</Text></Cell>
        </Row>
        <Row>
          <Cell><Text>产品A</Text></Cell>
          <Cell><Text>1000</Text></Cell>
        </Row>
        <Row>
          <Cell><Text>产品B</Text></Cell>
          <Cell><Text>2000</Text></Cell>
        </Row>
      </Table>
      
      <Paragraph>
        <Text>如表 </Text>
        <BookmarkRef name="table1" type="ref" hyperlink={false} />
        <Text> (第 </Text>
        <BookmarkRef name="table1" type="pageref" hyperlink={false} />
        <Text> 页) 所示，产品B的销量更高。</Text>
      </Paragraph>
    </Section>
  </Document>
);

// 示例5：综合文档 - 带有手动目录和交叉引用
const ComprehensiveExample = (
  <Document>
    <Section>
      {/* 封面 */}
      <Heading level={1}>
        <Text>项目报告</Text>
      </Heading>
      <Paragraph align="CENTER">
        <Text>使用书签和交叉引用的完整示例</Text>
      </Paragraph>
      
      {/* 手动目录 */}
      <Paragraph>
        <Bookmark name="toc">
          <Text bold={true} fontSize={14}>目录</Text>
        </Bookmark>
      </Paragraph>
      <Paragraph>
        <Text>1. 项目概述 ............... 第 </Text>
        <BookmarkRef name="overview" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      <Paragraph>
        <Text>2. 实施方案 ............... 第 </Text>
        <BookmarkRef name="implementation" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      <Paragraph>
        <Text>3. 预期成果 ............... 第 </Text>
        <BookmarkRef name="expected_results" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      <Paragraph>
        <Text>4. 时间计划 ............... 第 </Text>
        <BookmarkRef name="timeline" type="pageref" />
        <Text> 页</Text>
      </Paragraph>
      
      {/* 正文章节 */}
      <Heading level={2}>
        <Bookmark name="overview">
          <Text>1. 项目概述</Text>
        </Bookmark>
      </Heading>
      <Paragraph>
        <Text>本项目旨在开发一套基于JSX语法的文档生成系统。系统将支持多种文档格式输出，并提供丰富的格式化选项。</Text>
      </Paragraph>
      
      <Heading level={2}>
        <Bookmark name="implementation">
          <Text>2. 实施方案</Text>
        </Bookmark>
      </Heading>
      <Paragraph>
        <Text>根据</Text>
        <BookmarkRef name="overview" type="text" text="项目概述" />
        <Text>中的目标，我们制定了以下实施方案：</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 阶段一：需求分析与设计</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 阶段二：核心功能开发</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 阶段三：测试与优化</Text>
      </Paragraph>
      
      <Heading level={2}>
        <Bookmark name="expected_results">
          <Text>3. 预期成果</Text>
        </Bookmark>
      </Heading>
      <Paragraph>
        <Text>完成</Text>
        <BookmarkRef name="implementation" type="text" text="实施方案" />
        <Text>后，预期达成以下成果：</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 完整的文档生成库</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 详细的API文档</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 示例项目集合</Text>
      </Paragraph>
      
      <Heading level={2}>
        <Bookmark name="timeline">
          <Text>4. 时间计划</Text>
        </Bookmark>
      </Heading>
      <Paragraph>
        <Text>项目总周期：6个月</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 第1-2个月：完成</Text>
        <BookmarkRef name="overview" type="text" text="概述" />
        <Text>阶段工作</Text>
      </Paragraph>
      <Paragraph>
        <Text>• 第3-4个月：执行</Text>
        <BookmarkRef name="implementation" type="text" text="实施方案" />
      </Paragraph>
      <Paragraph>
        <Text>• 第5-6个月：验证</Text>
        <BookmarkRef name="expected_results" type="text" text="预期成果" />
      </Paragraph>
      
      {/* 返回目录的引用 */}
      <Paragraph align="RIGHT">
        <Text italic={true}>返回</Text>
        <BookmarkRef name="toc" type="text" text="目录" />
      </Paragraph>
    </Section>
  </Document>
);

// 导出综合示例
ComprehensiveExample;
