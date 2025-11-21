// 示例：自定义项目符号样式
<Document>
  <Section pageSize="A4">
    <Paragraph>项目符号样式示例</Paragraph>
    
    {/* 默认 Wingdings 圆点 */}
    <Paragraph>1. 默认样式（Wingdings 圆点）：</Paragraph>
    <BulletedList>
      <ListItem><Paragraph><Text>第一项</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>第二项</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>第三项</Text></Paragraph></ListItem>
    </BulletedList>
    
    {/* Wingdings 菱形 */}
    <Paragraph>2. Wingdings 菱形：</Paragraph>
    <BulletedList bulletChar="n" bulletFont="Wingdings">
      <ListItem><Paragraph><Text>菱形项 1</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>菱形项 2</Text></Paragraph></ListItem>
    </BulletedList>
    
    {/* Wingdings 三角 */}
    <Paragraph>3. Wingdings 三角：</Paragraph>
    <BulletedList bulletChar="ü" bulletFont="Wingdings">
      <ListItem><Paragraph><Text>三角项 1</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>三角项 2</Text></Paragraph></ListItem>
    </BulletedList>
    
    {/* Unicode 实心圆 */}
    <Paragraph>4. Unicode 实心圆（Arial）：</Paragraph>
    <BulletedList bulletChar="●" bulletFont="Arial">
      <ListItem><Paragraph><Text>实心圆项 1</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>实心圆项 2</Text></Paragraph></ListItem>
    </BulletedList>
    
    {/* Unicode 实心方块 */}
    <Paragraph>5. Unicode 实心方块（Calibri）：</Paragraph>
    <BulletedList bulletChar="■" bulletFont="Calibri">
      <ListItem><Paragraph><Text>方块项 1</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>方块项 2</Text></Paragraph></ListItem>
    </BulletedList>
    
    {/* Unicode 三角箭头 */}
    <Paragraph>6. Unicode 三角箭头（Arial）：</Paragraph>
    <BulletedList bulletChar="►" bulletFont="Arial">
      <ListItem><Paragraph><Text>箭头项 1</Text></Paragraph></ListItem>
      <ListItem><Paragraph><Text>箭头项 2</Text></Paragraph></ListItem>
    </BulletedList>
  </Section>
</Document>
