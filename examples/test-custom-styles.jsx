// Test custom styles with <Styles> and <Style> components

const doc = (
  <Document>
    <Styles>
      <Style 
        styleId="Heading1" 
        name="Heading 1"
        type="paragraph"
        outlineLevel={0}
        bold={true}
        fontSize={44}
        color="#2E74B5"
        keepNext={true}
        keepLines={true}
        spacingBefore={340}
        spacingAfter={330}
      />
      <Style 
        styleId="Heading2" 
        name="Heading 2"
        type="paragraph"
        outlineLevel={1}
        bold={true}
        fontSize={32}
        color="#2E74B5"
        keepNext={true}
        spacingBefore={260}
        spacingAfter={260}
      />
      <Style 
        styleId="Heading3" 
        name="Heading 3"
        type="paragraph"
        outlineLevel={2}
        bold={true}
        fontSize={28}
        keepNext={true}
        spacingBefore={260}
        spacingAfter={260}
      />
      <Style 
        styleId="Heading4" 
        name="Heading 4"
        type="paragraph"
        outlineLevel={3}
        bold={true}
        fontSize={24}
        italic={true}
      />
      <Style 
        styleId="Title" 
        name="Title"
        type="paragraph"
        bold={true}
        fontSize={56}
        color="#17365D"
        spacingAfter={400}
      />
      <Style 
        styleId="Emphasis" 
        name="Emphasis"
        type="character"
        italic={true}
        color="#C00000"
      />
    </Styles>

    <Section>
      <Heading styleId="Title">
        <Text>Document with Custom Styles</Text>
      </Heading>

      <Heading styleId="Heading1">
        <Text>1. Introduction</Text>
      </Heading>
      
      <Paragraph>
        <Text>This document demonstrates the new </Text>
        <Text italic={true}>custom styles</Text>
        <Text> feature using JSX-based style definitions.</Text>
      </Paragraph>

      <Heading styleId="Heading2">
        <Text>1.1 Overview</Text>
      </Heading>
      
      <Paragraph>
        <Text>Styles are now defined using the </Text>
        <Text bold={true}>{'<Styles>'}</Text>
        <Text> component with nested </Text>
        <Text bold={true}>{'<Style>'}</Text>
        <Text> components.</Text>
      </Paragraph>

      <Heading styleId="Heading3">
        <Text>1.1.1 Benefits</Text>
      </Heading>
      
      <Paragraph>
        <Text>• Type-safe JSX syntax</Text>
      </Paragraph>
      <Paragraph>
        <Text>• No manual XML writing required</Text>
      </Paragraph>
      <Paragraph>
        <Text>• Consistent with other jsx-docx components</Text>
      </Paragraph>

      <Heading styleId="Heading4">
        <Text>1.1.1.1 Example Usage</Text>
      </Heading>
      
      <Paragraph>
        <Text>This is level 4 heading with italic style.</Text>
      </Paragraph>

      <Heading styleId="Heading1">
        <Text>2. Style Properties</Text>
      </Heading>

      <Heading styleId="Heading2">
        <Text>2.1 Paragraph Styles</Text>
      </Heading>
      
      <Paragraph>
        <Text>Paragraph styles support: </Text>
        <Text bold={true}>outlineLevel</Text>
        <Text>, </Text>
        <Text bold={true}>keepNext</Text>
        <Text>, </Text>
        <Text bold={true}>keepLines</Text>
        <Text>, </Text>
        <Text bold={true}>spacingBefore</Text>
        <Text>, </Text>
        <Text bold={true}>spacingAfter</Text>
        <Text>, </Text>
        <Text bold={true}>lineSpacing</Text>
      </Paragraph>

      <Heading styleId="Heading2">
        <Text>2.2 Text Formatting</Text>
      </Heading>
      
      <Paragraph>
        <Text>Text properties include: </Text>
        <Text bold={true}>bold</Text>
        <Text>, </Text>
        <Text italic={true}>italic</Text>
        <Text>, </Text>
        <Text bold={true}>fontSize</Text>
        <Text>, </Text>
        <Text bold={true}>color</Text>
        <Text>, </Text>
        <Text bold={true}>fontFamily</Text>
      </Paragraph>

      <Heading styleId="Heading1">
        <Text>3. Conclusion</Text>
      </Heading>
      
      <Paragraph>
        <Text>The new style system provides a clean, JSX-based approach to defining document styles, making it easier to create professional-looking documents with consistent formatting.</Text>
      </Paragraph>
    </Section>
  </Document>
);

render(doc);
