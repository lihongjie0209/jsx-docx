// This file intentionally contains a runtime error to demonstrate error messages
// Run with: java -jar target/jsx-docx-0.2.0-fat.jar examples/test-error-runtime.jsx

<Document>
  <Section>
    <Paragraph>
      <Text>Trying to use undefined variable: {undefinedVariable}</Text>
    </Paragraph>
  </Section>
</Document>
