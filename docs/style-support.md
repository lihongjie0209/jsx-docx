# Style Support in jsx-docx

## Overview

All major components in jsx-docx now support the `styleId` prop to reference user-defined styles from the `<Styles>` component.

## Supported Components

### 1. **Paragraph** (`<Paragraph>`)
Supports paragraph-level styles (alignment, spacing, indentation, etc.)

```jsx
<Styles>
  <Style 
    styleId="Quote" 
    name="Quote"
    type="paragraph"
    italic={true}
    color="#666666"
  />
</Styles>

<Paragraph styleId="Quote">
  <Text>"This is a quote"</Text>
</Paragraph>
```

### 2. **Text** (`<Text>`)
Supports character-level styles (font, size, color, bold, italic, etc.)

```jsx
<Styles>
  <Style 
    styleId="Highlight" 
    name="Highlight"
    type="character"
    bold={true}
    color="#FF6600"
  />
</Styles>

<Paragraph>
  <Text>Regular text with </Text>
  <Text styleId="Highlight">highlighted text</Text>
</Paragraph>
```

### 3. **Heading** (`<Heading>`)
Supports heading styles (outline levels, formatting)

```jsx
<Styles>
  <Style 
    styleId="H1" 
    name="My Heading 1"
    type="paragraph"
    outlineLevel={0}
    bold={true}
    fontSize={44}
  />
</Styles>

<Heading styleId="H1">
  <Text>Chapter Title</Text>
</Heading>
```

### 4. **Table** (`<Table>`)
Supports table-level styles

```jsx
<Styles>
  <Style 
    styleId="DataTable" 
    name="Data Table"
    type="table"
  />
</Styles>

<Table styleId="DataTable">
  <Row>...</Row>
</Table>
```

### 5. **Cell** (`<Cell>`)
Applies paragraph style to the cell's content

```jsx
<Cell styleId="HeaderStyle">
  <Paragraph>...</Paragraph>
</Cell>
```

## Combining Styles with Direct Props

You can combine style references with direct props. Direct props will override style definitions:

```jsx
<Paragraph styleId="Quote" align="CENTER">
  <Text>Centered quote</Text>
</Paragraph>

<Text styleId="Highlight" underline={true}>
  Highlighted and underlined
</Text>
```

## Style Types

- **paragraph**: Applied to `<Paragraph>`, `<Heading>`, and `<Cell>` (for cell content)
- **character**: Applied to `<Text>` (inline text formatting)
- **table**: Applied to `<Table>` (table-level formatting)

## Benefits

1. **Consistency**: Define styles once, reuse everywhere
2. **Maintainability**: Update styles in one place
3. **Separation of Concerns**: Keep content and formatting separate
4. **Professional**: Create documents with consistent branding
5. **Flexibility**: Combine styles with direct props when needed

## Examples

See the following example files:
- `examples/test-custom-styles.jsx` - Complete style showcase
- `examples/test-simple-styles.jsx` - Basic style usage
- `examples/test-all-styles.jsx` - All component types with styles
