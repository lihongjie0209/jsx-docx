// 图表组件示例
// Chart Component Examples

// 示例数据
const salesData = [
  { label: "Q1", value: 12000 },
  { label: "Q2", value: 18500 },
  { label: "Q3", value: 22000 },
  { label: "Q4", value: 28000 }
];

const marketShare = [
  { label: "Product A", value: 35 },
  { label: "Product B", value: 28 },
  { label: "Product C", value: 22 },
  { label: "Others", value: 15 }
];

const monthlyTrend = [
  { label: "Jan", value: 45 },
  { label: "Feb", value: 52 },
  { label: "Mar", value: 48 },
  { label: "Apr", value: 61 },
  { label: "May", value: 55 },
  { label: "Jun", value: 67 }
];

// 自定义图表组件
const ChartCard = ({ title, description, children }) => (
  <>
    <Paragraph>
      <Text bold={true} size={14}>{title}</Text>
    </Paragraph>
    <Paragraph>
      <Text size={10} color="#666666">{description}</Text>
    </Paragraph>
    {children}
    <Paragraph><Text> </Text></Paragraph>
  </>
);

<Document>
  <Section pageSize="A4" orientation="portrait">
    {/* 标题 */}
    <Paragraph align="center">
      <Text bold={true} size={24}>图表组件演示</Text>
    </Paragraph>
    <Paragraph align="center">
      <Text size={12} color="#666666">Chart Component Demo - jsx-docx</Text>
    </Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* 1. 简单柱状图 (水平) */}
    <ChartCard 
      title="1. 柱状图 (Bar Chart - Horizontal)"
      description="使用 type='bar' 创建水平柱状图，适合展示分类对比数据"
    >
      <Chart 
        type="bar"
        title="季度销售额"
        width={500}
        height={280}
        data={salesData}
        colors={["#3498DB"]}
      />
    </ChartCard>
    
    {/* 2. 垂直柱状图 */}
    <ChartCard 
      title="2. 柱状图 (Column Chart - Vertical)"
      description="使用 type='column' 创建垂直柱状图"
    >
      <Chart 
        type="column"
        title="季度销售额对比"
        width={500}
        height={280}
        data={salesData}
        colors={["#2ECC71"]}
      />
    </ChartCard>
    
    {/* 3. 饼图 */}
    <ChartCard 
      title="3. 饼图 (Pie Chart)"
      description="使用 type='pie' 创建饼图，适合展示占比数据"
    >
      <Chart 
        type="pie"
        title="市场份额分布"
        width={400}
        height={350}
        data={marketShare}
      />
    </ChartCard>
    
    {/* 4. 折线图 */}
    <ChartCard 
      title="4. 折线图 (Line Chart)"
      description="使用 type='line' 创建折线图，适合展示趋势变化"
    >
      <Chart 
        type="line"
        title="月度销量趋势"
        width={550}
        height={280}
        data={monthlyTrend}
        colors={["#E74C3C"]}
      />
    </ChartCard>
    
    {/* 5. 面积图 */}
    <ChartCard 
      title="5. 面积图 (Area Chart)"
      description="使用 type='area' 创建面积图，强调数据累积效果"
    >
      <Chart 
        type="area"
        title="收入增长趋势"
        width={550}
        height={280}
        data={monthlyTrend}
        colors={["#9B59B6"]}
      />
    </ChartCard>
  </Section>
  
  <Section pageSize="A4" orientation="landscape">
    {/* 6. 多系列柱状图 */}
    <Paragraph>
      <Text bold={true} size={14}>6. 多系列图表 (Multi-Series Charts)</Text>
    </Paragraph>
    <Paragraph>
      <Text size={10} color="#666666">使用 categories + series 属性创建多系列图表</Text>
    </Paragraph>
    
    <Chart 
      type="bar"
      title="年度销售对比"
      width={700}
      height={350}
      categories={["Q1", "Q2", "Q3", "Q4"]}
      series={[
        { name: "2022年", values: [10000, 15000, 18000, 22000] },
        { name: "2023年", values: [12000, 18500, 22000, 28000] },
        { name: "2024年", values: [15000, 21000, 25000, 32000] }
      ]}
      colors={["#3498DB", "#2ECC71", "#E74C3C"]}
      legendPosition="right"
    />
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* 7. 多系列折线图 */}
    <Paragraph>
      <Text bold={true} size={14}>7. 多系列折线图 (Multi-Series Line Chart)</Text>
    </Paragraph>
    
    <Chart 
      type="line"
      title="产品销量趋势对比"
      width={700}
      height={300}
      categories={["Jan", "Feb", "Mar", "Apr", "May", "Jun"]}
      series={[
        { name: "产品 A", values: [30, 40, 45, 50, 55, 60] },
        { name: "产品 B", values: [25, 35, 38, 48, 52, 58] },
        { name: "产品 C", values: [20, 28, 35, 42, 48, 55] }
      ]}
      colors={["#E74C3C", "#3498DB", "#F39C12"]}
      legendPosition="bottom"
    />
  </Section>
  
  <Section pageSize="A4" orientation="portrait">
    {/* 8. 图表配置选项 */}
    <Paragraph>
      <Text bold={true} size={16}>图表配置选项说明</Text>
    </Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    <Table border={{ size: 1, color: "#CCCCCC" }} width="100%">
      <Row>
        <Cell background="#F5F5F5" width={120}><Paragraph><Text bold={true}>属性</Text></Paragraph></Cell>
        <Cell background="#F5F5F5" width={100}><Paragraph><Text bold={true}>类型</Text></Paragraph></Cell>
        <Cell background="#F5F5F5"><Paragraph><Text bold={true}>说明</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>type</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>string</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>图表类型: bar, column, pie, line, area</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>title</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>string</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>图表标题</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>width</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>number</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>宽度（像素），默认 500</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>height</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>number</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>高度（像素），默认 300</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>data</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>array</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>简单数据: [{`{label, value}`}, ...]</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>categories</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>array</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>多系列类别: ["Q1", "Q2", ...]</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>series</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>array</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>多系列数据: [{`{name, values}`}, ...]</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>colors</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>array</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>自定义颜色: ["#FF0000", ...]</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>legend</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>boolean</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>是否显示图例，默认 true</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell><Paragraph><Text>legendPosition</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>string</Text></Paragraph></Cell>
        <Cell><Paragraph><Text>图例位置: bottom, top, left, right</Text></Paragraph></Cell>
      </Row>
    </Table>
    
    <Paragraph><Text> </Text></Paragraph>
    <Paragraph>
      <Text size={9} color="#999999">Generated by jsx-docx Chart Component</Text>
    </Paragraph>
  </Section>
</Document>
