/**
 * Test Comment Component - 批注组件测试
 * 
 * 演示如何使用 Comment 组件为文档添加批注/评论。
 * Comment 组件包裹需要添加批注的文字内容。
 */

<Document>
    <Section pageSize="A4">
        {/* 标题 */}
        <Heading level={1}>批注组件演示</Heading>
        
        {/* 基础批注 */}
        <Heading level={2}>1. 基础批注</Heading>
        <Paragraph>
            <Text>这是一段普通文字，</Text>
            <Comment author="张三" text="这里需要修改措辞">
                <Text>这是被批注的文字</Text>
            </Comment>
            <Text>，后面继续是普通文字。</Text>
        </Paragraph>
        
        {/* 带首字母缩写的批注 */}
        <Heading level={2}>2. 带首字母缩写的批注</Heading>
        <Paragraph>
            <Comment author="John Doe" initials="JD" text="Please review this section for accuracy">
                <Text>This text needs review by the team.</Text>
            </Comment>
        </Paragraph>
        
        {/* 多个批注 */}
        <Heading level={2}>3. 同一段落多个批注</Heading>
        <Paragraph>
            <Comment author="审稿人A" text="第一处批注内容">
                <Text>第一处被批注的文字</Text>
            </Comment>
            <Text> —— 普通分隔文字 —— </Text>
            <Comment author="审稿人B" text="第二处批注内容">
                <Text>第二处被批注的文字</Text>
            </Comment>
        </Paragraph>
        
        {/* 不同段落的批注 */}
        <Heading level={2}>4. 不同段落的批注</Heading>
        <Paragraph>
            <Comment author="编辑" text="开头很吸引人">
                <Text>这是第一段的内容，开头引人入胜。</Text>
            </Comment>
        </Paragraph>
        <Paragraph>
            <Text>这是第二段，没有批注的普通文字。</Text>
        </Paragraph>
        <Paragraph>
            <Comment author="校对员" text="需要补充引用来源">
                <Text>这是第三段，包含需要标注来源的内容。</Text>
            </Comment>
        </Paragraph>
        
        {/* 对格式化文字添加批注 */}
        <Heading level={2}>5. 格式化文字批注</Heading>
        <Paragraph>
            <Comment author="设计师" text="加粗效果很好，但建议颜色改为蓝色">
                <Text bold={true}>这是加粗的被批注文字</Text>
            </Comment>
        </Paragraph>
        <Paragraph>
            <Comment author="排版" text="斜体用于强调很恰当">
                <Text italic={true} color="#0066CC">这是蓝色斜体的被批注文字</Text>
            </Comment>
        </Paragraph>
        
        {/* 实际应用场景 */}
        <Heading level={2}>6. 实际应用场景</Heading>
        
        {/* 学术论文审稿 */}
        <Paragraph>
            <Text bold={true}>学术论文审稿：</Text>
        </Paragraph>
        <Paragraph>
            <Text>根据实验数据分析，</Text>
            <Comment author="Reviewer #1" text="Please provide confidence intervals for this claim">
                <Text>显著性差异达到了 p&lt;0.05</Text>
            </Comment>
            <Text>，支持了我们的假设。</Text>
        </Paragraph>
        
        {/* 代码审查 */}
        <Paragraph>
            <Text bold={true}>代码审查风格：</Text>
        </Paragraph>
        <Paragraph>
            <Comment author="Code Reviewer" text="Consider using a more descriptive variable name like 'userCount' instead">
                <Text>const n = users.length;</Text>
            </Comment>
        </Paragraph>
        
        {/* 法律文档审核 */}
        <Paragraph>
            <Text bold={true}>法律文档审核：</Text>
        </Paragraph>
        <Paragraph>
            <Text>甲方应在合同签订后</Text>
            <Comment author="法务部" text="建议改为15个工作日，与行业惯例一致">
                <Text>10个工作日</Text>
            </Comment>
            <Text>内完成首批货款的支付。</Text>
        </Paragraph>
        
        {/* 总结 */}
        <Heading level={2}>总结</Heading>
        <Paragraph>
            <Text>Comment 组件支持以下功能：</Text>
        </Paragraph>
        <BulletedList>
            <ListItem><Paragraph><Text>设置批注作者（author）</Text></Paragraph></ListItem>
            <ListItem><Paragraph><Text>设置批注内容（text）</Text></Paragraph></ListItem>
            <ListItem><Paragraph><Text>设置作者首字母缩写（initials，可选）</Text></Paragraph></ListItem>
            <ListItem><Paragraph><Text>支持包裹任意内联内容（Text、Link 等）</Text></Paragraph></ListItem>
            <ListItem><Paragraph><Text>支持格式化文字（加粗、斜体、颜色等）</Text></Paragraph></ListItem>
            <ListItem><Paragraph><Text>同一段落可以有多个批注</Text></Paragraph></ListItem>
        </BulletedList>
    </Section>
</Document>
