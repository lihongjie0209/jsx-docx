// Example: 19x19 Multiplication Table
// This example demonstrates generating a multiplication table using JSX loops and dynamic content generation
// The table is created using nested loops to generate all cells programmatically

// Run with: java -jar target/jsx-docx-1.0-SNAPSHOT-fat.jar examples/test-multiplication-table.jsx -o debug-output/multiplication-table.docx

<Document>
  <Section pageSize="A4" pageMarginTop="720" pageMarginBottom="720" pageMarginLeft="720" pageMarginRight="720">
    
    {/* Title */}
    <Paragraph align="center" bold="true" fontSize="14">
      <Text>19×19 Multiplication Table</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Table Header */}
    <Paragraph bold="true"><Text>×</Text></Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Generate multiplication table rows */}
    {(() => {
      const rows = [];
      for (let i = 1; i <= 19; i++) {
        const products = [];
        for (let j = 1; j <= 19; j++) {
          products.push(i * j);
        }
        // Format each row as a single line with products
        const line = products.map(p => "" + p).join("  ");
        rows.push(
          <Paragraph key={i} fontSize="9">
            <Text>{"Row " + i + ": " + line}</Text>
          </Paragraph>
        );
      }
      return rows;
    })()}
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Simple Alternative: Row-by-row display */}
    <Paragraph bold="true"><Text>Compact Format (1-19 rows)</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {(() => {
      const compactRows = [];
      for (let i = 1; i <= 19; i++) {
        const products = [];
        for (let j = 1; j <= 19; j++) {
          products.push(i * j);
        }
        // Format as: "1: 1 2 3 4 5 ... 19"
        const productStr = products.map(p => "" + p).join(" ");
        compactRows.push(
          <Paragraph key={i} fontSize="9">
            <Text>{"" + i + ": " + productStr}</Text>
          </Paragraph>
        );
      }
      return compactRows;
    })()}
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Statistics */}
    <Paragraph bold="true"><Text>Statistics</Text></Paragraph>
    <Paragraph><Text>Rows: 19</Text></Paragraph>
    <Paragraph><Text>Columns: 19</Text></Paragraph>
    <Paragraph><Text>{"Total cells: " + (19 * 19)}</Text></Paragraph>
    <Paragraph><Text>{"Maximum product: " + (19 * 19)}</Text></Paragraph>
    
  </Section>
</Document>
