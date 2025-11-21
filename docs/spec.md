# JSX-Docx 组件规范

本文档定义了系统中可用的 JSX 组件、属性、子节点约束以及渲染到 DOCX 的行为。规范优先，代码实现需与本规范一致；未实现项会在“实现状态”中标注。

## 公共约定
 - 子节点：`<Section>`、`<Paragraph>`、`<Heading>`、`<Table>`、`<PageBreak>`、`<Image>`、`<Link>`、`<BulletedList>`、`<NumberedList>`、`<Header>`、`<Footer>`、纯文本。
- 文本节点：字符串会渲染为段落或文字的文本内容。
- 颜色：统一使用 `#RRGGBB`，渲染时自动去掉 `#`。

  - `orientation`: `"portrait" | "landscape"`（页面方向）。
## <Document>
- 属性：无。
- 行为：创建 XWPFDocument；非 `<Section>` 的子节点在默认节中渲染。
 - 属性：
   - `type`: `"default" | "first" | "even" | "odd"`，其中 `odd` 等价于默认页眉（在启用奇偶页区分时用于奇数页）。
 - 行为：根据 `type` 创建对应页眉。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉设置。可在页眉段落中使用 `<PageNumber>`。
- 属性：
  - `align`: `"left" | "center" | "right" | "both"`。
 - 属性：
   - `type`: `"default" | "first" | "even" | "odd"`，含义同 `<Header>`。
 - 行为：根据 `type` 创建对应页脚。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉页脚设置。可在页脚段落中使用 `<PageNumber>`。
  - `border`: 布尔或对象。布尔 `false` 关闭四边边框；对象 `{ size: 1, color: "#RRGGBB", sides: ["top","right","bottom","left"] }`，`size` 为磅（pt），`sides` 省略表示四边。
  - `tabStops`: 数组，段落制表位设置。元素为 `{ pos, align, leader }`：
    - `align`: `"left" | "center" | "right" | "decimal" | "bar"`。
    - `leader`: `"none" | "dots" | "dashes" | "underline" | "heavy" | "middleDot"`。
  - `levelConfig`: 数组，按层级（0..8）提供高级设置项，元素：
    - `format`: 同上（覆盖全局）。
    - `lvlText`: 自定义级别文本模式（如 `"%1.%2)"`）。
    - `indent`: `{ left: pt, hanging: pt }` 段落缩进（左缩进与悬挂缩进，单位 pt）。

## <Text>
  - `bold`: 布尔，加粗。
  - `italic`: 布尔，斜体。
  - `size`: 整数磅。
  - `color`: `#RRGGBB`。
  - `underline`: 布尔或字符串。布尔 `true` 等同 `"single"`；字符串可选值：`"single" | "double" | "dotted" | "dash" | "none"`。
  - `strike`: 布尔，删除线。
  - `highlight`: 字符串，高亮颜色（Word 预设名），如 `"yellow"`, `"green"`, `"cyan"` 等。
  - `font`: 字体族名称（如 `"Arial"`）。

## <Heading>
  - `level`: 整数 `1..6`，映射样式 `Heading{level}`。
- 行为：创建段落并设置样式 `Heading{level}`，渲染子节点。
- 实现状态：已实现。
## <Table>
- 子节点：`<Row>`。
- 属性：
- 行为：创建表格并移除默认首行；根据属性设置表格边框、宽度（百分比使用 PCT，`100%`=5000）、对齐；渲染行。
## <PageNumber>
- 作用：页码域（可用于任何段落，包括正文/页眉/页脚）。
- 子节点：无。
- 属性：无。
- 行为：在当前段落插入 `PAGE` 字段（Word 字段），显示当前页码。
- 实现状态：已实现。
## <Row>
- 作用：表格行。
- 子节点：`<Cell>`。
- 属性：
  - `header`: 布尔。为 `true` 时此行为表头行（跨页重复）。
  - `height`: 行高（磅，pt）。
- 行为：创建行，按顺序渲染单元格；若为表头行则设置跨页重复；若设置行高则应用固定高度。
- 实现状态：`header` 已实现；`height` 本迭代实现。

## <Cell>
- 作用：表格单元格。
- 子节点：块级组件或 `<Paragraph>`。
- 属性：
  - `vAlign`: `"top" | "center" | "bottom"`（垂直对齐）。
  - `padding`: 对象，单位为磅（pt），字段：`top`, `bottom`, `left`, `right`（缺省字段保持默认）。
  - `width`: 宽度。支持百分比字符串（如 `"70%"`，PCT 模式）或数字（磅 pt，DXA 模式）。
  - `background`: 单元格背景色 `#RRGGBB`。
  - `border`: 布尔或对象。布尔 `false` 关闭四边边框；对象形如 `{ size: 1, color: "#RRGGBB", sides: ["top","right","bottom","left"] }`，其中 `size` 为磅（pt），`sides` 省略表示四边。
  - `colspan`: 列合并，整数 ≥ 1（默认 1）。
  - `rowspan`: 行合并，整数 ≥ 1（默认 1）。
- 行为：创建单元格，应用垂直对齐与内边距（pt → twips，写入 `tcPr.vAlign` 与 `tcPr.tcMar`），再渲染子节点（段落会追加）。
- 实现状态：`vAlign`、`padding` 已实现；`width` 已实现；`background` 本迭代实现。

合并行为说明：
- `colspan` 通过 `tcPr.gridSpan` 生效；`rowspan` 通过 `tcPr.vMerge` 生效，首个单元格为 `restart`，下方连续行自动插入 `continue` 单元格以保持表格网格一致。
- 当同时设置 `colspan` 与 `rowspan` 时，系统会为覆盖区域内所有列自动管理 `continue` 单元格。

## <PageBreak>
- 作用：手动分页符。
- 子节点：无。
- 属性：无。
- 行为：插入分页段落。
- 实现状态：已实现。

## <BulletedList>
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

## <NumberedList>
- 属性：
  - `start`: 起始编号（默认 1）。
  - `format`: `"decimal" | "lowerLetter" | "upperLetter" | "lowerRoman" | "upperRoman"`（默认 `decimal`）。
- 行为：根据 `format` 创建编号样式（9 个层级）；根据 `start` 设置实例级起始值；每个 `<ListItem>` 渲染为带序号的段落。支持多级列表（`<ListItem level={n}/>`，`n` 范围 0..8）。
- 实现状态：已实现（含多级与起始值、format）。

## <Br>
- 作用：段落内换行（软换行）。
- 子节点：无。
- 行为：在当前段落插入 `w:br`。
- 实现状态：已实现。

## <Tab>
- 作用：段落内制表符。
- 子节点：无。
- 行为：在当前段落插入 `w:tab`。
- 实现状态：已实现。

## <Header>
- 作用：文档页眉区域内容。
- 子节点：块级组件（如 `<Paragraph>`、`<Text>` 等）。
- 属性：
  - `type`: `"default" | "first" | "even" | "odd"`，其中 `odd` 等价于默认页眉（在启用奇偶页区分时用于奇数页）。
- 行为：根据 `type` 创建对应页眉。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉设置。可在页眉段落中使用 `<PageNumber>`。
- 实现状态：已实现。

## <Footer>
- 作用：文档页脚区域内容。
- 子节点：块级组件（如 `<Paragraph>`、`<Text>` 等）。
- 属性：
  - `type`: `"default" | "first" | "even" | "odd"`，含义同 `<Header>`。
- 行为：根据 `type` 创建对应页脚。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉页脚设置。可在页脚段落中使用 `<PageNumber>`。
- 实现状态：已实现。

## <ListItem>
- 属性：
  - `level`: 数字，层级（默认 0，范围 0..8）。
- 行为：创建段落并设置 `numId` 和 `ilvl=level`，再渲染子节点。
- 实现状态：已实现。
## <Link>
- 作用：外部超链接（内联）。
- 子节点：纯文本或 `<Text>`（文本内容）。
- 属性：
  - `href`: 目标 URL。
- 行为：在当前段落创建 `XWPFHyperlinkRun`，文本显示为子节点文本，默认下划线、蓝色。
- 实现状态：本迭代实现。

## <Image>
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

## 错误与校验
- 根节点必须为 `<Document>`，否则抛出 `IllegalArgumentException`。
- `<Link>` 缺少 `href` 时忽略为普通文本；`<Image>` 无法读取 `src` 时忽略插入并继续渲染。
- 未识别组件：记录到 `stderr` 并继续。

## 未来扩展（不在本迭代）
- 段落/表格对齐与间距、行高、边框与样式。
- 主题色与样式模板绑定。
- 脚注/尾注、奇偶页与首页页眉页脚、按层级控制的列表缩进与文本格式、样式模板等。
