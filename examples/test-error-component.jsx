// This file intentionally contains a component validation error
// Run with: java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-component.jsx

// Error: Root element must be Document, not Section
<Section>
  <Paragraph>
    <Text>This won't work because Section is not a valid root element</Text>
  </Paragraph>
</Section>
