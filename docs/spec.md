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
 - 子节点：`<Styles>`、`<Section>`、`<Paragraph>`、`<Heading>`、`<Table>`、`<PageBreak>`、`<Image>`、`<Link>`、`<BulletedList>`、`<NumberedList>`、`<Header>`、`<Footer>`、纯文本。
- 文本节点：字符串会渲染为段落或文字的文本内容。
- 颜色：统一使用 `#RRGGBB`，渲染时自动去掉 `#`。
- 样式引用：多数组件支持 `styleId` 属性引用在 `<Styles>` 中定义的样式。

  - `orientation`: `"portrait" | "landscape"`（页面方向）。
## <Document>
- 属性：无。
- 子节点：`<Styles>`（可选，唯一）、`<Section>`、`<Paragraph>`、`<Heading>`、`<Table>`、`<PageBreak>`、`<Image>`、`<Link>`、`<BulletedList>`、`<NumberedList>`、`<Header>`、`<Footer>`、纯文本。
- 行为：创建 XWPFDocument；`<Styles>` 组件会生成文档样式定义；非 `<Section>` 的子节点在默认节中渲染。
## <Styles>
- 作用：定义文档样式（必须在 `<Document>` 的第一个子节点位置）。
- 子节点：`<Style>`（一个或多个样式定义）。
- 属性：无。
- 行为：生成 Word 样式定义（styles.xml），包含 docDefaults 和所有 `<Style>` 子节点定义的样式。
- 实现状态：已实现。

## <Style>
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

 - 属性：
   - `type`: `"default" | "first" | "even" | "odd"`，其中 `odd` 等价于默认页眉（在启用奇偶页区分时用于奇数页）。
 - 行为：根据 `type` 创建对应页眉。`first` 会启用 `titlePg`；`even`/`odd` 会启用奇偶页页眉设置。可在页眉段落中使用 `<PageNumber>`。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的段落样式 ID（可选）。
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

## <Heading>
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
## <Table>
- 子节点：`<Row>`。
- 属性：
  - `styleId`: 字符串，引用 `<Styles>` 中定义的表格样式 ID（可选）。
- 行为：创建表格并移除默认首行；应用样式引用（若指定）；根据属性设置表格边框、宽度（百分比使用 PCT，`100%`=5000）、对齐；渲染行。
- 实现状态：已实现（含样式支持）。
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
  - `styleId`: 字符串，引用 `<Styles>` 中定义的段落样式 ID（可选）。样式会应用到单元格的第一个段落。
  - `vAlign`: `"top" | "center" | "bottom"`（垂直对齐）。
  - `padding`: 对象，单位为磅（pt），字段：`top`, `bottom`, `left`, `right`（缺省字段保持默认）。
  - `width`: 宽度。支持百分比字符串（如 `"70%"`，PCT 模式）或数字（磅 pt，DXA 模式）。
  - `background`: 单元格背景色 `#RRGGBB`。
  - `border`: 布尔或对象。布尔 `false` 关闭四边边框；对象形如 `{ size: 1, color: "#RRGGBB", sides: ["top","right","bottom","left"] }`，其中 `size` 为磅（pt），`sides` 省略表示四边。
  - `colspan`: 列合并，整数 ≥ 1（默认 1）。
  - `rowspan`: 行合并，整数 ≥ 1（默认 1）。
- 行为：创建单元格，应用样式引用（若指定）到第一个段落，应用垂直对齐与内边距（pt → twips，写入 `tcPr.vAlign` 与 `tcPr.tcMar`），再渲染子节点（段落会追加）。
- 实现状态：`vAlign`、`padding`、`width`、`background`、`styleId` 已实现。

合并行为说明：
- `colspan` 通过 `tcPr.gridSpan` 生效；`rowspan` 通过 `tcPr.vMerge` 生效，首个单元格为 `restart`，下方连续行自动插入 `continue` 单元格以保持表格网格一致。
- 当同时设置 `colspan` 与 `rowspan` 时，系统会为覆盖区域内所有列自动管理 `continue` 单元格。

## <PageBreak>
- 作用：手动分页符。
- 子节点：无。
- 属性：无。
- 行为：插入分页段落。
- 实现状态：已实现。

## <Toc>
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

## <Include>
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
