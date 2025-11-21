render(
    <Document>
        <Section pageSize="A4">
            <Heading level="1" align="center">Hello JSX Word</Heading>
            <Paragraph align="left">
                This is a <Text bold color="#FF0000">bold red</Text> text in a paragraph.
            </Paragraph>
            <Table width="100%">
                <Row>
                    <Cell><Paragraph><Text>Cell 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Cell 2</Text></Paragraph></Cell>
                </Row>
                <Row>
                    <Cell><Paragraph><Text>Cell 3</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Cell 4</Text></Paragraph></Cell>
                </Row>
            </Table>
        </Section>
    </Document>
);
