const items = ['Apple', 'Banana'];

<Document>
  <BulletedList>
    {items.map(item => (
      <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
    ))}
  </BulletedList>
</Document>
