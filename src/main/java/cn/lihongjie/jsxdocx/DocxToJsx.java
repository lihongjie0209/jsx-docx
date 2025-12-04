package cn.lihongjie.jsxdocx;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Reverse engineering tool: Convert Word documents (.docx) to JSX format.
 * This allows users to import existing Word documents and modify them using jsx-docx.
 */
public class DocxToJsx {
    
    private final XWPFDocument document;
    private final StringBuilder jsx;
    private int indentLevel = 0;
    private final String indentStr = "  "; // 2 spaces
    private final Set<BigInteger> processedNumIds = new HashSet<>();
    
    public DocxToJsx(XWPFDocument document) {
        this.document = document;
        this.jsx = new StringBuilder();
    }
    
    /**
     * Convert a DOCX file to JSX string.
     */
    public static String convert(String docxPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(docxPath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            DocxToJsx converter = new DocxToJsx(doc);
            return converter.toJsx();
        }
    }
    
    /**
     * Convert a DOCX file to JSX and save to file.
     */
    public static void convertToFile(String docxPath, String jsxPath) throws IOException {
        String jsx = convert(docxPath);
        Files.writeString(Path.of(jsxPath), jsx);
    }
    
    /**
     * Main conversion method.
     */
    public String toJsx() {
        appendLine("<Document>");
        indentLevel++;
        
        // Convert section with page properties
        convertSection();
        
        indentLevel--;
        appendLine("</Document>");
        
        return jsx.toString();
    }
    
    private void convertSection() {
        appendLine("<Section" + getSectionProps() + ">");
        indentLevel++;
        
        // Convert headers
        convertHeaders();
        
        // Convert body elements
        List<IBodyElement> elements = document.getBodyElements();
        convertBodyElements(elements);
        
        // Convert footers
        convertFooters();
        
        indentLevel--;
        appendLine("</Section>");
    }
    
    private String getSectionProps() {
        StringBuilder props = new StringBuilder();
        
        try {
            CTSectPr sectPr = document.getDocument().getBody().getSectPr();
            if (sectPr != null) {
                // Page size
                if (sectPr.isSetPgSz()) {
                    CTPageSz pgSz = sectPr.getPgSz();
                    Object wObj = pgSz.getW();
                    Object hObj = pgSz.getH();
                    BigInteger w = wObj instanceof BigInteger ? (BigInteger) wObj : null;
                    BigInteger h = hObj instanceof BigInteger ? (BigInteger) hObj : null;
                    
                    // Check for standard sizes
                    if (isA4(w, h)) {
                        props.append(" pageSize=\"A4\"");
                    } else if (isLetter(w, h)) {
                        props.append(" pageSize=\"LETTER\"");
                    } else if (w != null && h != null) {
                        props.append(" pageSize={{width: ").append(w).append(", height: ").append(h).append("}}");
                    }
                    // If both null, don't add pageSize prop
                    
                    // Orientation
                    if (pgSz.getOrient() != null && pgSz.getOrient() == STPageOrientation.LANDSCAPE) {
                        props.append(" orientation=\"landscape\"");
                    }
                }
                
                // Margins - output as margins={{top: X, bottom: Y, left: Z, right: W}} in inches
                if (sectPr.isSetPgMar()) {
                    CTPageMar pgMar = sectPr.getPgMar();
                    Object topObj = pgMar.getTop();
                    Object bottomObj = pgMar.getBottom();
                    Object leftObj = pgMar.getLeft();
                    Object rightObj = pgMar.getRight();
                    
                    // Convert Object to BigInteger safely
                    BigInteger top = topObj instanceof BigInteger ? (BigInteger) topObj : null;
                    BigInteger bottom = bottomObj instanceof BigInteger ? (BigInteger) bottomObj : null;
                    BigInteger left = leftObj instanceof BigInteger ? (BigInteger) leftObj : null;
                    BigInteger right = rightObj instanceof BigInteger ? (BigInteger) rightObj : null;
                    
                    // Only output if we have any margin values
                    if (top != null || bottom != null || left != null || right != null) {
                        StringBuilder marginParts = new StringBuilder();
                        if (top != null) marginParts.append("top: ").append(twipsToInches(top));
                        if (bottom != null) {
                            if (marginParts.length() > 0) marginParts.append(", ");
                            marginParts.append("bottom: ").append(twipsToInches(bottom));
                        }
                        if (left != null) {
                            if (marginParts.length() > 0) marginParts.append(", ");
                            marginParts.append("left: ").append(twipsToInches(left));
                        }
                        if (right != null) {
                            if (marginParts.length() > 0) marginParts.append(", ");
                            marginParts.append("right: ").append(twipsToInches(right));
                        }
                        props.append(" margins={{").append(marginParts).append("}}");
                    }
                }
            }
        } catch (Exception e) {
            // Ignore errors, use defaults
        }
        
        return props.toString();
    }
    
    /**
     * Convert twips to inches (1 inch = 1440 twips)
     */
    private double twipsToInches(BigInteger twips) {
        return Math.round(twips.doubleValue() / 1440.0 * 100.0) / 100.0;
    }
    
    private boolean isA4(BigInteger w, BigInteger h) {
        // A4: 11906 x 16838 twips (portrait)
        return (w != null && h != null && 
                w.intValue() >= 11900 && w.intValue() <= 11910 &&
                h.intValue() >= 16830 && h.intValue() <= 16850);
    }
    
    private boolean isLetter(BigInteger w, BigInteger h) {
        // Letter: 12240 x 15840 twips (portrait)
        return (w != null && h != null && 
                w.intValue() >= 12230 && w.intValue() <= 12250 &&
                h.intValue() >= 15830 && h.intValue() <= 15850);
    }
    
    private void convertHeaders() {
        try {
            XWPFHeaderFooterPolicy policy = document.getHeaderFooterPolicy();
            if (policy != null) {
                XWPFHeader defaultHeader = policy.getDefaultHeader();
                if (defaultHeader != null) {
                    appendLine("<Header type=\"DEFAULT\">");
                    indentLevel++;
                    convertHeaderFooterContent(defaultHeader);
                    indentLevel--;
                    appendLine("</Header>");
                }
                
                XWPFHeader firstHeader = policy.getFirstPageHeader();
                if (firstHeader != null) {
                    appendLine("<Header type=\"FIRST\">");
                    indentLevel++;
                    convertHeaderFooterContent(firstHeader);
                    indentLevel--;
                    appendLine("</Header>");
                }
            }
        } catch (Exception e) {
            // Ignore header errors
        }
    }
    
    private void convertFooters() {
        try {
            XWPFHeaderFooterPolicy policy = document.getHeaderFooterPolicy();
            if (policy != null) {
                XWPFFooter defaultFooter = policy.getDefaultFooter();
                if (defaultFooter != null) {
                    appendLine("<Footer type=\"DEFAULT\">");
                    indentLevel++;
                    convertHeaderFooterContent(defaultFooter);
                    indentLevel--;
                    appendLine("</Footer>");
                }
                
                XWPFFooter firstFooter = policy.getFirstPageFooter();
                if (firstFooter != null) {
                    appendLine("<Footer type=\"FIRST\">");
                    indentLevel++;
                    convertHeaderFooterContent(firstFooter);
                    indentLevel--;
                    appendLine("</Footer>");
                }
            }
        } catch (Exception e) {
            // Ignore footer errors
        }
    }
    
    private void convertHeaderFooterContent(XWPFHeaderFooter hf) {
        for (IBodyElement element : hf.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                convertParagraph((XWPFParagraph) element);
            } else if (element instanceof XWPFTable) {
                convertTable((XWPFTable) element);
            }
        }
    }
    
    private void convertBodyElements(List<IBodyElement> elements) {
        List<XWPFParagraph> currentListItems = new ArrayList<>();
        BigInteger currentNumId = null;
        
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                BigInteger numId = para.getNumID();
                
                if (numId != null) {
                    // This is a list item
                    if (currentNumId == null || !currentNumId.equals(numId)) {
                        // Flush previous list if any
                        if (!currentListItems.isEmpty()) {
                            flushList(currentListItems, currentNumId);
                            currentListItems.clear();
                        }
                        currentNumId = numId;
                    }
                    currentListItems.add(para);
                } else {
                    // Not a list item, flush any pending list
                    if (!currentListItems.isEmpty()) {
                        flushList(currentListItems, currentNumId);
                        currentListItems.clear();
                        currentNumId = null;
                    }
                    
                    // Check if it's a heading
                    String style = para.getStyle();
                    if (style != null && style.matches("Heading\\d|标题\\d")) {
                        convertHeading(para, style);
                    } else {
                        convertParagraph(para);
                    }
                }
            } else if (element instanceof XWPFTable) {
                // Flush any pending list
                if (!currentListItems.isEmpty()) {
                    flushList(currentListItems, currentNumId);
                    currentListItems.clear();
                    currentNumId = null;
                }
                convertTable((XWPFTable) element);
            }
        }
        
        // Flush remaining list items
        if (!currentListItems.isEmpty()) {
            flushList(currentListItems, currentNumId);
        }
    }
    
    private void flushList(List<XWPFParagraph> items, BigInteger numId) {
        if (items.isEmpty()) return;
        
        // Determine if bulleted or numbered
        boolean isBulleted = isBulletedList(items.get(0));
        
        if (isBulleted) {
            appendLine("<BulletedList>");
        } else {
            appendLine("<NumberedList>");
        }
        indentLevel++;
        
        for (XWPFParagraph item : items) {
            int level = getListLevel(item);
            if (level > 0) {
                appendLine("<ListItem level={" + level + "}>");
            } else {
                appendLine("<ListItem>");
            }
            indentLevel++;
            convertParagraphContent(item);
            indentLevel--;
            appendLine("</ListItem>");
        }
        
        indentLevel--;
        if (isBulleted) {
            appendLine("</BulletedList>");
        } else {
            appendLine("</NumberedList>");
        }
    }
    
    private boolean isBulletedList(XWPFParagraph para) {
        try {
            BigInteger numId = para.getNumID();
            if (numId != null) {
                XWPFNumbering numbering = document.getNumbering();
                if (numbering != null) {
                    // Direct approach: iterate all abstract nums and check their format
                    // Since our Renderer creates one abstract num per list, we can use numId to index
                    java.util.List<XWPFAbstractNum> abstractNums = numbering.getAbstractNums();
                    if (abstractNums != null && !abstractNums.isEmpty()) {
                        // Try to find by matching numId - 1 as index (0-indexed vs 1-indexed)
                        int idx = numId.intValue() - 1;
                        if (idx >= 0 && idx < abstractNums.size()) {
                            XWPFAbstractNum abstractNum = abstractNums.get(idx);
                            if (abstractNum != null && abstractNum.getCTAbstractNum() != null) {
                                CTAbstractNum ctAbstractNum = abstractNum.getCTAbstractNum();
                                if (ctAbstractNum.sizeOfLvlArray() > 0) {
                                    CTLvl lvl = ctAbstractNum.getLvlArray(0);
                                    return checkLvlIsBullet(lvl);
                                }
                            }
                        }
                        // Fallback: if only one abstract num, use it
                        if (abstractNums.size() == 1) {
                            XWPFAbstractNum abstractNum = abstractNums.get(0);
                            if (abstractNum != null && abstractNum.getCTAbstractNum() != null) {
                                CTAbstractNum ctAbstractNum = abstractNum.getCTAbstractNum();
                                if (ctAbstractNum.sizeOfLvlArray() > 0) {
                                    CTLvl lvl = ctAbstractNum.getLvlArray(0);
                                    return checkLvlIsBullet(lvl);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Default to bulleted
        }
        return true;
    }
    
    private boolean checkLvlIsBullet(CTLvl lvl) {
        if (lvl == null) return true;
        // Check numFmt
        if (lvl.getNumFmt() != null) {
            try {
                STNumberFormat.Enum fmt = lvl.getNumFmt().getVal();
                if (fmt == STNumberFormat.BULLET) {
                    return true;
                }
                // decimal, lowerLetter, upperLetter, lowerRoman, upperRoman are numbered
                if (fmt == STNumberFormat.DECIMAL || fmt == STNumberFormat.LOWER_LETTER || 
                    fmt == STNumberFormat.UPPER_LETTER || fmt == STNumberFormat.LOWER_ROMAN ||
                    fmt == STNumberFormat.UPPER_ROMAN) {
                    return false;
                }
            } catch (Exception fmtEx) {
                // numFmt parsing failed, try lvlText method
            }
        }
        // Fallback: Check lvlText - if it contains %1, %2 etc., it's numbered
        if (lvl.getLvlText() != null) {
            String lvlText = lvl.getLvlText().getVal();
            if (lvlText != null && lvlText.contains("%")) {
                return false; // Numbered list
            }
        }
        return true;
    }
    
    private int getListLevel(XWPFParagraph para) {
        BigInteger level = para.getNumIlvl();
        return level != null ? level.intValue() : 0;
    }
    
    private void convertHeading(XWPFParagraph para, String style) {
        int level = 1;
        try {
            // Extract level from style name (e.g., "Heading1" -> 1)
            String numStr = style.replaceAll("\\D+", "");
            if (!numStr.isEmpty()) {
                level = Integer.parseInt(numStr);
            }
        } catch (Exception e) {
            level = 1;
        }
        
        StringBuilder tag = new StringBuilder();
        tag.append("<Heading level={").append(level).append("}");
        
        // Add alignment if not default
        ParagraphAlignment align = para.getAlignment();
        if (align != null && align != ParagraphAlignment.LEFT) {
            tag.append(" align=\"").append(align.name()).append("\"");
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        // Convert runs
        convertRuns(para.getRuns());
        
        indentLevel--;
        appendLine("</Heading>");
    }
    
    private void convertParagraph(XWPFParagraph para) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Paragraph");
        
        // Alignment
        ParagraphAlignment align = para.getAlignment();
        if (align != null && align != ParagraphAlignment.LEFT) {
            tag.append(" align=\"").append(align.name()).append("\"");
        }
        
        // Spacing (spec.md uses before/after in pt, POI returns twips - convert back)
        int spacingBefore = para.getSpacingBefore();
        if (spacingBefore > 0) {
            // Convert twips to pt (1 pt = 20 twips)
            double beforePt = spacingBefore / 20.0;
            tag.append(" before={").append(formatNumber(beforePt)).append("}");
        }
        
        int spacingAfter = para.getSpacingAfter();
        if (spacingAfter > 0) {
            double afterPt = spacingAfter / 20.0;
            tag.append(" after={").append(formatNumber(afterPt)).append("}");
        }
        
        // Line spacing (spec.md uses multiplier like 1.5)
        double lineSpacing = para.getSpacingBetween();
        if (lineSpacing > 0 && lineSpacing != 1.0) {
            tag.append(" line={").append(formatNumber(lineSpacing)).append("}");
        }
        
        // Indentation (spec.md uses pt, POI returns twips - convert)
        int indentLeft = para.getIndentationLeft();
        if (indentLeft > 0) {
            double leftPt = indentLeft / 20.0;
            tag.append(" indentLeft={").append(formatNumber(leftPt)).append("}");
        }
        
        int indentRight = para.getIndentationRight();
        if (indentRight > 0) {
            double rightPt = indentRight / 20.0;
            tag.append(" indentRight={").append(formatNumber(rightPt)).append("}");
        }
        
        int firstLineIndent = para.getIndentationFirstLine();
        if (firstLineIndent > 0) {
            double firstLinePt = firstLineIndent / 20.0;
            tag.append(" firstLine={").append(formatNumber(firstLinePt)).append("}");
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        convertParagraphRuns(para);
        
        indentLevel--;
        appendLine("</Paragraph>");
    }
    
    private void convertParagraphContent(XWPFParagraph para) {
        // Used by ListItem - wrap content in Paragraph for correct structure
        appendLine("<Paragraph>");
        indentLevel++;
        convertRuns(para.getRuns());
        indentLevel--;
        appendLine("</Paragraph>");
    }
    
    private void convertParagraphRuns(XWPFParagraph para) {
        // Used by convertParagraph - just output runs, no wrapper
        convertRuns(para.getRuns());
    }
    
    private void convertRuns(List<XWPFRun> runs) {
        for (XWPFRun run : runs) {
            // Check for images
            List<XWPFPicture> pictures = run.getEmbeddedPictures();
            if (!pictures.isEmpty()) {
                for (XWPFPicture pic : pictures) {
                    convertImage(pic);
                }
                continue;
            }
            
            // Check for page number field
            String text = run.getText(0);
            if (text == null || text.isEmpty()) {
                // Check for field codes
                CTR ctr = run.getCTR();
                if (ctr != null) {
                    // Check for page field
                    if (ctr.sizeOfFldCharArray() > 0 || ctr.sizeOfInstrTextArray() > 0) {
                        // Might be a field, check parent paragraph
                        continue;
                    }
                    // Check for tab
                    if (ctr.sizeOfTabArray() > 0) {
                        appendLine("<Tab />");
                        continue;
                    }
                    // Check for break
                    if (ctr.sizeOfBrArray() > 0) {
                        appendLine("<Br />");
                        continue;
                    }
                }
                continue;
            }
            
            convertTextRun(run, text);
        }
    }
    
    private void convertTextRun(XWPFRun run, String text) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Text");
        
        // Bold
        if (run.isBold()) {
            tag.append(" bold={true}");
        }
        
        // Italic
        if (run.isItalic()) {
            tag.append(" italic={true}");
        }
        
        // Underline
        UnderlinePatterns underline = run.getUnderline();
        if (underline != null && underline != UnderlinePatterns.NONE) {
            tag.append(" underline={true}");
        }
        
        // Strike
        if (run.isStrikeThrough()) {
            tag.append(" strike={true}");
        }
        
        // Font size (spec.md uses 'size' in pt)
        int fontSize = run.getFontSize();
        if (fontSize > 0) {
            tag.append(" size={").append(fontSize).append("}");
        }
        
        // Font family (spec.md uses 'font')
        String fontFamily = run.getFontFamily();
        if (fontFamily != null && !fontFamily.isEmpty()) {
            tag.append(" font=\"").append(escapeJsx(fontFamily)).append("\"");
        }
        
        // Color
        String color = run.getColor();
        if (color != null && !color.isEmpty() && !"000000".equals(color)) {
            tag.append(" color=\"#").append(color).append("\"");
        }
        
        // Highlight/background
        STHighlightColor.Enum highlight = run.getTextHightlightColor();
        if (highlight != null && highlight != STHighlightColor.NONE) {
            tag.append(" highlight=\"").append(highlight.toString().toLowerCase()).append("\"");
        }
        
        tag.append(">");
        
        // Escape text content
        String escapedText = escapeJsx(text);
        
        appendLine(tag.toString() + escapedText + "</Text>");
    }
    
    private void convertImage(XWPFPicture pic) {
        try {
            XWPFPictureData picData = pic.getPictureData();
            if (picData != null) {
                // Get dimensions
                int width = (int) (pic.getCTPicture().getSpPr().getXfrm().getExt().getCx() / 9525); // EMUs to pixels
                int height = (int) (pic.getCTPicture().getSpPr().getXfrm().getExt().getCy() / 9525);
                
                // Convert to base64
                byte[] data = picData.getData();
                String base64 = Base64.getEncoder().encodeToString(data);
                String mimeType = picData.getPackagePart().getContentType();
                String dataUri = "data:" + mimeType + ";base64," + base64;
                
                StringBuilder tag = new StringBuilder();
                tag.append("<Image src=\"").append(dataUri).append("\"");
                if (width > 0) {
                    tag.append(" width={").append(width).append("}");
                }
                if (height > 0) {
                    tag.append(" height={").append(height).append("}");
                }
                tag.append(" />");
                
                appendLine(tag.toString());
            }
        } catch (Exception e) {
            appendLine("{/* Image conversion failed */}");
        }
    }
    
    private void convertTable(XWPFTable table) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Table");
        
        // Table width
        try {
            CTTblPr tblPr = table.getCTTbl().getTblPr();
            if (tblPr != null && tblPr.isSetTblW()) {
                CTTblWidth tblW = tblPr.getTblW();
                if (tblW.getType() == STTblWidth.DXA) {
                    tag.append(" width={").append(tblW.getW()).append("}");
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Table alignment
        TableRowAlign align = table.getTableAlignment();
        if (align != null && align != TableRowAlign.LEFT) {
            tag.append(" align=\"").append(align.name()).append("\"");
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        // Convert rows
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            convertTableRow(row, i == 0);
        }
        
        indentLevel--;
        appendLine("</Table>");
    }
    
    private void convertTableRow(XWPFTableRow row, boolean isFirstRow) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Row");
        
        // Check if header row (heuristic: first row with bold text)
        if (isFirstRow && isHeaderRow(row)) {
            tag.append(" header={true}");
        }
        
        // Row height
        int height = row.getHeight();
        if (height > 0) {
            tag.append(" height={").append(height).append("}");
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        // Convert cells
        for (XWPFTableCell cell : row.getTableCells()) {
            convertTableCell(cell);
        }
        
        indentLevel--;
        appendLine("</Row>");
    }
    
    private boolean isHeaderRow(XWPFTableRow row) {
        // Check if all cells have bold text
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph para : cell.getParagraphs()) {
                for (XWPFRun run : para.getRuns()) {
                    if (run.isBold()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void convertTableCell(XWPFTableCell cell) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Cell");
        
        // Cell width
        try {
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                // Width
                if (tcPr.isSetTcW()) {
                    tag.append(" width={").append(tcPr.getTcW().getW()).append("}");
                }
                
                // Colspan
                if (tcPr.isSetGridSpan()) {
                    int span = tcPr.getGridSpan().getVal().intValue();
                    if (span > 1) {
                        tag.append(" colspan={").append(span).append("}");
                    }
                }
                
                // Rowspan (vMerge)
                if (tcPr.isSetVMerge()) {
                    STMerge.Enum merge = tcPr.getVMerge().getVal();
                    if (merge == STMerge.RESTART) {
                        // Count subsequent merged cells
                        // Note: This is simplified, actual rowspan calculation is complex
                        tag.append(" {/* rowspan start */}");
                    }
                }
                
                // Background color (spec.md uses 'background')
                if (tcPr.isSetShd()) {
                    Object fillObj = tcPr.getShd().getFill();
                    String fill = null;
                    if (fillObj instanceof byte[]) {
                        // POI returns byte[] for color values - convert to hex string
                        byte[] bytes = (byte[]) fillObj;
                        StringBuilder hex = new StringBuilder();
                        for (byte b : bytes) {
                            hex.append(String.format("%02X", b & 0xFF));
                        }
                        fill = hex.toString();
                    } else if (fillObj != null) {
                        fill = fillObj.toString();
                    }
                    if (fill != null && !fill.isEmpty() && !"auto".equals(fill)) {
                        tag.append(" background=\"#").append(fill).append("\"");
                    }
                }
                
                // Vertical alignment
                if (tcPr.isSetVAlign()) {
                    STVerticalJc.Enum vAlign = tcPr.getVAlign().getVal();
                    if (vAlign != null && vAlign != STVerticalJc.TOP) {
                        tag.append(" verticalAlign=\"").append(vAlign.toString().toLowerCase()).append("\"");
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        // Convert cell content, skip empty paragraphs
        List<XWPFParagraph> paras = cell.getParagraphs();
        for (XWPFParagraph para : paras) {
            // Skip empty paragraphs (POI creates default empty paragraphs in cells)
            if (para.getRuns().isEmpty() && para.getText().trim().isEmpty()) {
                continue;
            }
            convertCellParagraph(para);
        }
        
        // If no content was added, create empty paragraph
        if (paras.stream().allMatch(p -> p.getRuns().isEmpty() && p.getText().trim().isEmpty())) {
            appendLine("<Paragraph><Text></Text></Paragraph>");
        }
        
        indentLevel--;
        appendLine("</Cell>");
    }
    
    private void convertCellParagraph(XWPFParagraph para) {
        StringBuilder tag = new StringBuilder();
        tag.append("<Paragraph");
        
        // Alignment
        ParagraphAlignment align = para.getAlignment();
        if (align != null && align != ParagraphAlignment.LEFT) {
            tag.append(" align=\"").append(align.name()).append("\"");
        }
        
        tag.append(">");
        appendLine(tag.toString());
        indentLevel++;
        
        convertRuns(para.getRuns());
        
        indentLevel--;
        appendLine("</Paragraph>");
    }
    
    /**
     * Format number, removing trailing zeros after decimal
     */
    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        // Round to 2 decimal places and remove trailing zeros
        String formatted = String.format("%.2f", value);
        formatted = formatted.replaceAll("0+$", "");
        formatted = formatted.replaceAll("\\.$", "");
        return formatted;
    }
    
    private String escapeJsx(String text) {
        if (text == null) return "";
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("{", "&#123;")
            .replace("}", "&#125;");
    }
    
    private void appendLine(String line) {
        for (int i = 0; i < indentLevel; i++) {
            jsx.append(indentStr);
        }
        jsx.append(line).append("\n");
    }
    
    /**
     * CLI entry point for standalone usage.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java DocxToJsx <input.docx> [output.jsx]");
            System.out.println("  If output is not specified, prints to stdout");
            System.exit(1);
        }
        
        String inputPath = args[0];
        String outputPath = args.length > 1 ? args[1] : null;
        
        try {
            String jsx = convert(inputPath);
            
            if (outputPath != null) {
                Files.writeString(Path.of(outputPath), jsx);
                System.out.println("✓ Converted: " + inputPath + " -> " + outputPath);
            } else {
                System.out.println(jsx);
            }
        } catch (Exception e) {
            System.err.println("Error converting document: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
