// 演示: Nested Text Components 问题和解决方案
// 这个示例展示了错误的做法、为什么会出错、以及正确的做法

<Document>
  <Section pageSize="A4">
    
    {/* ===== 错误示例 1: Text 中嵌套 Text ===== */}
    <Paragraph bold="true" fontSize="12"><Text>❌ 错误示例 1: Text 中嵌套 Text</Text></Paragraph>
    <Paragraph><Text>以下内容应该显示但会消失：</Text></Paragraph>
    
    <Paragraph style={{backgroundColor: "#FFE6E6"}}>
      <Text>
        <Text>Nested 1</Text>
        <Text>Nested 2</Text>
      </Text>
    </Paragraph>
    <Paragraph><Text>结果: 上面的段落为空！</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 错误示例 2: Text 中嵌套 Paragraph ===== */}
    <Paragraph bold="true" fontSize="12"><Text>❌ 错误示例 2: Text 中嵌套块级元素 (Paragraph)</Text></Paragraph>
    <Paragraph><Text>这在动态生成表格时常见：</Text></Paragraph>
    
    {(() => {
      const cells = [
        <Paragraph><Text>Cell 1</Text></Paragraph>,
        <Paragraph><Text>Cell 2</Text></Paragraph>,
      ];
      return (
        <Paragraph style={{backgroundColor: "#FFE6E6"}}>
          <Text>
            {/* cells 中的 Paragraph 会被过滤掉 */}
            {cells}
          </Text>
        </Paragraph>
      );
    })()}
    
    <Paragraph><Text>结果: 所有单元格内容消失！</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 错误原因说明 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>🔍 为什么会出现这个问题?</Text></Paragraph>
    <Paragraph><Text>Renderer.java 的处理代码（第 240-248 行）:</Text></Paragraph>
    
    <Paragraph><Text monospace="true" fontSize="9">
{`case "text":
    if (parent instanceof XWPFParagraph) {
        XWPFRun run = createRun();
        for (Object c : children) {
            if (c instanceof String) {  // ← 只接受字符串！
                run.setText((String) c);
            }
            // VNode 被忽略了
        }
    }`}
    </Text></Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 正确示例 1: 扁平化文本 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>✅ 正确示例 1: 扁平化多个 Text 组件</Text></Paragraph>
    <Paragraph><Text>直接将 Text 放在 Paragraph 中（并列关系）:</Text></Paragraph>
    
    <Paragraph style={{backgroundColor: "#E6FFE6"}}>
      <Text>Hello</Text>
      <Text> </Text>
      <Text>World</Text>
    </Paragraph>
    <Paragraph><Text>结果: 显示 "Hello World"</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 正确示例 2: 字符串拼接 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>✅ 正确示例 2: 在单个 Text 中进行字符串拼接</Text></Paragraph>
    
    <Paragraph style={{backgroundColor: "#E6FFE6"}}>
      <Text>{"Hello" + " " + "World"}</Text>
    </Paragraph>
    <Paragraph><Text>结果: 显示 "Hello World"</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 正确示例 3: 使用表格容纳多行数据 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>✅ 正确示例 3: 用表格容纳块级内容 (Cell 可以包含 Paragraph)</Text></Paragraph>
    <Paragraph><Text>这是处理表格数据的正确方式：</Text></Paragraph>
    
    <Table width="100%">
      <Row header="true">
        <Cell background="#4472C4" width="33%"><Paragraph align="center"><Text style={{color: "white"}}>列 1</Text></Paragraph></Cell>
        <Cell background="#4472C4" width="33%"><Paragraph align="center"><Text style={{color: "white"}}>列 2</Text></Paragraph></Cell>
        <Cell background="#4472C4" width="33%"><Paragraph align="center"><Text style={{color: "white"}}>列 3</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell background="#F2F2F2"><Paragraph><Text>内容 1.1</Text></Paragraph></Cell>
        <Cell background="#FFFFFF"><Paragraph><Text>内容 1.2</Text></Paragraph></Cell>
        <Cell background="#F2F2F2"><Paragraph><Text>内容 1.3</Text></Paragraph></Cell>
      </Row>
      <Row>
        <Cell background="#FFFFFF"><Paragraph><Text>内容 2.1</Text></Paragraph></Cell>
        <Cell background="#F2F2F2"><Paragraph><Text>内容 2.2</Text></Paragraph></Cell>
        <Cell background="#FFFFFF"><Paragraph><Text>内容 2.3</Text></Paragraph></Cell>
      </Row>
    </Table>
    
    <Paragraph><Text>结果: 所有单元格内容正确显示！</Text></Paragraph>
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 数据类型层级结构 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>📊 Word 文档的正确结构层级</Text></Paragraph>
    
    <Paragraph><Text monospace="true" fontSize="9">{`Document
  └─ Section
      ├─ Paragraph
      │   ├─ Text (→ Run in Word)
      │   ├─ Text (→ Run in Word)
      │   └─ Br, Tab
      └─ Table
          └─ Row
              └─ Cell
                  └─ Paragraph ← Cell 只能包含块级元素！
                      └─ Text
`}</Text></Paragraph>
    
    <Paragraph><Text> </Text></Paragraph>
    
    {/* ===== 关键规则 ===== */}
    <Paragraph bold="true" fontSize="12"><Text>🎯 关键规则</Text></Paragraph>
    <BulletedList>
      <ListItem level="0"><Paragraph><Text bold="true">Text 只能包含字符串</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>不能在 Text 中嵌套 VNode（包括其他 Text）</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Paragraph 可以包含多个 Text（扁平化）</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Table 的 Cell 不能直接包含文本，必须用 Paragraph 包装</Text></Paragraph></ListItem>
      <ListItem level="0"><Paragraph><Text>Paragraph 可以在 Cell、Document、Section 等中</Text></Paragraph></ListItem>
    </BulletedList>
    
  </Section>
</Document>
