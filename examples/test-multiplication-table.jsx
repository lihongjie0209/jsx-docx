// Example: 19x19 Multiplication Table
// This example demonstrates generating a multiplication table using JSX loops and table components
// The table is created using nested loops to generate all rows and cells programmatically

// Run with: java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test-multiplication-table.jsx -o debug-output/multiplication-table.docx

<Document>
  <Section pageSize="A4" pageMarginTop="720" pageMarginBottom="720" pageMarginLeft="720" pageMarginRight="720">
    
    {/* Title */}
    <Paragraph align="center" bold="true" fontSize="14">
      <Text>19×19 Multiplication Table</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Generate multiplication table */}
    {(() => {
      const rows = [];
      
      // Header row with column numbers
      const headerCells = [];
      headerCells.push(
        <Cell key="header-0" background="#4472C4" width="5%">
          <Paragraph align="center" bold="true" fontSize="10">
            <Text style={{color: "#FFFFFF"}}>×</Text>
          </Paragraph>
        </Cell>
      );
      
      for (let j = 1; j <= 19; j++) {
        headerCells.push(
          <Cell key={"header-" + j} background="#4472C4" width="5%">
            <Paragraph align="center" bold="true" fontSize="9">
              <Text style={{color: "#FFFFFF"}}>{"" + j}</Text>
            </Paragraph>
          </Cell>
        );
      }
      
      rows.push(
        <Row key="header" header="true">
          {headerCells}
        </Row>
      );
      
      // Data rows
      for (let i = 1; i <= 19; i++) {
        const cells = [];
        
        // First cell: row number
        cells.push(
          <Cell key={"cell-" + i + "-0"} background="#D9E1F2" width="5%">
            <Paragraph align="center" bold="true" fontSize="9">
              <Text>{"" + i}</Text>
            </Paragraph>
          </Cell>
        );
        
        // Data cells
        for (let j = 1; j <= 19; j++) {
          const product = i * j;
          const bgColor = (i + j) % 2 === 0 ? "#F2F2F2" : "#FFFFFF";
          cells.push(
            <Cell key={"cell-" + i + "-" + j} background={bgColor} width="5%">
              <Paragraph align="center" fontSize="8">
                <Text>{"" + product}</Text>
              </Paragraph>
            </Cell>
          );
        }
        
        rows.push(
          <Row key={"row-" + i}>
            {cells}
          </Row>
        );
      }
      
      return <Table width="100%">{rows}</Table>;
    })()}
    
  </Section>
</Document>
