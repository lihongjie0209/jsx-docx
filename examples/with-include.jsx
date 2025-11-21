// Demonstration of <Include> component
// This example shows how to include reusable components from separate files

render(
  <Document>
    {/* Include company header from external file */}
    <Include path="./components/header.jsx" />

    <Paragraph bold="true" fontSize="14">
      <Text>Sales Report - Q4 2025</Text>
    </Paragraph>
    <Paragraph>
      <Text> </Text>
    </Paragraph>

    <Paragraph>
      <Text>This document demonstrates the Include component functionality.</Text>
    </Paragraph>
    <Paragraph>
      <Text>The header and footer sections are loaded from separate JSX files.</Text>
    </Paragraph>
    <Paragraph>
      <Text> </Text>
    </Paragraph>

    <Table border={{size: 1, color: "#000000"}} width="100%">
      <Row header={true}>
        <Cell background="#C8906D"><Paragraph bold="true"><Text>Quarter</Text></Paragraph></Cell>
        <Cell background="#C8906D"><Paragraph bold="true"><Text>Revenue</Text></Paragraph></Cell>
        <Cell background="#C8906D"><Paragraph bold="true"><Text>Growth</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>Q1</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>$1,250,000</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>+12%</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>Q2</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>$1,450,000</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>+16%</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>Q3</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>$1,680,000</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>+15.9%</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>Q4</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>$1,920,000</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>+14.3%</Text></Paragraph></Cell>
      </Row>
    </Table>

    <Paragraph>
      <Text> </Text>
    </Paragraph>
    <Paragraph>
      <Text> </Text>
    </Paragraph>

    {/* Include footer from external file */}
    <Include path="./components/footer.jsx" />
  </Document>
);
