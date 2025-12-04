// Test watermark component with various configurations
// Run: java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test-watermark.jsx -o output-watermark.docx

// Example 1: Basic watermark with default settings
<Document>
    <Watermark text="机密" />
    <Section>
        <Paragraph>
            <Text bold={true} size={16}>基本水印示例</Text>
        </Paragraph>
        <Paragraph>
            <Text>这是一个带有默认设置水印的文档。</Text>
        </Paragraph>
        <Paragraph>
            <Text>水印文字为"机密"，使用默认颜色（灰色）、字体大小和角度。</Text>
        </Paragraph>
    </Section>
</Document>
