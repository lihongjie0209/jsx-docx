// Custom indent configuration examples

<Document>
  <Section pageSize="A4">
    <Paragraph align="center" bold="true" fontSize="16">
      <Text>Custom Indent Configuration Examples</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 1: Default indent (420 + 360*level) */}
    <Paragraph bold="true"><Text>1. Default Indent</Text></Paragraph>
    <Paragraph><Text>indentLeft=420, indentIncrement=360, indentHanging=420</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text>Level 0: 420 twips left indent</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: 780 twips left indent (420 + 360)</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: 1140 twips left indent (420 + 720)</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: 1500 twips left indent (420 + 1080)</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 2: Larger base indent */}
    <Paragraph bold="true"><Text>2. Larger Base Indent</Text></Paragraph>
    <Paragraph><Text>indentLeft=720 (0.5 inch), indentIncrement=360, indentHanging=420</Text></Paragraph>
    <BulletedList indentLeft="720">
      <ListItem level="0"><Paragraph><Text>Level 0: More space from margin</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: Consistently larger indent</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: Even more indent</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 3: Smaller increment */}
    <Paragraph bold="true"><Text>3. Smaller Increment (Compact)</Text></Paragraph>
    <Paragraph><Text>indentLeft=420, indentIncrement=240, indentHanging=420</Text></Paragraph>
    <BulletedList indentIncrement="240">
      <ListItem level="0"><Paragraph><Text>Level 0: Standard start</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: Smaller jump (240 twips)</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: More compact hierarchy</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: Saves horizontal space</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 4: Larger increment */}
    <Paragraph bold="true"><Text>4. Larger Increment (Spacious)</Text></Paragraph>
    <Paragraph><Text>indentLeft=420, indentIncrement=720, indentHanging=420</Text></Paragraph>
    <BulletedList indentIncrement="720">
      <ListItem level="0"><Paragraph><Text>Level 0: Standard start</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: Large jump (720 twips = 0.5 inch)</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: Very clear hierarchy</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 5: Custom hanging indent */}
    <Paragraph bold="true"><Text>5. Custom Hanging Indent</Text></Paragraph>
    <Paragraph><Text>indentLeft=420, indentIncrement=360, indentHanging=600</Text></Paragraph>
    <BulletedList indentHanging="600">
      <ListItem level="0"><Paragraph><Text>Larger hanging indent makes bullet stand out more</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Text wraps further from bullet</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Good for long text with multiple lines</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 6: Minimal hanging indent */}
    <Paragraph bold="true"><Text>6. Minimal Hanging Indent</Text></Paragraph>
    <Paragraph><Text>indentLeft=420, indentIncrement=360, indentHanging=240</Text></Paragraph>
    <BulletedList indentHanging="240">
      <ListItem level="0"><Paragraph><Text>Smaller hanging indent</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Bullet closer to text</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>More compact appearance</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 7: All custom values */}
    <Paragraph bold="true"><Text>7. All Custom Values</Text></Paragraph>
    <Paragraph><Text>indentLeft=500, indentIncrement=500, indentHanging=300</Text></Paragraph>
    <BulletedList indentLeft="500" indentIncrement="500" indentHanging="300">
      <ListItem level="0"><Paragraph><Text>Level 0: 500 twips</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: 1000 twips (500 + 500)</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: 1500 twips (500 + 1000)</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: 2000 twips (500 + 1500)</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 8: Combined with custom bullets */}
    <Paragraph bold="true"><Text>8. Custom Indent + Custom Bullet</Text></Paragraph>
    <Paragraph><Text>Diamond bullets with larger spacing</Text></Paragraph>
    <BulletedList bulletChar="n" bulletFont="Wingdings" indentLeft="600" indentIncrement="480">
      <ListItem level="0"><Paragraph><Text>Custom bullet character</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Custom indent spacing</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Full control over appearance</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 9: Practical use case - Compact nested list */}
    <Paragraph bold="true"><Text>9. Practical: Compact Nested List</Text></Paragraph>
    <Paragraph><Text>Good for deeply nested content with limited space</Text></Paragraph>
    <BulletedList indentLeft="300" indentIncrement="200" indentHanging="300">
      <ListItem level="0"><Paragraph><Text>Project Overview</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Phase 1</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Task 1.1</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Subtask 1.1.1</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Subtask 1.1.2</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Task 1.2</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Phase 2</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Task 2.1</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 10: Practical use case - Spacious presentation */}
    <Paragraph bold="true"><Text>10. Practical: Spacious Presentation</Text></Paragraph>
    <Paragraph><Text>Good for presentations or executive summaries</Text></Paragraph>
    <BulletedList bulletChar="\u25CF" bulletFont="Arial" indentLeft="720" indentIncrement="720" indentHanging="480">
      <ListItem level="0"><Paragraph><Text bold="true">Key Finding #1</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Supporting detail</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Additional context</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text bold="true">Key Finding #2</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Evidence</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Implications</Text></Paragraph></ListItem>
    </BulletedList>
  </Section>
</Document>
