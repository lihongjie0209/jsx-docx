package cn.lihongjie.jsxdocx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for DocxToJsx reverse converter.
 * Tests each supported component to ensure proper conversion.
 */
public class DocxToJsxTest {

    @TempDir
    Path tempDir;

    private final Compiler compiler = new Compiler();
    private final JsRuntime runtime = new JsRuntime();
    private final Renderer renderer = new Renderer();

    /**
     * Helper method to generate DOCX and convert back to JSX
     */
    private String roundTrip(String jsx) throws Exception {
        Path docxPath = tempDir.resolve("test-" + System.nanoTime() + ".docx");
        String jsCode = compiler.compile(jsx);
        var vdom = runtime.run(jsCode, null);
        renderer.renderToDocx(vdom, docxPath.toString(), tempDir, null);
        return DocxToJsx.convert(docxPath.toString());
    }

    // ==================== Document & Section Tests ====================

    @Test
    void testDocumentBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text>Hello</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Document>"), "Should contain Document");
        assertTrue(result.contains("</Document>"), "Should close Document");
        assertTrue(result.contains("<Section"), "Should contain Section");
        assertTrue(result.contains("Hello"), "Should contain text");
        
        System.out.println("Document basic test passed");
    }

    @Test
    void testSectionPageSizeA4() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph><Text>A4 Page</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("pageSize=\"A4\""), "Should detect A4 page size");
        
        System.out.println("Section A4 page size test passed");
    }

    @Test
    void testSectionPageSizeLetter() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="LETTER">
                <Paragraph><Text>Letter Page</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("pageSize=\"LETTER\""), "Should detect Letter page size");
        
        System.out.println("Section Letter page size test passed");
    }

    @Test
    void testSectionMargins() throws Exception {
        String jsx = """
            <Document>
              <Section margins={{ top: 1, bottom: 1, left: 1.25, right: 1.25 }}>
                <Paragraph><Text>With Margins</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        assertTrue(result.contains("margins={"), "Should have margins object");
        
        System.out.println("Section margins test passed");
    }

    @Test
    void testSectionLandscape() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4" orientation="landscape">
                <Paragraph><Text>Landscape</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("orientation=\"landscape\""), "Should detect landscape orientation");
        
        System.out.println("Section landscape test passed");
    }

    // ==================== Paragraph Tests ====================

    @Test
    void testParagraphBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text>Simple paragraph</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Paragraph>"), "Should contain Paragraph");
        assertTrue(result.contains("Simple paragraph"), "Should contain text");
        
        System.out.println("Paragraph basic test passed");
    }

    @Test
    void testParagraphAlignCenter() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph align="CENTER"><Text>Centered</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("align=\"CENTER\""), "Should have CENTER alignment");
        
        System.out.println("Paragraph align CENTER test passed");
    }

    @Test
    void testParagraphAlignRight() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph align="RIGHT"><Text>Right aligned</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("align=\"RIGHT\""), "Should have RIGHT alignment");
        
        System.out.println("Paragraph align RIGHT test passed");
    }

    @Test
    void testParagraphAlignJustify() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph align="BOTH"><Text>Justified text that should span multiple lines to show justification effect properly.</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("align=\"BOTH\"") || result.contains("align=\"JUSTIFY\""), 
            "Should have BOTH/JUSTIFY alignment");
        
        System.out.println("Paragraph align JUSTIFY test passed");
    }

    @Test
    void testParagraphSpacing() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph before={10} after={10}><Text>With spacing</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("before={"), "Should have before spacing");
        assertTrue(result.contains("after={"), "Should have after spacing");
        
        System.out.println("Paragraph spacing test passed");
    }

    @Test
    void testParagraphIndentation() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph indentLeft={720}><Text>Indented text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("indentLeft={"), "Should have indentLeft");
        
        System.out.println("Paragraph indentation test passed");
    }

    @Test
    void testParagraphFirstLineIndent() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph firstLine={36}><Text>First line indented paragraph with enough text to show the indent effect.</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("firstLine={"), "Should have firstLine");
        
        System.out.println("Paragraph first line indent test passed");
    }

    // ==================== Text Formatting Tests ====================

    @Test
    void testTextBold() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text bold={true}>Bold text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("bold={true}"), "Should have bold={true}");
        assertTrue(result.contains("Bold text"), "Should contain text");
        
        System.out.println("Text bold test passed");
    }

    @Test
    void testTextItalic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text italic={true}>Italic text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("italic={true}"), "Should have italic={true}");
        
        System.out.println("Text italic test passed");
    }

    @Test
    void testTextUnderline() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text underline={true}>Underlined text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("underline={true}"), "Should have underline={true}");
        
        System.out.println("Text underline test passed");
    }

    @Test
    void testTextStrike() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text strike={true}>Strikethrough text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("strike={true}"), "Should have strike={true}");
        
        System.out.println("Text strike test passed");
    }

    @Test
    void testTextFontSize() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text size={24}>Large text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("size={24}"), "Should have size={24}");
        
        System.out.println("Text font size test passed");
    }

    @Test
    void testTextFontFamily() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text font="Arial">Arial text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("font=\"Arial\""), "Should have font=\"Arial\"");
        
        System.out.println("Text font test passed");
    }

    @Test
    void testTextColor() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text color="#FF0000">Red text</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("color=\"#FF0000\""), "Should have color=\"#FF0000\"");
        
        System.out.println("Text color test passed");
    }

    @Test
    void testTextCombinedFormatting() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Text bold={true} italic={true} size={14} color="#0000FF">Combined formatting</Text>
                </Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("bold={true}"), "Should have bold");
        assertTrue(result.contains("italic={true}"), "Should have italic");
        assertTrue(result.contains("size={14}"), "Should have size");
        assertTrue(result.contains("color=\"#0000FF\""), "Should have color");
        
        System.out.println("Text combined formatting test passed");
    }

    @Test
    void testMultipleTextRuns() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Text>Normal </Text>
                  <Text bold={true}>bold </Text>
                  <Text italic={true}>italic </Text>
                  <Text>normal again</Text>
                </Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Normal"), "Should have Normal text");
        assertTrue(result.contains("bold={true}"), "Should have bold text");
        assertTrue(result.contains("italic={true}"), "Should have italic text");
        
        System.out.println("Multiple text runs test passed");
    }

    // ==================== Heading Tests ====================

    @Test
    void testHeadingLevel1() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Heading level={1}>Heading 1</Heading>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Heading 1"), "Should contain heading text");
        
        System.out.println("Heading level 1 test passed");
    }

    @Test
    void testHeadingLevel2() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Heading level={2}>Heading 2</Heading>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Heading 2"), "Should contain heading text");
        
        System.out.println("Heading level 2 test passed");
    }

    // ==================== Table Tests ====================

    @Test
    void testTableBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table>
                  <Row>
                    <Cell><Paragraph><Text>Cell 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Cell 2</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Table"), "Should contain Table");
        assertTrue(result.contains("<Row"), "Should contain Row");
        assertTrue(result.contains("<Cell"), "Should contain Cell");
        assertTrue(result.contains("Cell 1"), "Should contain cell text");
        assertTrue(result.contains("Cell 2"), "Should contain cell text");
        
        System.out.println("Table basic test passed");
    }

    @Test
    void testTableWithWidth() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table width={8000}>
                  <Row>
                    <Cell width={4000}><Paragraph><Text>Cell 1</Text></Paragraph></Cell>
                    <Cell width={4000}><Paragraph><Text>Cell 2</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("width={"), "Should have width property");
        
        System.out.println("Table with width test passed");
    }

    @Test
    void testTableHeaderRow() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table>
                  <Row header={true}>
                    <Cell><Paragraph><Text bold={true}>Header 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text bold={true}>Header 2</Text></Paragraph></Cell>
                  </Row>
                  <Row>
                    <Cell><Paragraph><Text>Data 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Data 2</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("header={true}"), "Should detect header row");
        assertTrue(result.contains("Header 1"), "Should contain header text");
        assertTrue(result.contains("Data 1"), "Should contain data text");
        
        System.out.println("Table header row test passed");
    }

    @Test
    void testTableCellBackgroundColor() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table>
                  <Row>
                    <Cell background="#EEEEEE"><Paragraph><Text>Colored cell</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("background=\"#"), "Should have background");
        
        System.out.println("Table cell background color test passed");
    }

    @Test
    void testTableColspan() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table>
                  <Row>
                    <Cell colspan={2}><Paragraph><Text>Merged cell</Text></Paragraph></Cell>
                  </Row>
                  <Row>
                    <Cell><Paragraph><Text>Cell 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Cell 2</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("colspan={2}"), "Should have colspan={2}");
        
        System.out.println("Table colspan test passed");
    }

    @Test
    void testTableMultipleRows() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Table>
                  <Row>
                    <Cell><Paragraph><Text>R1C1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R1C2</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R1C3</Text></Paragraph></Cell>
                  </Row>
                  <Row>
                    <Cell><Paragraph><Text>R2C1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R2C2</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R2C3</Text></Paragraph></Cell>
                  </Row>
                  <Row>
                    <Cell><Paragraph><Text>R3C1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R3C2</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>R3C3</Text></Paragraph></Cell>
                  </Row>
                </Table>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("R1C1"), "Should contain R1C1");
        assertTrue(result.contains("R2C2"), "Should contain R2C2");
        assertTrue(result.contains("R3C3"), "Should contain R3C3");
        
        int rowCount = result.split("<Row").length - 1;
        assertEquals(3, rowCount, "Should have 3 rows");
        
        System.out.println("Table multiple rows test passed");
    }

    // ==================== List Tests ====================

    @Test
    void testBulletedListBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <BulletedList>
                  <ListItem><Paragraph><Text>Item 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Item 2</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Item 3</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<BulletedList>"), "Should contain BulletedList");
        assertTrue(result.contains("<ListItem>"), "Should contain ListItem");
        assertTrue(result.contains("Item 1"), "Should contain Item 1");
        assertTrue(result.contains("Item 2"), "Should contain Item 2");
        assertTrue(result.contains("Item 3"), "Should contain Item 3");
        
        System.out.println("Bulleted list basic test passed");
    }

    @Test
    void testNumberedListBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <NumberedList>
                  <ListItem><Paragraph><Text>First</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Second</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>Third</Text></Paragraph></ListItem>
                </NumberedList>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<NumberedList>"), "Should contain NumberedList");
        assertTrue(result.contains("<ListItem>"), "Should contain ListItem");
        assertTrue(result.contains("First"), "Should contain First");
        assertTrue(result.contains("Second"), "Should contain Second");
        
        System.out.println("Numbered list basic test passed");
    }

    @Test
    void testNestedList() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <BulletedList>
                  <ListItem level={0}><Paragraph><Text>Parent 1</Text></Paragraph></ListItem>
                  <ListItem level={1}><Paragraph><Text>Child 1.1</Text></Paragraph></ListItem>
                  <ListItem level={1}><Paragraph><Text>Child 1.2</Text></Paragraph></ListItem>
                  <ListItem level={0}><Paragraph><Text>Parent 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Parent 1"), "Should contain Parent 1");
        assertTrue(result.contains("Child 1.1"), "Should contain Child 1.1");
        assertTrue(result.contains("level={1}"), "Should have nested level");
        
        System.out.println("Nested list test passed");
    }

    // ==================== Header & Footer Tests ====================

    @Test
    void testHeaderBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Header>
                  <Paragraph><Text>Header Text</Text></Paragraph>
                </Header>
                <Paragraph><Text>Body</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Header"), "Should contain Header");
        assertTrue(result.contains("Header Text"), "Should contain header text");
        
        System.out.println("Header basic test passed");
    }

    @Test
    void testFooterBasic() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Footer>
                  <Paragraph><Text>Footer Text</Text></Paragraph>
                </Footer>
                <Paragraph><Text>Body</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Footer"), "Should contain Footer");
        assertTrue(result.contains("Footer Text"), "Should contain footer text");
        
        System.out.println("Footer basic test passed");
    }

    // ==================== Special Content Tests ====================

    @Test
    void testLineBreak() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Text>Before</Text>
                  <Br/>
                  <Text>After</Text>
                </Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Before"), "Should contain text before break");
        assertTrue(result.contains("After"), "Should contain text after break");
        
        System.out.println("Line break test passed");
    }

    @Test
    void testTab() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph>
                  <Text>Before</Text>
                  <Tab/>
                  <Text>After</Text>
                </Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Before"), "Should contain text before tab");
        assertTrue(result.contains("After"), "Should contain text after tab");
        
        System.out.println("Tab test passed");
    }

    @Test
    void testEmptyParagraph() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph></Paragraph>
                <Paragraph><Text>Not empty</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("<Paragraph>"), "Should contain Paragraph");
        assertTrue(result.contains("Not empty"), "Should contain text");
        
        System.out.println("Empty paragraph test passed");
    }

    @Test
    void testSpecialCharacters() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text>&lt;Tag&gt; &amp; "Quotes"</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Tag"), "Should contain tag text");
        
        System.out.println("Special characters test passed");
    }

    @Test
    void testChineseText() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text>Hello World</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("Hello"), "Should contain Chinese text");
        
        System.out.println("Chinese text test passed");
    }

    @Test
    void testLongText() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text>This is a very long paragraph that spans multiple lines to test how the converter handles longer content. It should preserve all the text without truncation or modification. The quick brown fox jumps over the lazy dog. Lorem ipsum dolor sit amet, consectetur adipiscing elit.</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        
        assertTrue(result.contains("very long paragraph"), "Should contain long text");
        assertTrue(result.contains("Lorem ipsum"), "Should contain end of long text");
        
        System.out.println("Long text test passed");
    }

    // ==================== Complex Document Test ====================

    @Test
    void testComplexDocument() throws Exception {
        String jsx = """
            <Document>
              <Section pageSize="A4">
                <Paragraph align="CENTER"><Text bold={true}>Document Title</Text></Paragraph>
                <Paragraph>
                  <Text>This is a paragraph with </Text>
                  <Text italic={true}>italic</Text>
                  <Text> and </Text>
                  <Text bold={true}>bold</Text>
                  <Text> text.</Text>
                </Paragraph>
                <Table>
                  <Row header={true}>
                    <Cell width={80000}><Paragraph><Text bold={true}>Header 1</Text></Paragraph></Cell>
                    <Cell width={80000}><Paragraph><Text bold={true}>Header 2</Text></Paragraph></Cell>
                  </Row>
                  <Row>
                    <Cell><Paragraph><Text>Data 1</Text></Paragraph></Cell>
                    <Cell><Paragraph><Text>Data 2</Text></Paragraph></Cell>
                  </Row>
                </Table>
                <BulletedList>
                  <ListItem><Paragraph><Text>List item 1</Text></Paragraph></ListItem>
                  <ListItem><Paragraph><Text>List item 2</Text></Paragraph></ListItem>
                </BulletedList>
              </Section>
            </Document>
            """;
        
        String result = roundTrip(jsx);
        System.out.println("\n=== Generated JSX ===\n" + result);
        
        assertTrue(result.contains("Document Title"), "Should contain title");
        assertTrue(result.contains("<Table"), "Should contain table");
        assertTrue(result.contains("<BulletedList>"), "Should contain list");
        
        System.out.println("Complex document test passed");
    }

    // ==================== Round-trip Consistency Test ====================

    @Test
    void testRoundTripConsistency() throws Exception {
        String jsx = """
            <Document>
              <Section>
                <Paragraph><Text bold={true}>Test</Text></Paragraph>
              </Section>
            </Document>
            """;
        
        Path docxPath1 = tempDir.resolve("trip1.docx");
        Path docxPath2 = tempDir.resolve("trip2.docx");
        
        String jsCode = compiler.compile(jsx);
        var vdom = runtime.run(jsCode, null);
        renderer.renderToDocx(vdom, docxPath1.toString(), tempDir, null);
        
        String jsx2 = DocxToJsx.convert(docxPath1.toString());
        
        String jsCode2 = compiler.compile(jsx2);
        var vdom2 = runtime.run(jsCode2, null);
        renderer.renderToDocx(vdom2, docxPath2.toString(), tempDir, null);
        
        long size1 = Files.size(docxPath1);
        long size2 = Files.size(docxPath2);
        
        System.out.println("  DOCX 1: " + size1 + " bytes");
        System.out.println("  DOCX 2: " + size2 + " bytes");
        
        long sizeDiff = Math.abs(size1 - size2);
        assertTrue(sizeDiff < 100, "Round-trip should produce similar file sizes (diff: " + sizeDiff + ")");
        
        System.out.println("Round trip preservation test passed");
    }
}
