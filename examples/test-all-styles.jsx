// Comprehensive example demonstrating style support across all components

const doc = (
  <Document>
    <Styles>
      {/* Heading styles */}
      <Style 
        styleId="Title" 
        name="Title"
        type="paragraph"
        bold={true}
        fontSize={56}
        color="#CC0000"
        spacingAfter={400}
      />
      
      <Style 
        styleId="Heading1" 
        name="Heading 1"
        type="paragraph"
        outlineLevel={0}
        bold={true}
        fontSize={44}
        color="#0066CC"
        keepNext={true}
        spacingBefore={240}
        spacingAfter={120}
      />
      
      {/* Paragraph styles */}
      <Style 
        styleId="Quote" 
        name="Quote"
        type="paragraph"
        italic={true}
        color="#666666"
        fontSize={22}
        spacingBefore={120}
        spacingAfter={120}
      />
      
      <Style 
        styleId="CodeBlock" 
        name="Code Block"
        type="paragraph"
        fontFamily="Consolas"
        fontSize={20}
        color="#000000"
      />
      
      {/* Character (text) styles */}
      <Style 
        styleId="Highlight" 
        name="Highlight"
        type="character"
        bold={true}
        color="#FF6600"
      />
      
      <Style 
        styleId="CodeText" 
        name="Code Text"
        type="character"
        fontFamily="Courier New"
        fontSize={20}
        color="#CC0000"
      />
      
      {/* Table style */}
      <Style 
        styleId="DataTable" 
        name="Data Table"
        type="table"
      />
    </Styles>

    <Section>
      {/* Title using custom style */}
      <Heading styleId="Title">
        <Text>Style System Demo</Text>
      </Heading>

      {/* Heading using custom style */}
      <Heading styleId="Heading1">
        <Text>1. Paragraph Styles</Text>
      </Heading>
      
      {/* Regular paragraph */}
      <Paragraph>
        <Text>This is a regular paragraph with </Text>
        <Text styleId="Highlight">highlighted text</Text>
        <Text> and </Text>
        <Text styleId="CodeText">inline code</Text>
        <Text> using character styles.</Text>
      </Paragraph>

      {/* Paragraph with custom style */}
      <Paragraph styleId="Quote">
        <Text>"This is a quote paragraph using the Quote style definition."</Text>
      </Paragraph>

      {/* Code block paragraph with custom style */}
      <Paragraph styleId="CodeBlock">
        <Text>function hello() {'{'}</Text>
      </Paragraph>
      <Paragraph styleId="CodeBlock">
        <Text>  console.log("Hello, World!");</Text>
      </Paragraph>
      <Paragraph styleId="CodeBlock">
        <Text>{'}'}</Text>
      </Paragraph>

      <Heading styleId="Heading1">
        <Text>2. Text (Character) Styles</Text>
      </Heading>
      
      <Paragraph>
        <Text>You can apply character styles to </Text>
        <Text styleId="Highlight">specific text runs</Text>
        <Text> without affecting the entire paragraph. For example, </Text>
        <Text styleId="CodeText">variable_name</Text>
        <Text> can be styled as code.</Text>
      </Paragraph>

      <Heading styleId="Heading1">
        <Text>3. Table Styles</Text>
      </Heading>
      
      <Paragraph>
        <Text>Tables can also reference custom styles:</Text>
      </Paragraph>

      {/* Table with custom style */}
      <Table styleId="DataTable" border={{size: 1, color: "#0066CC"}}>
        <Row>
          <Cell>
            <Paragraph styleId="Heading1">
              <Text bold={true}>Component</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph styleId="Heading1">
              <Text bold={true}>Style Support</Text>
            </Paragraph>
          </Cell>
        </Row>
        <Row>
          <Cell>
            <Paragraph>
              <Text styleId="CodeText">{'<Paragraph>'}</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph>
              <Text>✓ Paragraph styles via </Text>
              <Text styleId="CodeText">styleId</Text>
            </Paragraph>
          </Cell>
        </Row>
        <Row>
          <Cell>
            <Paragraph>
              <Text styleId="CodeText">{'<Text>'}</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph>
              <Text>✓ Character styles via </Text>
              <Text styleId="CodeText">styleId</Text>
            </Paragraph>
          </Cell>
        </Row>
        <Row>
          <Cell>
            <Paragraph>
              <Text styleId="CodeText">{'<Heading>'}</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph>
              <Text>✓ Heading styles via </Text>
              <Text styleId="CodeText">styleId</Text>
            </Paragraph>
          </Cell>
        </Row>
        <Row>
          <Cell>
            <Paragraph>
              <Text styleId="CodeText">{'<Table>'}</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph>
              <Text>✓ Table styles via </Text>
              <Text styleId="CodeText">styleId</Text>
            </Paragraph>
          </Cell>
        </Row>
        <Row>
          <Cell>
            <Paragraph>
              <Text styleId="CodeText">{'<Cell>'}</Text>
            </Paragraph>
          </Cell>
          <Cell>
            <Paragraph>
              <Text>✓ Cell paragraph styles via </Text>
              <Text styleId="CodeText">styleId</Text>
            </Paragraph>
          </Cell>
        </Row>
      </Table>

      <Heading styleId="Heading1">
        <Text>4. Combining Styles and Direct Props</Text>
      </Heading>
      
      <Paragraph>
        <Text>Styles and direct properties can be combined. For example:</Text>
      </Paragraph>
      
      <Paragraph styleId="Quote" align="CENTER">
        <Text>This quote is centered using direct prop + Quote style.</Text>
      </Paragraph>
      
      <Paragraph>
        <Text styleId="Highlight">This uses Highlight style</Text>
        <Text> but you can also add </Text>
        <Text styleId="Highlight" underline={true}>underline via direct prop</Text>
        <Text>.</Text>
      </Paragraph>
    </Section>
  </Document>
);

render(doc);
