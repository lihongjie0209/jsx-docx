// 动态项目周报示例 - 展示如何使用 JSX + JavaScript 生成专业的周报文档
// 数据上下文示例（可以通过 --data 参数传入 JSON 文件）

// 如果没有传入 data，使用默认示例数据
const defaultData = {
  "title": "AI 研发组周报",
  "author": "李工",
  "date": "2025-11-20",
  "highlights": ["完成模型量化", "推理延迟降低 30%", "发布 v2.0 Beta"],
  "metrics": [
    { "name": "准确率", "val": "98.5%", "trend": "up" },
    { "name": "QPS", "val": "150", "trend": "up" },
    { "name": "平均耗时", "val": "45ms", "trend": "down" }
  ]
};

// 使用全局 data 对象（如果存在），否则使用默认数据
const reportData = typeof data !== 'undefined' ? data : defaultData;

<Document>
  {/* 1. 定义可复用的样式 */}
  <Styles>
    <Style styleId="Title" type="paragraph" fontSize={48} bold={true} color="#2E74B5" spacingAfter={400} />
    <Style styleId="Heading1" type="paragraph" outlineLevel={0} fontSize={32} bold={true} color="#1F4D78" spacingBefore={300} spacingAfter={200} />
    <Style styleId="TableHeader" type="paragraph" bold={true} />
    <Style styleId="TableBody" type="paragraph" />
  </Styles>

  <Section pageSize="A4">
    {/* 2. 页眉：公司机密标识 */}
    <Header>
      <Paragraph align="right"><Text color="#888888">内部机密文件 • 禁止外传</Text></Paragraph>
    </Header>

    {/* 3. 文档主体 */}
    <Paragraph styleId="Title" align="center"><Text>{reportData.title}</Text></Paragraph>
    <Paragraph align="center">
      <Text>汇报人：{reportData.author} | 日期：{reportData.date}</Text>
    </Paragraph>

    {/* 4. 动态列表：本周高光 */}
    <Paragraph styleId="Heading1"><Text>本周高光</Text></Paragraph>
    <BulletedList bulletChar="l" bulletFont="Wingdings">
      {reportData.highlights.map(item => (
        <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>

    {/* 5. 动态表格：核心指标 */}
    <Paragraph styleId="Heading1"><Text>核心指标监控</Text></Paragraph>
    <Table width="100%" border={{size: 1, color: "#CCCCCC"}}>
      <Row header={true}>
        <Cell background="#E7E6E6"><Paragraph styleId="TableHeader" align="center"><Text>指标名称</Text></Paragraph></Cell>
        <Cell background="#E7E6E6"><Paragraph styleId="TableHeader" align="center"><Text>当前值</Text></Paragraph></Cell>
        <Cell background="#E7E6E6"><Paragraph styleId="TableHeader" align="center"><Text>趋势</Text></Paragraph></Cell>
      </Row>
      {reportData.metrics.map(m => (
        <Row>
          <Cell><Paragraph styleId="TableBody" align="center"><Text>{m.name}</Text></Paragraph></Cell>
          <Cell><Paragraph styleId="TableBody" align="center"><Text>{m.val}</Text></Paragraph></Cell>
          <Cell>
            {/* 根据趋势动态改变颜色 */}
            <Paragraph styleId="TableBody" align="center">
              <Text color={m.trend === 'up' ? "#009900" : "#CC0000"}>
                {m.trend === 'up' ? "↑ 上升" : "↓ 下降"}
              </Text>
            </Paragraph>
          </Cell>
        </Row>
      ))}
    </Table>

    {/* 6. 页脚：带页码 */}
    <Footer>
      <Paragraph align="center"><Text>第 </Text><PageNumber /><Text> 页</Text></Paragraph>
    </Footer>
  </Section>
</Document>
