// Simple example of custom styles

const doc = (
  <Document>
    <Styles>
      <Style 
        styleId="H1" 
        name="My Heading 1"
        outlineLevel={0}
        bold={true}
        fontSize={44}
        color="#0066CC"
      />
      <Style 
        styleId="H2" 
        name="My Heading 2"
        outlineLevel={1}
        bold={true}
        fontSize={32}
        color="#0066CC"
      />
      <Style 
        styleId="MyTitle" 
        name="My Title"
        bold={true}
        fontSize={60}
        color="#CC0000"
        spacingAfter={600}
      />
    </Styles>

    <Section>
      <Heading styleId="MyTitle">
        <Text>Welcome</Text>
      </Heading>

      <Heading styleId="H1">
        <Text>Chapter 1</Text>
      </Heading>
      
      <Paragraph>
        <Text>This is a paragraph under heading 1.</Text>
      </Paragraph>

      <Heading styleId="H2">
        <Text>Section 1.1</Text>
      </Heading>
      
      <Paragraph>
        <Text>This is a paragraph under heading 2.</Text>
      </Paragraph>
    </Section>
  </Document>
);

render(doc);
