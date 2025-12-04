/**
 * Footnote and Endnote Example
 * 
 * Demonstrates the usage of Footnote and Endnote components:
 * - Basic footnote (content at page bottom)
 * - Basic endnote (content at document end)
 * - Multiple footnotes/endnotes
 * - Mixed usage in academic style
 */

const data = {};

<Document>
  <Section>
    {/* Title */}
    <Paragraph>
      <Text bold={true} size={32}>Footnote and Endnote Demo</Text>
    </Paragraph>
    
    <Paragraph>
      <Text size={24} color="#666666">脚注与尾注功能演示</Text>
    </Paragraph>
    
    {/* Section 1: Basic Footnote */}
    <Paragraph>
      <Text bold={true} size={28}>1. Basic Footnote (基本脚注)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>This is a paragraph with a footnote reference</Text>
      <Footnote text="This is the footnote content that appears at the bottom of the page." />
      <Text>. Footnotes are commonly used for brief explanations or citations.</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>脚注是一种常用的文档注释方式</Text>
      <Footnote text="脚注内容会显示在页面底部，通常用于补充说明或引用来源。" />
      <Text>，特别适合学术论文和正式文档。</Text>
    </Paragraph>
    
    {/* Section 2: Basic Endnote */}
    <Paragraph>
      <Text bold={true} size={28}>2. Basic Endnote (基本尾注)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>Endnotes work similarly to footnotes but appear at the end of the document</Text>
      <Endnote text="This endnote content will appear at the end of the document, not at the bottom of the page." />
      <Text>. They are useful for longer explanations.</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>尾注与脚注类似，但内容显示在文档末尾</Text>
      <Endnote text="尾注适合较长的注释内容，不会打断页面的阅读流程。" />
      <Text>。</Text>
    </Paragraph>
    
    {/* Section 3: Multiple Footnotes */}
    <Paragraph>
      <Text bold={true} size={28}>3. Multiple Footnotes (多个脚注)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>A single paragraph can contain multiple footnotes</Text>
      <Footnote text="First footnote: This is the first reference." />
      <Text> to reference different sources</Text>
      <Footnote text="Second footnote: This is the second reference." />
      <Text> or provide various explanations</Text>
      <Footnote text="Third footnote: This is the third reference." />
      <Text>.</Text>
    </Paragraph>
    
    {/* Section 4: Multiple Endnotes */}
    <Paragraph>
      <Text bold={true} size={28}>4. Multiple Endnotes (多个尾注)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>Similarly, you can have multiple endnotes</Text>
      <Endnote text="Endnote A: First endnote reference." />
      <Text> in the same paragraph</Text>
      <Endnote text="Endnote B: Second endnote reference." />
      <Text>.</Text>
    </Paragraph>
    
    {/* Section 5: Mixed Usage */}
    <Paragraph>
      <Text bold={true} size={28}>5. Mixed Footnotes and Endnotes (混合使用)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>You can mix footnotes</Text>
      <Footnote text="This is a footnote in mixed usage." />
      <Text> and endnotes</Text>
      <Endnote text="This is an endnote in mixed usage." />
      <Text> in the same document or even the same paragraph.</Text>
    </Paragraph>
    
    {/* Section 6: Academic Style Example */}
    <Paragraph>
      <Text bold={true} size={28}>6. Academic Style Example (学术论文示例)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>According to recent research</Text>
      <Footnote text="Smith, J. (2024). 'The Impact of Technology on Modern Communication'. Journal of Digital Studies, 15(3), 45-67." />
      <Text>, the use of digital communication has increased by 300% over the past decade. This finding is supported by multiple studies</Text>
      <Footnote text="Johnson, M. & Williams, K. (2023). 'Digital Transformation in the 21st Century'. Tech Review Quarterly, 8(2), 112-128." />
      <Text> that examine the changing patterns of human interaction.</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>根据最新研究</Text>
      <Endnote text="张三. (2024). '数字时代的文档处理技术'. 信息技术研究, 12(4), 78-95." />
      <Text>，使用 JSX 语法生成 Word 文档可以大幅提高开发效率。这一结论得到了业界广泛认可</Text>
      <Endnote text="李四, 王五. (2023). '现代办公自动化实践'. 软件工程学报, 20(1), 33-48." />
      <Text>。</Text>
    </Paragraph>
    
    {/* Conclusion */}
    <Paragraph>
      <Text bold={true} size={28}>Conclusion (总结)</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>Footnotes and endnotes are powerful tools for adding supplementary information to your documents. Use footnotes for brief page-specific notes and endnotes for longer references that you want to collect at the end of the document.</Text>
    </Paragraph>
    
    <Paragraph>
      <Text>脚注和尾注是为文档添加补充信息的强大工具。脚注适合简短的页面级注释，尾注则适合需要在文档末尾集中展示的较长参考内容。</Text>
    </Paragraph>
  </Section>
</Document>
