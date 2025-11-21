// 示例：使用 JS 逻辑生成内容
const items = ['Apple', 'Banana', 'Orange'];

<Document>
  <Section pageSize="A4">
    <Paragraph>水果列表：</Paragraph>
    <BulletedList>
      {items.map(item => (
        <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>
  </Section>
</Document>