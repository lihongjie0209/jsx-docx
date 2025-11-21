// Example: Invoice Document
// This example demonstrates creating a professional invoice matching the reference design
// Features: company header, bill-to section, itemized table, totals, and payment terms

<Document>
  <Section pageSize="A4" pageMarginTop="1440" pageMarginBottom="1440" pageMarginLeft="1440" pageMarginRight="1440">
    
    {/* ===== Header: Company Info and Logo Placeholder ===== */}
    <Table width="100%" border={false}>
      <Row>
        <Cell width="60%" border={false} vAlign="top">
          <Paragraph bold="true" fontSize="14"><Text>Your Company Inc.</Text></Paragraph>
          <Paragraph fontSize="10"><Text>1234 Company St.</Text></Paragraph>
          <Paragraph fontSize="10"><Text>Company Town, ST 12345</Text></Paragraph>
        </Cell>
        <Cell width="40%" border={false} vAlign="top">
          <Paragraph align="right"><Text> </Text></Paragraph>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Invoice Title ===== */}
    <Paragraph align="right" bold="true" fontSize="32">
      <Text style={{color: "#C8906D"}}>INVOICE</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Bill To and Invoice Details ===== */}
    <Table width="100%" border={false}>
      <Row>
        {/* Left: Bill To */}
        <Cell width="50%" border={false} vAlign="top">
          <Paragraph bold="true" fontSize="10">
            <Text style={{color: "#C8906D"}}>Bill To</Text>
          </Paragraph>
          <Paragraph bold="true" fontSize="11"><Text>Customer Name</Text></Paragraph>
          <Paragraph fontSize="10"><Text>1234 Customer St.</Text></Paragraph>
          <Paragraph fontSize="10"><Text>Customer Town, ST 12345</Text></Paragraph>
        </Cell>
        
        {/* Right: Invoice Details */}
        <Cell width="50%" border={false} vAlign="top">
          <Table width="100%" border={false}>
            <Row>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10">
                  <Text bold="true" style={{color: "#C8906D"}}>Invoice #</Text>
                </Paragraph>
              </Cell>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10"><Text>0000007</Text></Paragraph>
              </Cell>
            </Row>
            <Row>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10">
                  <Text bold="true" style={{color: "#C8906D"}}>Invoice date</Text>
                </Paragraph>
              </Cell>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10"><Text>10-02-2023</Text></Paragraph>
              </Cell>
            </Row>
            <Row>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10">
                  <Text bold="true" style={{color: "#C8906D"}}>Due date</Text>
                </Paragraph>
              </Cell>
              <Cell width="50%" border={false}>
                <Paragraph align="right" fontSize="10"><Text>10-16-2023</Text></Paragraph>
              </Cell>
            </Row>
          </Table>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Line Items Table ===== */}
    {(() => {
      const items = [
        { qty: "1.00", description: "Replacement of spark plugs", unitPrice: "40.00", amount: "$40.00" },
        { qty: "2.00", description: "Brake pad replacement (front)", unitPrice: "40.00", amount: "$80.00" },
        { qty: "4.00", description: "Wheel alignment", unitPrice: "17.50", amount: "$70.00" },
        { qty: "2.00", description: "Mechanic's rate per hour", unitPrice: "30.00", amount: "$60.00" }
      ];
      
      const rows = [];
      
      // Header row with terracotta/copper color
      rows.push(
        <Row key="header" header="true">
          <Cell width="12%" background="#C8906D" vAlign="center" padding={{top: 8, bottom: 8, left: 8, right: 8}}>
            <Paragraph align="left" bold="true">
              <Text style={{color: "#FFFFFF"}} fontSize="10">QTY</Text>
            </Paragraph>
          </Cell>
          <Cell width="48%" background="#C8906D" vAlign="center" padding={{top: 8, bottom: 8, left: 8, right: 8}}>
            <Paragraph align="left" bold="true">
              <Text style={{color: "#FFFFFF"}} fontSize="10">Description</Text>
            </Paragraph>
          </Cell>
          <Cell width="20%" background="#C8906D" vAlign="center" padding={{top: 8, bottom: 8, left: 8, right: 8}}>
            <Paragraph align="right" bold="true">
              <Text style={{color: "#FFFFFF"}} fontSize="10">Unit Price</Text>
            </Paragraph>
          </Cell>
          <Cell width="20%" background="#C8906D" vAlign="center" padding={{top: 8, bottom: 8, left: 8, right: 8}}>
            <Paragraph align="right" bold="true">
              <Text style={{color: "#FFFFFF"}} fontSize="10">Amount</Text>
            </Paragraph>
          </Cell>
        </Row>
      );
      
      // Item rows - white background
      items.forEach((item, idx) => {
        rows.push(
          <Row key={"item-" + idx}>
            <Cell width="12%" background="#FFFFFF" vAlign="center" padding={{top: 6, bottom: 6, left: 8, right: 8}}>
              <Paragraph align="left" fontSize="10"><Text>{item.qty}</Text></Paragraph>
            </Cell>
            <Cell width="48%" background="#FFFFFF" vAlign="center" padding={{top: 6, bottom: 6, left: 8, right: 8}}>
              <Paragraph align="left" fontSize="10"><Text>{item.description}</Text></Paragraph>
            </Cell>
            <Cell width="20%" background="#FFFFFF" vAlign="center" padding={{top: 6, bottom: 6, left: 8, right: 8}}>
              <Paragraph align="right" fontSize="10"><Text>{item.unitPrice}</Text></Paragraph>
            </Cell>
            <Cell width="20%" background="#FFFFFF" vAlign="center" padding={{top: 6, bottom: 6, left: 8, right: 8}}>
              <Paragraph align="right" fontSize="10"><Text>{item.amount}</Text></Paragraph>
            </Cell>
          </Row>
        );
      });
      
      return <Table width="100%">{rows}</Table>;
    })()}
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Totals Section ===== */}
    <Table width="100%" border={false}>
      <Row>
        <Cell width="60%" border={false}><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%" border={false}>
          <Paragraph align="right" fontSize="10"><Text>Subtotal</Text></Paragraph>
        </Cell>
        <Cell width="20%" border={false}>
          <Paragraph align="right" fontSize="10"><Text>$250.00</Text></Paragraph>
        </Cell>
      </Row>
      <Row>
        <Cell width="60%" border={false}><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%" border={false}>
          <Paragraph align="right" fontSize="10"><Text>Sales Tax (5%)</Text></Paragraph>
        </Cell>
        <Cell width="20%" border={false}>
          <Paragraph align="right" fontSize="10"><Text>$12.50</Text></Paragraph>
        </Cell>
      </Row>
      <Row>
        <Cell width="60%" border={false}><Paragraph><Text> </Text></Paragraph></Cell>
        <Cell width="20%" border={{size: 1, color: "#C8906D", sides: ["top"]}} padding={{top: 6, bottom: 6}}>
          <Paragraph align="right" bold="true" fontSize="11">
            <Text style={{color: "#C8906D"}}>Total (USD)</Text>
          </Paragraph>
        </Cell>
        <Cell width="20%" border={{size: 1, color: "#C8906D", sides: ["top"]}} padding={{top: 6, bottom: 6}}>
          <Paragraph align="right" bold="true" fontSize="11">
            <Text style={{color: "#C8906D"}}>$262.50</Text>
          </Paragraph>
        </Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== Terms and Conditions Section ===== */}
    <Paragraph bold="true" fontSize="10">
      <Text style={{color: "#C8906D"}}>Terms and Conditions</Text>
    </Paragraph>
    <Paragraph fontSize="10"><Text>Payment is due in 14 days</Text></Paragraph>
    <Paragraph fontSize="10"><Text>Please make checks payable to: Your Company Inc.</Text></Paragraph>
    
  </Section>
</Document>
