// 九九乘法表（React 风格的自定义组件版本）
// 演示 jsx-docx 完整支持 React 风格的函数组件语法

// 单个乘法单元格组件
const MultiCell = ({ row, col }) => {
  const result = row * col;
  return (
    <Cell vAlign="center" padding={{ top: 5, bottom: 5, left: 5, right: 5 }}>
      <Paragraph align="center">
        <Text bold={true}>{row}×{col}={result}</Text>
      </Paragraph>
    </Cell>
  );
};

// 空单元格组件
const EmptyCell = () => (
  <Cell background="#E0E0E0">
    <Paragraph><Text> </Text></Paragraph>
  </Cell>
);

// 表格行组件
const TableRow = ({ row }) => {
  const cells = [];
  
  // 添加有效的乘法单元格（对角线及以下）
  for (let col = 1; col <= row; col++) {
    cells.push(<MultiCell row={row} col={col} key={col} />);
  }
  
  // 添加空单元格（对角线以上）
  for (let col = row + 1; col <= 9; col++) {
    cells.push(<EmptyCell key={`empty-${col}`} />);
  }
  
  return <Row>{cells}</Row>;
};

// 主文档
<Document>
  <Section pageSize="A4" orientation="portrait">
    <Paragraph align="center">
      <Text bold={true} size={20}>九九乘法表</Text>
    </Paragraph>
    
    <Paragraph align="center">
      <Text size={10} color="#666666">使用 React 风格自定义组件生成</Text>
    </Paragraph>
    
    <Table border={{ size: 1, color: "#000000" }} width="100%" align="center">
      <TableRow row={1} />
      <TableRow row={2} />
      <TableRow row={3} />
      <TableRow row={4} />
      <TableRow row={5} />
      <TableRow row={6} />
      <TableRow row={7} />
      <TableRow row={8} />
      <TableRow row={9} />
    </Table>
    
    <Paragraph>
      <Text size={8} color="#999999">此表格使用自定义组件 &lt;MultiCell /&gt;、&lt;EmptyCell /&gt;、&lt;TableRow /&gt; 生成</Text>
    </Paragraph>
  </Section>
</Document>
