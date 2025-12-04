# jsx-docx Agent Guide

You are generating Word documents using jsx-docx. This guide provides everything you need.

## Quick Start Template

```jsx
<Document>
  <Section>
    <Paragraph>
      <Text>Hello World</Text>
    </Paragraph>
  </Section>
</Document>
```

## Component Hierarchy (MUST follow this structure)

```
Document (root)
├── Styles (optional, must be first child if present)
│   └── Style
└── Section (required, one or more)
    ├── Header/Footer (optional)
    ├── Heading (level 1-9)
    ├── Paragraph
    │   ├── Text (inline)
    │   ├── Link (inline)
    │   ├── Image (inline)
    │   ├── Bookmark (container)
    │   ├── BookmarkRef (inline)
    │   ├── Footnote/Endnote (inline)
    │   └── Comment (container)
    ├── Table
    │   └── Row
    │       └── Cell
    ├── BulletedList / NumberedList
    │   └── ListItem
    │       └── Paragraph
    ├── Toc (auto-generated table of contents)
    ├── Chart
    └── Watermark
```

## Core Components Reference

### Document & Section
```jsx
<Document>
  <Section pageSize="A4" orientation="portrait" marginTop={1440} marginBottom={1440} marginLeft={1800} marginRight={1800}>
    {/* content */}
  </Section>
</Document>
```
- pageSize: "A4" | "LETTER" | "LEGAL" | custom {width, height} in twips
- orientation: "portrait" | "landscape"
- margins: in twips (1440 twips = 1 inch)

### Text Formatting
```jsx
<Paragraph align="LEFT|CENTER|RIGHT|JUSTIFY" lineSpacing={1.5} spaceBefore={120} spaceAfter={120}>
  <Text bold={true} italic={true} underline={true} fontSize={12} fontFamily="宋体" color="#FF0000">
    Formatted text
  </Text>
</Paragraph>
```

### Headings (auto-included in TOC)
```jsx
<Heading level={1}>Chapter Title</Heading>
<Heading level={2}>Section Title</Heading>
```

### Tables
```jsx
<Table width={5000} align="CENTER">
  <Row header={true}>
    <Cell width={2500} backgroundColor="#EEEEEE"><Text bold={true}>Header</Text></Cell>
    <Cell width={2500}><Text>Header 2</Text></Cell>
  </Row>
  <Row>
    <Cell><Text>Data 1</Text></Cell>
    <Cell><Text>Data 2</Text></Cell>
  </Row>
</Table>

{/* Table with colspan - IMPORTANT: all rows must have same total column count */}
<Table width={9000}>
  <Row>
    <Cell width={2000}><Text>Label</Text></Cell>
    <Cell width={3000}><Text>Value 1</Text></Cell>
    <Cell width={2000}><Text>Label 2</Text></Cell>
    <Cell width={2000}><Text>Value 2</Text></Cell>
  </Row>
  <Row>
    <Cell width={2000}><Text>Merged Label</Text></Cell>
    <Cell width={7000} colspan={3}><Text>This cell spans 3 columns</Text></Cell>
  </Row>
</Table>
```
**⚠️ Table Rules:**
- Every row MUST have the same number of columns (counting colspan)
- If row 1 has 4 cells, all rows need 4 cells (or use colspan to merge)
- Cell widths in same column should be consistent

### Lists
```jsx
{/* Bulleted List */}
<BulletedList bulletChar="•">
  <ListItem><Paragraph><Text>Item 1</Text></Paragraph></ListItem>
  <ListItem><Paragraph><Text>Item 2</Text></Paragraph></ListItem>
</BulletedList>

{/* Numbered List */}
<NumberedList format="decimal|upperLetter|lowerLetter|upperRoman|lowerRoman">
  <ListItem><Paragraph><Text>First</Text></Paragraph></ListItem>
  <ListItem><Paragraph><Text>Second</Text></Paragraph></ListItem>
</NumberedList>

{/* Nested Lists - use level prop */}
<BulletedList>
  <ListItem level={0}><Paragraph><Text>Parent</Text></Paragraph></ListItem>
  <ListItem level={1}><Paragraph><Text>Child</Text></Paragraph></ListItem>
  <ListItem level={2}><Paragraph><Text>Grandchild</Text></Paragraph></ListItem>
</BulletedList>
```

### Images
```jsx
<Image src="path/to/image.png" width={200} height={150} />
<Image src="data:image/png;base64,..." width={200} />
<Image src="https://example.com/image.png" width={200} />
```

### Links
```jsx
<Link href="https://example.com">Click here</Link>
```

### Table of Contents
```jsx
<Toc title="目录" maxLevel={3} hyperlink={true} showPageNumbers={true} />
```

### Headers & Footers
```jsx
<Section>
  <Header type="DEFAULT|FIRST|EVEN">
    <Paragraph align="CENTER"><Text>Header Text</Text></Paragraph>
  </Header>
  <Footer type="DEFAULT">
    <Paragraph align="CENTER"><Text>Page </Text><PageNumber /></Paragraph>
  </Footer>
  {/* content */}
</Section>
```

### Bookmarks & Cross-References
```jsx
{/* Create bookmark */}
<Bookmark name="chapter1">
  <Text>Chapter 1: Introduction</Text>
</Bookmark>

{/* Reference bookmark */}
<Text>See page </Text>
<BookmarkRef name="chapter1" type="pageref" />
{/* type: "pageref" (page number), "ref" (content), "text" (custom) */}
```

### Footnotes & Endnotes
```jsx
<Paragraph>
  <Text>Main text</Text>
  <Footnote text="This appears at page bottom" />
  <Endnote text="This appears at document end" />
</Paragraph>
```

### Comments
```jsx
<Comment author="Reviewer" text="Please revise this section">
  <Text>Text with comment</Text>
</Comment>
```

### Charts
```jsx
<Chart 
  type="bar|column|line|area|pie"
  title="Sales Data"
  width={500} height={300}
  categories={["Q1", "Q2", "Q3", "Q4"]}
  series={[
    { name: "2023", values: [100, 200, 150, 300] },
    { name: "2024", values: [150, 250, 200, 350] }
  ]}
/>
```

### Watermark
```jsx
<Watermark text="CONFIDENTIAL" color="#CCCCCC" fontSize={72} rotation={-45} />
```

### Styles (Reusable Formatting)
```jsx
<Document>
  <Styles>
    <Style styleId="Title" type="paragraph" fontSize={24} bold={true} align="CENTER" />
    <Style styleId="Emphasis" type="character" italic={true} color="#0066CC" />
  </Styles>
  <Section>
    <Paragraph styleId="Title"><Text>Document Title</Text></Paragraph>
    <Paragraph><Text styleId="Emphasis">Emphasized text</Text></Paragraph>
  </Section>
</Document>
```

## Properties Quick Reference

### Section
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| pageSize | string/object | "A4" | "A4", "LETTER", "LEGAL" or {width, height} in twips |
| orientation | string | "portrait" | "portrait" or "landscape" |
| marginTop | number | 1440 | Top margin in twips |
| marginBottom | number | 1440 | Bottom margin in twips |
| marginLeft | number | 1800 | Left margin in twips |
| marginRight | number | 1800 | Right margin in twips |

### Paragraph
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| align | string | "LEFT" | "LEFT", "CENTER", "RIGHT", "JUSTIFY" |
| lineSpacing | number | 1.0 | Line spacing multiplier (1.5 = 150%) |
| spaceBefore | number | 0 | Space before paragraph in twips |
| spaceAfter | number | 0 | Space after paragraph in twips |
| indentLeft | number | 0 | Left indent in twips |
| indentRight | number | 0 | Right indent in twips |
| indentFirstLine | number | 0 | First line indent in twips |
| styleId | string | - | Reference to defined style |

### Text
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| bold | boolean | false | Bold text |
| italic | boolean | false | Italic text |
| underline | boolean | false | Underlined text |
| strike | boolean | false | Strikethrough text |
| fontSize | number | 11 | Font size in points |
| fontFamily | string | "Calibri" | Font family name |
| color | string | "#000000" | Text color in hex |
| highlight | string | - | Highlight color ("yellow", "green", etc.) |
| superscript | boolean | false | Superscript text |
| subscript | boolean | false | Subscript text |
| styleId | string | - | Reference to character style |

### Heading
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| level | number | 1 | Heading level 1-9 |

### Table
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| width | number | auto | Table width in twips |
| align | string | "LEFT" | "LEFT", "CENTER", "RIGHT" |
| borders | boolean | true | Show table borders |
| styleId | string | - | Reference to table style |

### Row
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| header | boolean | false | Mark as header row (repeats on page break) |

### Cell
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| width | number | auto | Cell width in twips |
| backgroundColor | string | - | Background color in hex |
| verticalAlign | string | "top" | "top", "center", "bottom" |
| colspan | number | 1 | Column span |
| rowspan | number | 1 | Row span |
| borders | object | - | {top, bottom, left, right} border config |

### BulletedList
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| bulletChar | string | "•" | Bullet character (Unicode or Wingdings) |
| bulletFont | string | - | Font for bullet ("Wingdings", "Symbol") |
| indentLeft | number | 420 | Base indent in twips |
| indentIncrement | number | 420 | Indent increase per level |
| indentHanging | number | 360 | Hanging indent in twips |

### NumberedList
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| format | string | "decimal" | "decimal", "upperLetter", "lowerLetter", "upperRoman", "lowerRoman" |
| start | number | 1 | Starting number |

### ListItem
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| level | number | 0 | Nesting level (0-8) |

### Image
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| src | string | **required** | File path, URL, or base64 data URI |
| width | number | auto | Width in points |
| height | number | auto | Height in points |
| alt | string | - | Alternative text |

### Link
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| href | string | **required** | URL or bookmark reference |

### Toc
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| title | string | "目录" | TOC title |
| maxLevel | number | 3 | Maximum heading level to include (1-9) |
| hyperlink | boolean | true | Make entries clickable |
| showPageNumbers | boolean | true | Show page numbers |

### Header / Footer
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| type | string | "DEFAULT" | "DEFAULT", "FIRST", "EVEN" |

### Bookmark
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| name | string | **required** | Unique bookmark identifier |

### BookmarkRef
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| name | string | **required** | Bookmark name to reference |
| type | string | "pageref" | "pageref" (page#), "ref" (content), "text" (custom) |
| text | string | - | Display text when type="text" |
| hyperlink | boolean | true | Make reference clickable |

### Footnote / Endnote
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| text | string | **required** | Note content |

### Comment
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| author | string | "" | Comment author name |
| text | string | **required** | Comment text |

### Chart
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| type | string | "bar" | "bar", "column", "line", "area", "pie" |
| title | string | - | Chart title |
| width | number | 400 | Width in points |
| height | number | 300 | Height in points |
| categories | array | **required** | X-axis labels ["Q1", "Q2", ...] |
| series | array | **required** | [{name, values, color?}, ...] |

### Watermark
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| text | string | **required** | Watermark text |
| color | string | "#C0C0C0" | Text color in hex |
| fontSize | number | 72 | Font size in points |
| rotation | number | -45 | Rotation angle in degrees |
| fontFamily | string | "Calibri" | Font family |

### Style
| Property | Type | Default | Description |
|----------|------|---------|-------------|
| styleId | string | **required** | Unique style identifier |
| type | string | "paragraph" | "paragraph", "character", "table" |
| basedOn | string | - | Parent style ID to inherit from |
| *(plus all Text/Paragraph properties)* | | | |

## JavaScript in JSX

You can use JavaScript expressions and logic:

```jsx
const items = ["Apple", "Banana", "Orange"];
const showDetails = true;

<Document>
  <Section>
    {/* Conditional rendering */}
    {showDetails && <Paragraph><Text>Details shown</Text></Paragraph>}
    
    {/* List mapping */}
    <BulletedList>
      {items.map(item => (
        <ListItem>
          <Paragraph><Text>{item}</Text></Paragraph>
        </ListItem>
      ))}
    </BulletedList>
    
    {/* Template literals */}
    <Paragraph>
      <Text>Total items: {items.length}</Text>
    </Paragraph>
  </Section>
</Document>
```

## Data Context

When `data` parameter is provided, access it as a global:

```jsx
// If data = { title: "Report", items: [...] }
<Document>
  <Section>
    <Heading level={1}>{data.title}</Heading>
    <BulletedList>
      {data.items.map(item => (
        <ListItem><Paragraph><Text>{item.name}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>
  </Section>
</Document>
```

## Units Reference

| Unit | Value | Usage |
|------|-------|-------|
| Twips | 1/20 point | Margins, spacing, indentation |
| 1 inch | 1440 twips | `marginLeft={1440}` |
| 1 cm | ~567 twips | `marginTop={567}` |
| Points | font size | `fontSize={12}` (12pt) |
| EMU | 914400/inch | Image dimensions |

## Common Patterns

### Report Template
```jsx
<Document>
  <Section>
    <Heading level={1}>Report Title</Heading>
    <Paragraph><Text>Date: 2024-01-01</Text></Paragraph>
    
    <Heading level={2}>Summary</Heading>
    <Paragraph><Text>Overview text...</Text></Paragraph>
    
    <Heading level={2}>Data</Heading>
    <Table>
      <Row header={true}>
        <Cell><Text bold={true}>Column 1</Text></Cell>
        <Cell><Text bold={true}>Column 2</Text></Cell>
      </Row>
      <Row>
        <Cell><Text>Value 1</Text></Cell>
        <Cell><Text>Value 2</Text></Cell>
      </Row>
    </Table>
  </Section>
</Document>
```

### Invoice Template
```jsx
<Document>
  <Section marginTop={720} marginBottom={720}>
    <Paragraph align="RIGHT">
      <Text bold={true} fontSize={24}>INVOICE</Text>
    </Paragraph>
    <Paragraph align="RIGHT">
      <Text>Invoice #: INV-001</Text>
    </Paragraph>
    
    <Table width={9000}>
      <Row header={true}>
        <Cell backgroundColor="#333333"><Text bold={true} color="#FFFFFF">Item</Text></Cell>
        <Cell backgroundColor="#333333"><Text bold={true} color="#FFFFFF">Qty</Text></Cell>
        <Cell backgroundColor="#333333"><Text bold={true} color="#FFFFFF">Price</Text></Cell>
        <Cell backgroundColor="#333333"><Text bold={true} color="#FFFFFF">Total</Text></Cell>
      </Row>
      <Row>
        <Cell><Text>Product A</Text></Cell>
        <Cell><Text>2</Text></Cell>
        <Cell><Text>$50.00</Text></Cell>
        <Cell><Text>$100.00</Text></Cell>
      </Row>
    </Table>
  </Section>
</Document>
```

## Important Rules

1. **Root must be `<Document>`** - Always start with `<Document>` as the root element
2. **Section required** - Content must be inside `<Section>` elements
3. **Text in containers** - Text content should be wrapped in `<Text>` inside `<Paragraph>`
4. **Lists need structure** - `<ListItem>` must contain `<Paragraph>` with `<Text>`
5. **Valid JSX syntax** - Use `{/* comment */}` for comments, `{expression}` for JS
6. **Self-closing tags** - Empty elements use `<Image />` syntax
7. **Boolean props** - Use `bold={true}` not `bold="true"`
8. **Color format** - Use hex strings: `color="#FF0000"`
9. **Table column consistency** - Every row must have same column count (use colspan to merge)

## Error Prevention

❌ Wrong:
```jsx
<Paragraph>Plain text</Paragraph>
<BulletedList><ListItem>Item</ListItem></BulletedList>
{/* Table with inconsistent columns */}
<Table>
  <Row><Cell>A</Cell><Cell>B</Cell></Row>
  <Row><Cell>C</Cell><Cell>D</Cell><Cell>E</Cell><Cell>F</Cell></Row>
</Table>
```

✅ Correct:
```jsx
<Paragraph><Text>Plain text</Text></Paragraph>
<BulletedList><ListItem><Paragraph><Text>Item</Text></Paragraph></ListItem></BulletedList>
{/* Table with consistent 4 columns */}
<Table>
  <Row><Cell colspan={2}>A+B merged</Cell><Cell>C</Cell><Cell>D</Cell></Row>
  <Row><Cell>A</Cell><Cell>B</Cell><Cell>C</Cell><Cell>D</Cell></Row>
</Table>
```
