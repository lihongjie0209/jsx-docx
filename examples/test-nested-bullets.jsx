// Nested bullet list demonstration

const categories = [
  { 
    name: 'Frontend Technologies', 
    items: ['React', 'Vue', 'Angular', 'Svelte'] 
  },
  { 
    name: 'Backend Technologies', 
    items: ['Node.js', 'Java Spring', 'Python Django', 'Go'] 
  },
  { 
    name: 'Databases', 
    items: ['PostgreSQL', 'MongoDB', 'Redis'] 
  }
];

<Document>
  <Section pageSize="A4">
    <Paragraph align="center" bold="true" fontSize="16">
      <Text>Nested Bullet List Examples</Text>
    </Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 1: Basic nested list */}
    <Paragraph bold="true"><Text>1. Basic Nested List</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text>Main Item 1</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-item 1.1</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-item 1.2</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Sub-sub-item 1.2.1</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Sub-sub-item 1.2.2</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Main Item 2</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-item 2.1</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 2: Nested list with custom bullet (Wingdings diamond) */}
    <Paragraph bold="true"><Text>2. Nested List with Diamond Bullets</Text></Paragraph>
    <BulletedList bulletChar="n" bulletFont="Wingdings">
      <ListItem level="0"><Paragraph><Text>Programming Languages</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>JavaScript</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Python</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Java</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Frameworks</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>React</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Spring Boot</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Django</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 3: Nested list with Unicode bullets */}
    <Paragraph bold="true"><Text>3. Nested List with Unicode Bullets</Text></Paragraph>
    <BulletedList bulletChar="\u25CF" bulletFont="Arial">
      <ListItem level="0"><Paragraph><Text>Web Development</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>HTML5</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>CSS3</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Flexbox</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Grid</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>JavaScript ES6+</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Mobile Development</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>React Native</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Flutter</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 4: Dynamic nested list using map() */}
    <Paragraph bold="true"><Text>4. Dynamic Nested List (using map)</Text></Paragraph>
    <BulletedList>
      {categories.map(category => [
        <ListItem level="0" key={category.name}>
          <Paragraph><Text bold="true">{category.name}</Text></Paragraph>
        </ListItem>,
        ...category.items.map(item => (
          <ListItem level="1" key={item}>
            <Paragraph><Text>{item}</Text></Paragraph>
          </ListItem>
        ))
      ])}
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 5: Deep nesting (up to 5 levels) */}
    <Paragraph bold="true"><Text>5. Deep Nesting Example</Text></Paragraph>
    <BulletedList bulletChar="l" bulletFont="Wingdings">
      <ListItem level="0"><Paragraph><Text>Level 0: Company</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Level 1: Department - Engineering</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: Team - Frontend</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: Project - E-commerce Site</Text></Paragraph></ListItem>
      <ListItem level="4"><Paragraph><Text>Level 4: Task - Shopping Cart Feature</Text></Paragraph></ListItem>
      <ListItem level="5"><Paragraph><Text>Level 5: Subtask - Payment Integration</Text></Paragraph></ListItem>
      <ListItem level="4"><Paragraph><Text>Level 4: Task - User Profile</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: Project - Mobile App</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Level 2: Team - Backend</Text></Paragraph></ListItem>
      <ListItem level="3"><Paragraph><Text>Level 3: Project - API Gateway</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* Example 6: Mixed bullet styles in nested structure */}
    <Paragraph bold="true"><Text>6. Mixed Bullet Styles</Text></Paragraph>
    <Paragraph><Text>Default bullets:</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text>Main point with default bullet</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-point with default bullet</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Sub-sub-point with default bullet</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text>Arrow bullets:</Text></Paragraph>
    <BulletedList bulletChar="\u00FC" bulletFont="Wingdings">
      <ListItem level="0"><Paragraph><Text>Main point with arrow</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-point with arrow</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Sub-sub-point with arrow</Text></Paragraph></ListItem>
    </BulletedList>
    
    <Paragraph><Text>Square bullets:</Text></Paragraph>
    <BulletedList bulletChar="\u25A0" bulletFont="Arial">
      <ListItem level="0"><Paragraph><Text>Main point with square</Text></Paragraph></ListItem>
      <ListItem level="1"><Paragraph><Text>Sub-point with square</Text></Paragraph></ListItem>
      <ListItem level="2"><Paragraph><Text>Sub-sub-point with square</Text></Paragraph></ListItem>
    </BulletedList>
  </Section>
</Document>
