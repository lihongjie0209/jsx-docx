// Example: Invoice Document
// This example demonstrates creating a professional invoice using jsx-docx
// with company info, customer details, line items table, and totals

<Document>
  <Section pageSize="A4" pageMarginTop="1440" pageMarginBottom="1440" pageMarginLeft="1440" pageMarginRight="1440">
    
    {/* ===== Header Section: Company Info and Invoice Title ===== */}
    <Table width="100%">
      <Row>
        {/* Left: Company Info */}
        <Cell width="50%" vAlign="top">
          <Paragraph bold="true" fontSize="16"><Text>Your Company Inc.</Text></Paragraph>
          <Paragraph fontSize="11"><Text>1234 Company St.</Text></Paragraph>
          <Paragraph fontSize="11"><Text>Company Town, ST 12345</Text></Paragraph>
        </Cell>
        
        {/* Right: Invoice Title */}
        <Cell width="50%" vAlign="top">
          <Paragraph align="right" bold="true" fontSize="24">
            <Text style={{color: "#C97D5C"}}>INVOICE</Text>
          </Paragraph>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Bill To Section ===== */}
    <Table width="100%">
      <Row>
        {/* Bill To */}
        <Cell width="50%" vAlign="top">
          <Paragraph bold="true" fontSize="11" style={{color: "#C97D5C"}}>
            <Text>Bill To</Text>
          </Paragraph>
          <Paragraph fontSize="11"><Text>Customer Name</Text></Paragraph>
          <Paragraph fontSize="11"><Text>1234 Customer St.</Text></Paragraph>
          <Paragraph fontSize="11"><Text>Customer Town, ST 12345</Text></Paragraph>
        </Cell>
        
        {/* Invoice Details */}
        <Cell width="50%" vAlign="top">
          <Table width="100%">
            <Row>
              <Cell width="50%" background="#F5F5F5"><Paragraph align="right"><Text bold="true" fontSize="11">Invoice #</Text></Paragraph></Cell>
              <Cell width="50%"><Paragraph align="right"><Text fontSize="11">0000007</Text></Paragraph></Cell>
            </Row>
            <Row>
              <Cell width="50%" background="#F5F5F5"><Paragraph align="right"><Text bold="true" fontSize="11">Invoice date</Text></Paragraph></Cell>
              <Cell width="50%"><Paragraph align="right"><Text fontSize="11">10-02-2023</Text></Paragraph></Cell>
            </Row>
            <Row>
              <Cell width="50%" background="#F5F5F5"><Paragraph align="right"><Text bold="true" fontSize="11">Due date</Text></Paragraph></Cell>
              <Cell width="50%"><Paragraph align="right"><Text fontSize="11">10-16-2023</Text></Paragraph></Cell>
            </Row>
          </Table>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Line Items Table ===== */}
    {(() => {
      const items = [
        { qty: 1.00, description: "Replacement of spark plugs", unitPrice: 40.00, amount: 40.00 },
        { qty: 2.00, description: "Brake pad replacement (front)", unitPrice: 40.00, amount: 80.00 },
        { qty: 4.00, description: "Wheel alignment", unitPrice: 17.50, amount: 70.00 },
        { qty: 2.00, description: "Mechanic's rate per hour", unitPrice: 30.00, amount: 60.00 }
      ];
      
      const rows = [];
      
      // Header row
      rows.push(
        <Row key="header" header="true">
          <Cell width="8%" background="#C97D5C" vAlign="center">
            <Paragraph align="center"><Text bold="true" style={{color: "white"}} fontSize="11">QTY</Text></Paragraph>
          </Cell>
          <Cell width="50%" background="#C97D5C" vAlign="center">
            <Paragraph align="left"><Text bold="true" style={{color: "white"}} fontSize="11">Description</Text></Paragraph>
          </Cell>
          <Cell width="21%" background="#C97D5C" vAlign="center">
            <Paragraph align="right"><Text bold="true" style={{color: "white"}} fontSize="11">Unit Price</Text></Paragraph>
          </Cell>
          <Cell width="21%" background="#C97D5C" vAlign="center">
            <Paragraph align="right"><Text bold="true" style={{color: "white"}} fontSize="11">Amount</Text></Paragraph>
          </Cell>
        </Row>
      );
      
      // Item rows
      items.forEach((item, idx) => {
        const bgColor = idx % 2 === 0 ? "#FAFAFA" : "#FFFFFF";
        rows.push(
          <Row key={"item-" + idx}>
            <Cell width="8%" background={bgColor} vAlign="center">
              <Paragraph align="center"><Text fontSize="10">{"" + item.qty.toFixed(2)}</Text></Paragraph>
            </Cell>
            <Cell width="50%" background={bgColor} vAlign="center">
              <Paragraph align="left"><Text fontSize="10">{item.description}</Text></Paragraph>
            </Cell>
            <Cell width="21%" background={bgColor} vAlign="center">
              <Paragraph align="right"><Text fontSize="10">{"$" + item.unitPrice.toFixed(2)}</Text></Paragraph>
            </Cell>
            <Cell width="21%" background={bgColor} vAlign="center">
              <Paragraph align="right"><Text fontSize="10">{"$" + item.amount.toFixed(2)}</Text></Paragraph>
            </Cell>
          </Row>
        );
      });
      
      return <Table width="100%">{rows}</Table>;
    })()}
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Totals Section ===== */}
    <Table width="100%">
      <Row>
        <Cell width="60%"><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%"><Paragraph align="right"><Text bold="true" fontSize="11">Subtotal</Text></Paragraph></Cell>
        <Cell width="20%"><Paragraph align="right"><Text fontSize="11">$250.00</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell width="60%"><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%"><Paragraph align="right"><Text fontSize="11">Sales Tax (5%)</Text></Paragraph></Cell>
        <Cell width="20%"><Paragraph align="right"><Text fontSize="11">$12.50</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell width="60%"><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%" background="#F5F5F5" vAlign="center">
          <Paragraph align="right"><Text bold="true" fontSize="12" style={{color: "#C97D5C"}}>Total (USD)</Text></Paragraph>
        </Cell>
        <Cell width="20%" background="#F5F5F5" vAlign="center">
          <Paragraph align="right"><Text bold="true" fontSize="12" style={{color: "#C97D5C"}}>$262.50</Text></Paragraph>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Terms and Conditions Section ===== */}
    <Paragraph bold="true" fontSize="11" style={{color: "#C97D5C"}}>
      <Text>Terms and Conditions</Text>
    </Paragraph>
    <Paragraph fontSize="10"><Text>Payment is due in 14 days</Text></Paragraph>
    <Paragraph fontSize="10"><Text>Please make checks payable to: Your Company Inc.</Text></Paragraph>
    
  </Section>
</Document>
