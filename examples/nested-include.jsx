// Test nested includes
render(
  <Document>
    <Paragraph bold="true" fontSize="14">
      <Text>Nested Include Test</Text>
    </Paragraph>
    <Paragraph>
      <Text> </Text>
    </Paragraph>

    {/* This includes a file that also uses Include */}
    <Include path="./components/section-with-header.jsx" />

    <Paragraph>
      <Text>Main content area with tables and data.</Text>
    </Paragraph>

    <Include path="./components/footer.jsx" />
  </Document>
);
