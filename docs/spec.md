# JSX-Docx 组件规范

本文档定义了系统中可用的 JSX 组件、属性、子节点约束以及渲染到 DOCX 的行为。规范优先，代码实现需与本规范一致；未实现项会在"实现状态"中标注。

## 全局 Data 对象

JSX 代码可以访问全局 `data` 对象，该对象包含运行时传递的参数。数据可以通过两种方式提供：

### 程序调用
```java
import cn.lihongjie.jsxdocx.JsRuntime;
import java.util.HashMap;
import java.util.Map;

Map<String, Object> data = new HashMap<>();
data.put("title", "My Document");
data.put("author", "John Doe");
new JsRuntime().run(compiledJs, data);
```

### CLI 使用
```bash
java -jar jsx-docx.jar template.jsx --data context.json -o output.docx
```

### JSX 中使用
```jsx
<Document>
  <Section>
    <Paragraph><Text>{data.title}</Text></Paragraph>
    <Paragraph><Text>作者: {data.author}</Text></Paragraph>
    
    {/* 支持数组迭代 */}
    <BulletedList>
      {data.items.map(item => (
        <ListItem><Paragraph><Text>{item}</Text></Paragraph></ListItem>
      ))}
    </BulletedList>
    
    {/* 支持嵌套对象 */}
    <Paragraph><Text>{data.user.name} ({data.user.email})</Text></Paragraph>
  </Section>
</Document>
```

支持数据类型：`string`、`number`、`boolean`、`object`、`array`。

---

## 公共约定
- 文本节点：字符串会渲染为段落或文字的文本内容。
- 颜色：统一使用 `#RRGGBB`，渲染时自动去掉 `#`。
- 样式引用：多数组件支持 `styleId` 属性引用在 `<Styles>` 中定义的样式。
- 单位：
  - **twips**：1/20 磅，Word 内部单位。`1440 twips = 1 英寸`，`20 twips = 1 磅`。
  - **pt（磅）**：段落属性、单元格边距等使用磅，内部会自动转换为 twips（`pt × 20`）。
  - **英寸**：页边距使用英寸，内部会转换为 twips（`inches × 1440`）。


## `<Document>`


- 作用：根节点，表示完整的 Word 文档。
- 属性：无。
- 子节点：`<Styles>`（可选，唯一）、`<Section>`、`<Paragraph>`、`<Heading>`、`<Table>`、`<PageBreak>`、`<Image>`、`<Link>`、`<BulletedList>`、`<NumberedList>`、`<Header>`、`<Footer>`、纯文本。
- 行为：创建 XWPFDocument；`<Styles>` 组件会生成文档样式定义；非 `<Section>` 的子节点在默认节中渲染。系统自动在 settings.xml 中启用 `<w:updateFields/>`，使文档在 Word 中打开时自动更新所有域（如 TOC、PAGE）。
- 实现状态：已实现。

## `<Section>`


- 作用：定义文档节，用于设置页面布局属性。
- 子节点：`<Paragraph>`、`<Heading>`、`<Table>`、`<PageBreak>`、`<Image>`、`<Link>`、`<BulletedList>`、`<NumberedList>`、纯文本（所有块级组件）。
- 属性：
  - `pageSize`: 字符串，页面尺寸预设值（可选）：
    - `"A4"`: 210mm × 297mm（默认值，11900 × 16840 twips）
    - `"LETTER"`: 8.5" × 11"（12240 × 15840 twips）
  - `orientation`: 字符串，页面方向（可选）：
    - `"portrait"`: 纵向（默认）
    - `"landscape"`: 横向
  - `margins`: 对象，页边距（可选），**单位为英寸**，字段：
    - `top`: 数字，上边距（英寸）
    - `bottom`: 数字，下边距（英寸）
    - `left`: 数字，左边距（英寸）
    - `right`: 数字，右边距（英寸）
    - 内部会自动转换为 twips（`inches × 1440`）
- 行为：设置文档节属性（`CTSectPr`），包括页面尺寸、方向和边距。不同于其他容器组件，`<Section>` 本身不创建新的 POI 对象，而是应用属性后渲染子节点到同一文档级别。
  - **重要**：当同时设置 `pageSize` 和 `orientation="landscape"` 时，页面的宽度和高度会自动交换。例如，A4 纵向是 11900×16840 twips，A4 横向会自动变为 16840×11900 twips。
- 示例：
```jsx
<Section 
  pageSize="A4" 
  orientation="portrait" 
  margins={{ top: 1, bottom: 1, left: 1.25, right: 1.25 }}
>
  <Paragraph><Text>内容</Text></Paragraph>
</Section>
```
- 注意事项：
  - **不支持** `pageMarginTop`/`pageMarginBottom`/`pageMarginLeft`/`pageMarginRight` 单独属性，必须使用 `margins` 对象。
  - 页边距单位为英寸，不是 twips 或磅。例如：1 英寸 = 1440 twips。
  - 页面方向改变时，无需手动交换宽高值，Word 会自动处理。
- 实现状态：已实现。


## `<Styles>`


- 作用：定义文档样式（必须在 `<Document>` 的第一个子节点位置）。
- 子节点：`<Style>`（一个或多个样式定义）。
- 属性：无。
- 行为：生成 Word 样式定义（styles.xml），包含 docDefaults 和所有 `<Style>` 子节点定义的样式。
- 实现状态：已实现。

## `<Style>`


- 作用：单个样式定义（仅在 `<Styles>` 内使用）。
- 子节点：无。
- 属性：
  - `styleId`: 字符串，样式唯一标识符（必需），其他组件通过此 ID 引用样式。
  - `name`: 字符串，样式显示名称（默认同 `styleId`）。
  - `type`: `"paragraph" | "character" | "table"`，样式类型（默认 `paragraph`）。
  - `basedOn`: 字符串，基于的样式 ID（可选）。
  - **段落属性**（仅 `type="paragraph"` 时生效）：
    - `outlineLevel`: 整数 0..8，大纲级别（用于标题）。
    - `keepNext`: 布尔，与下一段保持在同一页。
    - `keepLines`: 布尔，段落不分页。
    - `spacingBefore`: 整数，段前间距（twips）。
    - `spacingAfter`: 整数，段后间距（twips）。
    - `lineSpacing`: 整数，行间距（twips）。
  - **文字属性**（对 `paragraph` 和 `character` 类型生效）：
    - `bold`: 布尔，粗体。
    - `italic`: 布尔，斜体。
    - `fontSize`: 整数，字号（半点，如 44 = 22pt）。
    - `color`: 字符串，文字颜色 `#RRGGBB`。
    - `fontFamily`: 字符串，字体名称（如 `"Arial"`）。
- 行为：在 styles.xml 中创建样式定义，设置指定的格式属性。
- 示例：
```jsx
<Styles>
  <Style 
    styleId="Heading1" 
    name="My Heading 1"
    type="paragraph"
    outlineLevel={0}
    bold={true}
    fontSize={44}
    color="#0066CC"
  />
  <Style 
    styleId="Highlight" 
    name="Highlight Text"
    type="character"
    bold={true}
    color="#FF6600"
  />
</Styles>
```
- 实现状态：已实现。

## `<Paragraph>`


- 作用：段落容器。
- 子节点：`<Text>`、`<Link>`、`<Image>`、`<Br>`、`<Tab>`、`<PageNumber>`、纯文本。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的段落样式 ID（可选）。
  - `align`: `"left" | "center" | "right" | "both"`（段落对齐方式）。
  - `before`: 数字，段前间距（磅 pt）。
  - `after`: 数字，段后间距（磅 pt）。
  - `line`: 数字，行间距（倍数，如 `1.5` 表示 1.5 倍行距）。
  - `indentLeft`: 数字，左缩进（磅 pt）。
  - `indentRight`: 数字，右缩进（磅 pt）。
  - `firstLine`: 数字，首行缩进（磅 pt）。
  - `keepWithNext`: 布尔，与下一段保持在同一页。
  - `keepLines`: 布尔，段落不分页。
  - `background`: 字符串，段落背景色 `#RRGGBB`。
  - `border`: 布尔或对象。布尔 `false` 关闭四边边框；对象 `{ size: 1, color: "#RRGGBB", sides: ["top","right","bottom","left"] }`，`size` 为磅（pt），`sides` 省略表示四边。
  - `tabStops`: 数组，段落制表位设置。元素为 `{ pos, align, leader }`：
    - `pos`: 数字，制表位位置（磅 pt）。
    - `align`: `"left" | "center" | "right" | "decimal" | "bar"`。
    - `leader`: `"none" | "dots" | "dashes" | "underline" | "heavy" | "middleDot"`。
- 行为：创建 XWPFParagraph，应用样式引用（若指定）或直接属性，渲染子节点。单位自动转换（pt → twips）。
- 实现状态：已实现。

## `<NumberedList>`


- 作用：有序列表（带编号的列表）。
- 子节点：`<ListItem>`。
- 属性：
  - `start`: 整数，起始编号（默认 1）。
  - `format`: 字符串，编号格式（默认 `"decimal"`）。可选值：
    - `"decimal"`: 阿拉伯数字（1, 2, 3...）
    - `"lowerLetter"`: 小写字母（a, b, c...）
    - `"upperLetter"`: 大写字母（A, B, C...）
    - `"lowerRoman"`: 小写罗马数字（i, ii, iii...）
    - `"upperRoman"`: 大写罗马数字（I, II, III...）
  - `levelConfig`: 数组，按层级（0..8）提供高级设置项，元素：
    - `format`: 同上（覆盖全局）。
    - `lvlText`: 自定义级别文本模式（如 `"%1.%2)"`）。
    - `indent`: `{ left: pt, hanging: pt }` 段落缩进（左缩进与悬挂缩进，单位 pt）。
- 行为：创建带编号的编号定义（9 个层级），每个 `<ListItem>` 渲染为带序号的段落。支持多级列表（通过 `<ListItem level={n}/>`）。
- 示例：
```jsx
<NumberedList format="upperRoman" start={3}>
  <ListItem><Paragraph><Text>III. 第三项</Text></Paragraph></ListItem>
  <ListItem level={1}><Paragraph><Text>a. 子项</Text></Paragraph></ListItem>
</NumberedList>
```
- 实现状态：已实现（含多级、起始值、format、levelConfig）。

## `<Text>`


- 作用：文本运行（Run），用于应用文本格式。
- 子节点：纯文本字符串。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的字符样式 ID（可选）。样式引用优先于直接属性。
  - `bold`: 布尔，加粗。
  - `italic`: 布尔，斜体。
  - `size`: 整数磅。
  - `color`: `#RRGGBB`。
  - `underline`: 布尔或字符串。布尔 `true` 等同 `"single"`；字符串可选值：`"single" | "double" | "dotted" | "dash" | "none"`。
  - `strike`: 布尔，删除线。
  - `highlight`: 字符串，高亮颜色（Word 预设名），如 `"yellow"`, `"green"`, `"cyan"` 等。
  - `font`: 字体族名称（如 `"Arial"`）。
- 行为：创建 Run 并应用样式（若指定 `styleId`）或直接属性，渲染文本内容。直接属性会覆盖样式定义。
- 示例：
```jsx
<Text styleId="Highlight">高亮文本</Text>
<Text styleId="CodeText" underline={true}>带下划线的代码</Text>
```
- 实现状态：已实现。

## `<Heading>`


- 作用：标题段落，必须通过样式引用来定义标题级别。
- 子节点：`<Text>`、纯文本。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的段落样式 ID（必需，替代原 `level` 属性）。
- 行为：创建段落并设置样式引用，渲染子节点。
- 示例：
```jsx
<Styles>
  <Style styleId="H1" name="Heading 1" outlineLevel={0} bold={true} fontSize={44}/>
</Styles>
<Heading styleId="H1"><Text>章节标题</Text></Heading>
```
- 实现状态：已实现（不再支持 `level` 属性，必须通过 `styleId` 引用样式）。


## `<Table>`


- 作用：表格容器。
- 子节点：`<Row>`。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的表格样式 ID（可选）。
  - `border`: 布尔或对象。布尔 `false` 关闭所有边框（包括内边框）；对象 `{ size: 1, color: "#RRGGBB" }`，`size` 为磅（pt）。
  - `width`: 字符串，表格宽度。支持百分比（如 `"100%"`，使用 PCT 模式，`100%` = 5000）。
  - `align`: `"left" | "center" | "right"`（表格对齐方式）。
  - `layout`: `"auto" | "fixed"`（表格布局算法）。`"fixed"` 使用固定列宽，需配合 `columns` 属性。
  - `columns`: 数组，列宽定义（数字，单位为磅 pt）。设置此属性会自动启用 `layout="fixed"`。
- 行为：创建表格并移除默认首行；应用样式引用（若指定）；根据属性设置表格边框、宽度（百分比使用 PCT，`100%`=5000）、对齐；渲染行。支持行/列合并（通过 `<Cell>` 的 `colspan`/`rowspan`）。
- 示例：
```jsx
<Table width="100%" border={{ size: 1, color: "#000000" }} columns={[100, 200, 150]}>
  <Row>
    <Cell><Paragraph><Text>列1</Text></Paragraph></Cell>
    <Cell colspan={2}><Paragraph><Text>合并列2-3</Text></Paragraph></Cell>
  </Row>
</Table>
```
- 实现状态：已实现（含样式支持、边框、宽度、对齐、固定列宽、行列合并）。


## `<PageNumber>`


- 作用：页码域（可用于任何段落，包括正文/页眉/页脚）。
- 子节点：无。
- 属性：无。
- 行为：在当前段落插入 `PAGE` 字段（Word 字段），显示当前页码。
- 实现状态：已实现。


## `<Row>`


- 作用：表格行。
- 子节点：`<Cell>`。
- 属性：
  - `header`: 布尔。为 `true` 时此行为表头行（跨页重复显示）。
  - `height`: 数字，行高（磅 pt）。
- 行为：创建行，按顺序渲染单元格；若为表头行则设置跨页重复（`<w:tblHeader/>`）；若设置行高则应用固定高度（内部转换为 twips）。
- 实现状态：已实现。

## `<Cell>`


- 作用：表格单元格。
- 子节点：`<Paragraph>`、`<Table>`（支持嵌套表格）等块级组件。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的段落样式 ID（可选）。样式会应用到单元格的第一个段落。
  - `vAlign`: `"top" | "center" | "bottom"`（垂直对齐）。
  - `padding`: 对象，单元格内边距（单位为磅 pt），字段：`top`, `bottom`, `left`, `right`（缺省字段保持默认）。
  - `width`: 宽度。支持百分比字符串（如 `"70%"`，PCT 模式）或数字（磅 pt，DXA 模式）。
  - `background`: 单元格背景色 `#RRGGBB`。
  - `border`: 布尔或对象。布尔 `false` 关闭四边边框；对象 `{ size: 1, color: "#RRGGBB", sides: ["top","right","bottom","left"] }`，其中 `size` 为磅（pt），`sides` 省略表示四边。
  - `colspan`: 列合并，整数 ≥ 1（默认 1）。通过 `tcPr.gridSpan` 实现。
  - `rowspan`: 行合并，整数 ≥ 1（默认 1）。通过 `tcPr.vMerge` 实现。
- 行为：创建单元格，应用样式引用（若指定）到第一个段落，应用垂直对齐与内边距（pt → twips，写入 `tcPr.vAlign` 与 `tcPr.tcMar`），再渲染子节点。
- 合并行为：
  - `colspan`：首个单元格设置 `gridSpan`，占据多列。
  - `rowspan`：首个单元格设置 `vMerge="restart"`，下方连续行自动插入 `vMerge="continue"` 单元格以保持表格网格一致。
  - 当同时设置 `colspan` 与 `rowspan` 时，系统会为覆盖区域内所有列自动管理 `continue` 单元格。
- 示例：
```jsx
<Cell vAlign="center" padding={{ top: 10, bottom: 10, left: 15, right: 15 }} background="#F0F0F0">
  <Paragraph><Text>单元格内容</Text></Paragraph>
</Cell>
```
- 实现状态：已实现（含所有属性、行列合并）。

## `<PageBreak>`


- 作用：手动分页符。
- 子节点：无。
- 属性：无。
- 行为：插入分页段落。
- 实现状态：已实现。

## `<Toc>`


- 作用：插入目录（Table of Contents），使用 Word TOC 域。
- 子节点：无。
- 属性：
  - `title`: 字符串，目录标题（可选，默认 `"目录"`）。
  - `maxLevel`: 整数，显示的最大标题级别（1..9，默认 `3`）。对应 TOC 域的 `\o "1-maxLevel"`。
  - `hyperlink`: 布尔，目录项是否作为超链接（默认 `true`）。对应 TOC 域的 `\h` 开关。
  - `showPageNumbers`: 布尔，是否显示页码（默认 `true`）。设为 `false` 时使用 `\n` 开关省略页码。
- 行为：
  - 若设置 `title`，先创建标题段落
  - 创建包含 TOC 域的段落，域结构：
    - `fldChar type="begin"`：域开始
    - `instrText`：域指令，如 `"TOC \o \"1-3\" \h \z \u"`
    - `fldChar type="separate"`：分隔符
    - 占位符文本：`"右键点击此处选择\"更新域\"以生成目录"`
    - `fldChar type="end"`：域结束
  - TOC 域依赖文档中段落样式的 `outlineLevel` 属性（在 `<Style>` 中定义）
  - **自动更新**：系统自动在 `settings.xml` 中启用 `<w:updateFields/>` 设置，使文档在 Word 中打开时自动更新所有域（包括 TOC）
  - 目录包含：标题文本、页码、超链接（可跳转到对应标题）
  - 手动更新：也可以右键点击目录区域选择"更新域"（或按 F9）手动刷新
- 示例：
```jsx
<Styles>
  <Style styleId="H1" outlineLevel={0} bold={true} fontSize={44}/>
  <Style styleId="H2" outlineLevel={1} bold={true} fontSize={36}/>
  <Style styleId="H3" outlineLevel={2} bold={true} fontSize={28}/>
</Styles>

<Toc title="目录" maxLevel={3} />

<Heading styleId="H1"><Text>第一章 简介</Text></Heading>
<Heading styleId="H2"><Text>1.1 背景</Text></Heading>
<Heading styleId="H3"><Text>1.1.1 历史</Text></Heading>
```
- 实现状态：本迭代实现。

## `<BulletedList>`


- 作用：项目符号列表（无序列表）。
- 子节点：`<ListItem>`。
- 属性：
  - `bulletChar`: 字符串，项目符号字符（默认为 Wingdings 字体的 'l' 显示为 •）。支持：
    - Wingdings 字符：`"l"` (•圆点), `"n"` (◆菱形), `"ü"` (►三角), `"§"` (◆)
    - 直接 Unicode 字符：如 `"●"`, `"○"`, `"■"`, `"□"`, `"▪"`, `"▫"`, `"►"`, `"•"`
  - `bulletFont`: 字符串，项目符号字体（默认 `"Wingdings"`）。常用字体：
    - `"Wingdings"`: Windows 标准符号字体
    - `"Symbol"`: 数学与希腊符号字体  
    - `"Arial"` / `"Calibri"`: 使用 Unicode 字符时可选普通字体
  - `indentLeft`: 数字，第一级左缩进（单位：twips，默认 420）。1 inch = 1440 twips，420 twips ≈ 0.29"
  - `indentIncrement`: 数字，每级增加的缩进量（单位：twips，默认 360）。360 twips = 0.25"
  - `indentHanging`: 数字，悬挂缩进（单位：twips，默认 420）。悬挂缩进使项目符号突出显示
- 行为：
  - 创建带项目符号的编号定义（9 个层级），每个层级使用相同的 `bulletChar` 和 `bulletFont`
  - 每个 `<ListItem>` 渲染为带项目符号的段落
  - 支持多级列表（`<ListItem level={n}/>`，`n` 范围 0..8）
  - 缩进计算：level N 的左缩进 = `indentLeft + N × indentIncrement`
  - 示例：默认值下，level 0 = 420 twips，level 1 = 780 twips，level 2 = 1140 twips
- 实现状态：已实现（支持多级、自定义项目符号字符、字体与缩进）。

## `<Br>`


- 作用：段落内换行（软换行）。
- 子节点：无。
- 行为：在当前段落插入 `w:br`。
- 实现状态：已实现。

## `<Tab>`


- 作用：段落内制表符。
- 子节点：无。
- 行为：在当前段落插入 `w:tab`。
- 实现状态：已实现。

## `<Include>`


- 作用：在文档中包含外部 JSX 文件的内容，实现组件复用。
- 子节点：无（外部文件的内容会被插入到当前位置）。
- 属性：
  - `path`: 字符串，必需。要包含的 JSX 文件路径，支持相对路径（相对于当前文档所在目录）。
    - 相对路径示例：`"./components/header.jsx"`、`"../shared/footer.jsx"`
    - 路径会使用当前文档的父目录作为基准进行解析
- 行为：
  - 在渲染阶段读取指定的 JSX 文件
  - 使用 Compiler 编译 JSX 为 JavaScript
  - 使用 JsRuntime 执行生成 VNode 树
  - 将生成的内容渲染到当前父节点（如果包含文件的根是 `<Document>`，则跳过根节点只渲染其子节点）
  - 包含的文件可以访问与主文档相同的 `data` 对象（数据上下文共享）
  - 支持嵌套包含（包含的文件中也可以使用 `<Include>`）
  - 自动检测循环包含并抛出 `IllegalStateException`（如 A 包含 B，B 包含 A）
  - 包含的文件路径解析是相对于**包含它的文件**的位置，而非主文档位置
- 错误处理：
  - 缺少 `path` 属性：打印错误信息到 stderr，跳过该组件
  - 文件不存在：打印错误信息到 stderr，跳过该组件，继续渲染
  - 循环包含：抛出 `IllegalStateException` 并显示完整包含链（如 "a.jsx → b.jsx → a.jsx"）
  - 编译或执行错误：打印错误信息到 stderr，跳过该组件
- 示例：
```jsx
// components/header.jsx
<Document>
  <Paragraph align="CENTER" bold="true" fontSize="16">
    <Text>公司名称</Text>
  </Paragraph>
  <Paragraph align="CENTER" fontSize="10">
    <Text>地址信息</Text>
  </Paragraph>
</Document>

// main.jsx
render(
  <Document>
    <Include path="./components/header.jsx" />
    <Paragraph><Text>主文档内容</Text></Paragraph>
    <Include path="./components/footer.jsx" />
  </Document>
);
```
- 使用场景：
  - 页眉/页脚复用
  - 多文档共享的公司信息、免责声明等
  - 表格模板、样式定义的复用
  - 大型文档的模块化组织
- 实现状态：已实现（支持相对路径、嵌套包含、循环检测、数据上下文共享）。

## `<Header>`


- 作用：文档页眉区域内容。
- 子节点：块级组件（如 `<Paragraph>`、`<Text>` 等）。
- 属性：
  - `type`: `"default" | "first" | "even" | "odd"`，其中 `odd` 等价于默认页眉（在启用奇偶页区分时用于奇数页）。
- 行为：根据 `type` 创建对应页眉。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉设置。可在页眉段落中使用 `<PageNumber>`。
- 实现状态：已实现。

## `<Footer>`


- 作用：文档页脚区域内容。
- 子节点：块级组件（如 `<Paragraph>`、`<Text>` 等）。
- 属性：
  - `type`: `"default" | "first" | "even" | "odd"`，含义同 `<Header>`。
- 行为：根据 `type` 创建对应页脚。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉页脚设置。可在页脚段落中使用 `<PageNumber>`。
- 实现状态：已实现。

## `<ListItem>`


- 作用：列表项（仅在 `<BulletedList>` 或 `<NumberedList>` 内使用）。
- 子节点：`<Paragraph>`、`<Text>`、纯文本。
- 属性：
  - `level`: 数字，层级（默认 0，范围 0..8）。用于多级列表。
- 行为：创建段落并设置 `numId` 和 `ilvl=level`，应用父列表的编号样式，再渲染子节点。
- 示例：
```jsx
<BulletedList>
  <ListItem><Paragraph><Text>一级项目</Text></Paragraph></ListItem>
  <ListItem level={1}><Paragraph><Text>二级项目（缩进）</Text></Paragraph></ListItem>
  <ListItem level={2}><Paragraph><Text>三级项目（更深缩进）</Text></Paragraph></ListItem>
</BulletedList>
```
- 实现状态：已实现。


## `<Link>`


- 作用：外部超链接（内联）。
- 子节点：纯文本或 `<Text>`（文本内容）。
- 属性：
  - `href`: 目标 URL。
- 行为：在当前段落创建 `XWPFHyperlinkRun`，文本显示为子节点文本，默认下划线、蓝色。
- 实现状态：本迭代实现。

## `<Image>`


- 作用：内联图片。
- 子节点：无（预留 `alt`）。
- 属性：
  - `src`: 图片来源，支持 `data:` URI（`image/png`、`image/jpeg`）或本地绝对路径。
  - `width`: 宽度（px）。
  - `height`: 高度（px）。
  - `fit`: `"contain" | "scaleDown"`。当设置 `maxWidth`/`maxHeight` 时，按比例计算尺寸：
    - `contain`: 在 `maxWidth`×`maxHeight` 边界内等比缩放（不裁剪）。
    - `scaleDown`: 仅当超过最大边界才缩小；否则使用原尺寸或指定 `width/height`。
  - `maxWidth`: 最大宽度（px）。
  - `maxHeight`: 最大高度（px）。
- 行为：在当前段落创建 `Run` 并通过 `addPicture()` 插入图片，尺寸使用 `Units.toEMU(width|height)`。
- 实现状态：本迭代实现（fit contain/scaleDown；maxWidth/maxHeight 支持；读取图片本身尺寸做等比计算）。

---

## 样式系统说明

### 样式引用机制
- 组件通过 `styleId` 属性引用在 `<Styles>` 中定义的样式
- 样式引用优先应用，直接属性会覆盖样式定义
- 支持三种样式类型：
  - `paragraph`: 用于段落级组件（`<Paragraph>`、`<Heading>`、`<Cell>` 内容）
  - `character`: 用于文本级组件（`<Text>`）
  - `table`: 用于表格组件（`<Table>`）

### 样式组合
可以将样式引用与直接属性组合使用：
```jsx
<Paragraph styleId="Quote" align="CENTER">
  <Text styleId="Highlight" underline={true}>组合样式示例</Text>
</Paragraph>
```

### 支持 styleId 的组件
- `<Paragraph>`: 段落样式（type="paragraph"）
- `<Text>`: 字符样式（type="character"）
- `<Heading>`: 段落样式（type="paragraph"，通常带 outlineLevel）
- `<Table>`: 表格样式（type="table"）
- `<Cell>`: 段落样式（应用到单元格第一个段落）

## 错误与校验
- 根节点必须为 `<Document>`，否则抛出 `IllegalArgumentException`。
- `<Styles>` 必须是 `<Document>` 的第一个子节点（如果存在）。
- `<Style>` 缺少 `styleId` 时会输出警告并跳过该样式定义。
- 组件引用不存在的 `styleId` 时，Word 会使用默认格式（无错误）。
- `<Link>` 缺少 `href` 时忽略为普通文本；`<Image>` 无法读取 `src` 时忽略插入并继续渲染。
- 未识别组件：记录到 `stderr` 并继续。

## 未来扩展（不在本迭代）
- 更多样式属性（边框、阴影、字符间距等）
- 样式继承链（basedOn 的完整支持）
- 主题色与样式模板绑定
- 脚注/尾注、条件格式等
