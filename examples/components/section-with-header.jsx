// Nested include example - includes another component that also uses Include

<Document>
  <Paragraph bold="true" fontSize="12">
    <Text>Section Header with Nested Includes</Text>
  </Paragraph>
  <Paragraph>
    <Text> </Text>
  </Paragraph>
  
  {/* This will include header.jsx which is standalone */}
  <Include path="./header.jsx" />
  
  <Paragraph fontSize="10" italic="true">
    <Text>This demonstrates nested include capability.</Text>
  </Paragraph>
</Document>
