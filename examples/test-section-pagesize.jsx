// Section pageSize and orientation demonstration
// This file demonstrates how pageSize and orientation work together correctly
// When orientation is "landscape", the width and height are automatically swapped

render(
  <Document>
    {/* A4 Portrait (default) - 210mm × 297mm */}
    <Section pageSize="A4" orientation="portrait">
      <Heading level={1}>A4 Portrait (Default)</Heading>
      <Paragraph>
        <Text>Page size: 210mm × 297mm (Width × Height)</Text>
      </Paragraph>
      <Paragraph>
        <Text>Internal dimensions: 11900 × 16840 twips</Text>
      </Paragraph>
      <Paragraph>
        <Text>This is the default portrait orientation.</Text>
      </Paragraph>
    </Section>

    {/* Page break between sections */}
    <PageBreak />

    {/* A4 Landscape - dimensions are swapped automatically */}
    <Section pageSize="A4" orientation="landscape">
      <Heading level={1}>A4 Landscape (Swapped Dimensions)</Heading>
      <Paragraph>
        <Text>Page size: 297mm × 210mm (Width × Height)</Text>
      </Paragraph>
      <Paragraph>
        <Text>Internal dimensions: 16840 × 11900 twips (automatically swapped)</Text>
      </Paragraph>
      <Paragraph>
        <Text>When orientation="landscape" is specified, the width and height are automatically exchanged.</Text>
      </Paragraph>
      <Paragraph>
        <Text>This ensures the page displays correctly in landscape mode.</Text>
      </Paragraph>
    </Section>

    {/* Page break */}
    <PageBreak />

    {/* Letter Portrait */}
    <Section pageSize="LETTER" orientation="portrait">
      <Heading level={1}>Letter Portrait</Heading>
      <Paragraph>
        <Text>Page size: 8.5" × 11" (Width × Height)</Text>
      </Paragraph>
      <Paragraph>
        <Text>Internal dimensions: 12240 × 15840 twips</Text>
      </Paragraph>
      <Paragraph>
        <Text>Standard US letter size in portrait orientation.</Text>
      </Paragraph>
    </Section>

    {/* Page break */}
    <PageBreak />

    {/* Letter Landscape - dimensions are swapped automatically */}
    <Section pageSize="LETTER" orientation="landscape">
      <Heading level={1}>Letter Landscape (Swapped Dimensions)</Heading>
      <Paragraph>
        <Text>Page size: 11" × 8.5" (Width × Height)</Text>
      </Paragraph>
      <Paragraph>
        <Text>Internal dimensions: 15840 × 12240 twips (automatically swapped)</Text>
      </Paragraph>
      <Paragraph>
        <Text>US letter size in landscape mode with automatically swapped dimensions.</Text>
      </Paragraph>
    </Section>

    {/* Page break */}
    <PageBreak />

    {/* Combined with custom margins */}
    <Section 
      pageSize="A4" 
      orientation="landscape"
      margins={{ top: 0.5, bottom: 0.5, left: 0.75, right: 0.75 }}
    >
      <Heading level={1}>A4 Landscape with Custom Margins</Heading>
      <Paragraph>
        <Text bold={true}>Page Configuration:</Text>
      </Paragraph>
      <BulletedList>
        <ListItem>
          <Paragraph><Text>Page size: A4 (297mm × 210mm in landscape)</Text></Paragraph>
        </ListItem>
        <ListItem>
          <Paragraph><Text>Orientation: Landscape (dimensions auto-swapped)</Text></Paragraph>
        </ListItem>
        <ListItem>
          <Paragraph><Text>Top margin: 0.5 inches (720 twips)</Text></Paragraph>
        </ListItem>
        <ListItem>
          <Paragraph><Text>Bottom margin: 0.5 inches (720 twips)</Text></Paragraph>
        </ListItem>
        <ListItem>
          <Paragraph><Text>Left margin: 0.75 inches (1080 twips)</Text></Paragraph>
        </ListItem>
        <ListItem>
          <Paragraph><Text>Right margin: 0.75 inches (1080 twips)</Text></Paragraph>
        </ListItem>
      </BulletedList>
      <Paragraph>
        <Text>This demonstrates that pageSize, orientation, and margins all work together correctly.</Text>
      </Paragraph>
    </Section>
  </Document>
);
