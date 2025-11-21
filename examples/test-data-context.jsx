// Example: Data Context Usage
// This example demonstrates passing a JSON data context to jsx-docx

// Run with: java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test-data-context.jsx --data examples/data-context.json -o debug-output/data-context-example.docx

<Document>
  <Section pageSize="A4">
    {/* Title from data */}
    <Paragraph align="center" bold="true" fontSize="16">
      <Text>{data.title}</Text>
    </Paragraph>
    
    {/* Metadata from data */}
    <Paragraph>
      <Text bold="true">Author: </Text>
      <Text>{data.author}</Text>
    </Paragraph>
    
    <Paragraph>
      <Text bold="true">Date: </Text>
      <Text>{data.date}</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Summary from data */}
    <Paragraph bold="true"><Text>Summary</Text></Paragraph>
    <Paragraph><Text>{data.summary}</Text></Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Dynamic list from data.items array */}
    <Paragraph bold="true"><Text>Items</Text></Paragraph>
    <BulletedList>
      {data.items.map((item) => (
        <ListItem level="0">
          <Paragraph><Text>{item}</Text></Paragraph>
        </ListItem>
      ))}
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Contact information from nested object */}
    <Paragraph bold="true"><Text>Contact Information</Text></Paragraph>
    <Paragraph><Text>Name: {data.contact.name}</Text></Paragraph>
    <Paragraph><Text>Email: {data.contact.email}</Text></Paragraph>
    <Paragraph><Text>Phone: {data.contact.phone}</Text></Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Footnote from data */}
    <Paragraph italic="true"><Text>{data.footer}</Text></Paragraph>
  </Section>
</Document>
