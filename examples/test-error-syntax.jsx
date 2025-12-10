// This file intentionally contains a syntax error to demonstrate error messages
// Run with: java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-syntax.jsx

<Document>
  <Section>
    <Paragraph>
      <Text>This text is properly closed</Text>
    </Paragraph>
    <Paragraph>
      <Text>This text is missing a closing tag
    </Paragraph>
  </Section>
</Document>
