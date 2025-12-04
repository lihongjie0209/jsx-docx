package cn.lihongjie.jsxdocx;

import cn.lihongjie.jsxdocx.model.VNode;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHint;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.math.BigInteger;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

// Chart imports
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.ss.util.CellRangeAddress;

// Watermark imports (VML)
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTFormulas;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTH;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.STExt;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.STConnectType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

// Comment imports
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;

public class Renderer {

    private XWPFDocument currentDocument;
    private Path basePath;
    private Map<String, Object> dataContext;
    private Set<Path> includeStack;
    private int totalSections;
    private int currentSectionIndex;
    private int nextCommentId = 0;  // Auto-incrementing comment ID counter

    private void renderStyles(XWPFDocument document, VNode stylesNode) {
        try {
            // Create styles document structure
            StylesDocument stylesDoc = StylesDocument.Factory.newInstance();
            CTStyles styles = stylesDoc.addNewStyles();
            
            // Add default document settings
            styles.addNewDocDefaults();
            
            // Process each Style child node
            if (stylesNode.getChildren() != null) {
                for (Object child : stylesNode.getChildren()) {
                    if (child instanceof VNode) {
                        VNode styleNode = (VNode) child;
                        if ("style".equals(styleNode.getType())) {
                            addStyleDefinition(styles, styleNode);
                        }
                    }
                }
            }
            
            // Create or get the styles part in the DOCX package
            PackagePartName stylesPartName = PackagingURIHelper.createPartName("/word/styles.xml");
            PackagePart stylesPart;
            
            if (document.getPackage().containPart(stylesPartName)) {
                stylesPart = document.getPackage().getPart(stylesPartName);
            } else {
                stylesPart = document.getPackage().createPart(stylesPartName, 
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml");
                // Add relationship from document to styles
                document.getPackagePart().addRelationship(stylesPartName, TargetMode.INTERNAL,
                    "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles");
            }
            
            // Write the styles to the part
            try (var out = stylesPart.getOutputStream()) {
                stylesDoc.save(out);
            }
            
        } catch (Exception e) {
            System.err.println("Error: Failed to render Styles component: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addStyleDefinition(CTStyles styles, VNode styleNode) {
        var style = styles.addNewStyle();
        
        // Required: styleId and name
        String styleId = String.valueOf(styleNode.getProps().get("styleId"));
        String name = String.valueOf(styleNode.getProps().get("name"));
        String type = String.valueOf(styleNode.getProps().get("type")); // paragraph, character, table
        
        if (styleId == null || "null".equals(styleId)) {
            System.err.println("Warning: Style missing required styleId property");
            return;
        }
        
        style.setStyleId(styleId);
        style.addNewName().setVal(name != null && !"null".equals(name) ? name : styleId);
        
        // Set style type (default to paragraph)
        if ("character".equals(type)) {
            style.setType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType.CHARACTER);
        } else if ("table".equals(type)) {
            style.setType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType.TABLE);
        } else {
            style.setType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType.PARAGRAPH);
        }
        
        // basedOn
        String basedOn = String.valueOf(styleNode.getProps().get("basedOn"));
        if (basedOn != null && !"null".equals(basedOn)) {
            style.addNewBasedOn().setVal(basedOn);
        }
        
        // Paragraph properties - use CTPPrGeneral for style definitions
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral pPr = null;
        
        // outlineLevel for headings
        Object outlineLevelObj = styleNode.getProps().get("outlineLevel");
        if (outlineLevelObj != null) {
            int outlineLevel = toInt(outlineLevelObj, -1);
            if (outlineLevel >= 0) {
                if (pPr == null) pPr = style.addNewPPr();
                pPr.addNewOutlineLvl().setVal(BigInteger.valueOf(outlineLevel));
            }
        }
        
        // keepNext
        if ("true".equals(String.valueOf(styleNode.getProps().get("keepNext")))) {
            if (pPr == null) pPr = style.addNewPPr();
            pPr.addNewKeepNext();
        }
        
        // keepLines
        if ("true".equals(String.valueOf(styleNode.getProps().get("keepLines")))) {
            if (pPr == null) pPr = style.addNewPPr();
            pPr.addNewKeepLines();
        }
        
        // spacing (before, after, line)
        Object spacingBefore = styleNode.getProps().get("spacingBefore");
        Object spacingAfter = styleNode.getProps().get("spacingAfter");
        Object lineSpacing = styleNode.getProps().get("lineSpacing");
        
        if (spacingBefore != null || spacingAfter != null || lineSpacing != null) {
            if (pPr == null) pPr = style.addNewPPr();
            var spacing = pPr.addNewSpacing();
            if (spacingBefore != null) spacing.setBefore(BigInteger.valueOf(toInt(spacingBefore, 0)));
            if (spacingAfter != null) spacing.setAfter(BigInteger.valueOf(toInt(spacingAfter, 0)));
            if (lineSpacing != null) {
                spacing.setLine(BigInteger.valueOf(toInt(lineSpacing, 240)));
                spacing.setLineRule(org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule.AUTO);
            }
        }
        
        // Run (text) properties
        CTRPr rPr = null;
        
        // bold
        if ("true".equals(String.valueOf(styleNode.getProps().get("bold")))) {
            if (rPr == null) rPr = style.addNewRPr();
            rPr.addNewB();
        }
        
        // italic
        if ("true".equals(String.valueOf(styleNode.getProps().get("italic")))) {
            if (rPr == null) rPr = style.addNewRPr();
            rPr.addNewI();
        }
        
        // fontSize (in half-points, e.g., 24 = 12pt)
        Object fontSize = styleNode.getProps().get("fontSize");
        if (fontSize != null) {
            if (rPr == null) rPr = style.addNewRPr();
            int sz = toInt(fontSize, 24);
            rPr.addNewSz().setVal(BigInteger.valueOf(sz));
            rPr.addNewSzCs().setVal(BigInteger.valueOf(sz));
        }
        
        // color
        String color = String.valueOf(styleNode.getProps().get("color"));
        if (color != null && !"null".equals(color)) {
            if (rPr == null) rPr = style.addNewRPr();
            String hexColor = color.startsWith("#") ? color.substring(1) : color;
            rPr.addNewColor().setVal(hexColor);
        }
        
        // fontFamily
        String fontFamily = String.valueOf(styleNode.getProps().get("fontFamily"));
        if (fontFamily != null && !"null".equals(fontFamily)) {
            if (rPr == null) rPr = style.addNewRPr();
            var fonts = rPr.addNewRFonts();
            fonts.setAscii(fontFamily);
            fonts.setHAnsi(fontFamily);
        }
    }

    public void renderToDocx(VNode vDom, String outputPath) throws IOException {
        renderToDocx(vDom, outputPath, null, null);
    }

    public void renderToDocx(VNode vDom, String outputPath, Path sourcePath, Map<String, Object> dataContext) throws IOException {
        this.basePath = sourcePath != null ? sourcePath.getParent() : Paths.get(".");
        this.dataContext = dataContext;
        this.includeStack = new HashSet<>();
        
        // Pre-scan to count sections
        this.totalSections = countSections(vDom);
        this.currentSectionIndex = 0;
        
        try (XWPFDocument document = new XWPFDocument()) {

            if (!"document".equals(vDom.getType())) {
                throw new IllegalArgumentException("Root element must be <Document>");
            }

            renderChildren(document, vDom);

            // -------------------------------------------------------
            // 核心代码：设置 "Update Fields On Open"
            // -------------------------------------------------------
            // 这会在 settings.xml 中添加 <w:updateFields w:val="true"/>
            document.enforceUpdateFields();

            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
        }
    }

    private void renderChildren(Object parent, VNode node) {
        if (node.getChildren() == null) return;
        for (Object child : node.getChildren()) {
            renderNode(parent, child);
        }
    }

    private void renderNode(Object parent, Object child) {
        if (child instanceof String) {
            if (parent instanceof XWPFParagraph) {
                XWPFRun run = ((XWPFParagraph) parent).createRun();
                run.setText((String) child);
            }
            return;
        }

        VNode node = (VNode) child;
        String type = node.getType();

        switch (type) {
            case "styles":
                // Styles should be rendered at document level
                if (parent instanceof XWPFDocument) {
                    renderStyles((XWPFDocument) parent, node);
                }
                break;
            case "document":
                break;
            case "header":
                if (parent instanceof XWPFDocument) {
                    XWPFDocument doc = (XWPFDocument) parent;
                    String t = node.getProps().get("type") != null ? String.valueOf(node.getProps().get("type")).toLowerCase() : "default";
                    try {
                        if ("even".equals(t) || "odd".equals(t)) {
                            doc.setEvenAndOddHeadings(true);
                        }
                        if ("first".equals(t)) {
                            // Ensure title page flag
                            CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                                    ? doc.getDocument().getBody().getSectPr()
                                    : doc.getDocument().getBody().addNewSectPr();
                            if (!sectPr.isSetTitlePg()) sectPr.addNewTitlePg();
                        }
                        HeaderFooterType hft = HeaderFooterType.DEFAULT;
                        if ("first".equals(t)) hft = HeaderFooterType.FIRST;
                        else if ("even".equals(t)) hft = HeaderFooterType.EVEN;
                        XWPFHeader h = doc.createHeader(hft);
                        if (h != null) { renderChildren(h, node); break; }
                    } catch (Throwable ignored) {}
                    try {
                        XWPFHeaderFooterPolicy policy = doc.getHeaderFooterPolicy();
                        if (policy == null) policy = doc.createHeaderFooterPolicy();
                        XWPFHeader header = null;
                        if ("first".equals(t)) header = policy.getFirstPageHeader();
                        else if ("even".equals(t)) header = policy.getEvenPageHeader();
                        else header = policy.getDefaultHeader();
                        if (header == null) {
                            if ("first".equals(t)) header = policy.createHeader(STHdrFtr.FIRST);
                            else if ("even".equals(t)) header = policy.createHeader(STHdrFtr.EVEN);
                            else header = policy.createHeader(STHdrFtr.DEFAULT);
                        }
                        if (header != null) renderChildren(header, node);
                    } catch (Throwable ignored) {}
                }
                break;
            case "footer":
                if (parent instanceof XWPFDocument) {
                    XWPFDocument doc = (XWPFDocument) parent;
                    String t = node.getProps().get("type") != null ? String.valueOf(node.getProps().get("type")).toLowerCase() : "default";
                    try {
                        if ("even".equals(t) || "odd".equals(t)) {
                            doc.setEvenAndOddHeadings(true);
                        }
                        if ("first".equals(t)) {
                            CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                                    ? doc.getDocument().getBody().getSectPr()
                                    : doc.getDocument().getBody().addNewSectPr();
                            if (!sectPr.isSetTitlePg()) sectPr.addNewTitlePg();
                        }
                        HeaderFooterType hft = HeaderFooterType.DEFAULT;
                        if ("first".equals(t)) hft = HeaderFooterType.FIRST;
                        else if ("even".equals(t)) hft = HeaderFooterType.EVEN;
                        XWPFFooter f = doc.createFooter(hft);
                        if (f != null) { renderChildren(f, node); break; }
                    } catch (Throwable ignored) {}
                    try {
                        XWPFHeaderFooterPolicy policy = doc.getHeaderFooterPolicy();
                        if (policy == null) policy = doc.createHeaderFooterPolicy();
                        XWPFFooter footer = null;
                        if ("first".equals(t)) footer = policy.getFirstPageFooter();
                        else if ("even".equals(t)) footer = policy.getEvenPageFooter();
                        else footer = policy.getDefaultFooter();
                        if (footer == null) {
                            if ("first".equals(t)) footer = policy.createFooter(STHdrFtr.FIRST);
                            else if ("even".equals(t)) footer = policy.createFooter(STHdrFtr.EVEN);
                            else footer = policy.createFooter(STHdrFtr.DEFAULT);
                        }
                        if (footer != null) renderChildren(footer, node);
                    } catch (Throwable ignored) {}
                }
                break;
            case "bulletedlist":
                if (parent instanceof XWPFDocument) {
                    String bulletChar = node.getProps().get("bulletChar") != null ? String.valueOf(node.getProps().get("bulletChar")) : "l";
                    String bulletFont = node.getProps().get("bulletFont") != null ? String.valueOf(node.getProps().get("bulletFont")) : "Wingdings";
                    int indentLeft = toInt(node.getProps().get("indentLeft"), 420);
                    int indentIncrement = toInt(node.getProps().get("indentIncrement"), 360);
                    int indentHanging = toInt(node.getProps().get("indentHanging"), 420);
                    BigInteger numId = createBulletNumbering((XWPFDocument) parent, bulletChar, bulletFont, indentLeft, indentIncrement, indentHanging);
                    for (Object c : node.getChildren()) {
                        if (c instanceof VNode && ((VNode) c).getType().equals("listitem")) {
                            XWPFParagraph p = ((XWPFDocument) parent).createParagraph();
                            int level = toInt(((VNode) c).getProps().get("level"), 0);
                            p.setNumID(numId);
                            p.setNumILvl(BigInteger.valueOf(Math.max(0, Math.min(8, level))));
                            renderChildren(p, (VNode) c);
                        }
                    }
                }
                break;
            case "numberedlist":
                if (parent instanceof XWPFDocument) {
                    Integer start = null;
                    Object startObj = node.getProps().get("start");
                    if (startObj instanceof Number) start = ((Number) startObj).intValue();
                    String fmt = node.getProps().get("format") != null ? String.valueOf(node.getProps().get("format")).toLowerCase() : "decimal";
                    Object levelConfig = node.getProps().get("levelConfig");
                    BigInteger numId = createFormattedNumbering((XWPFDocument) parent, fmt, start, levelConfig);
                    for (Object c : node.getChildren()) {
                        if (c instanceof VNode && ((VNode) c).getType().equals("listitem")) {
                            XWPFParagraph p = ((XWPFDocument) parent).createParagraph();
                            int level = toInt(((VNode) c).getProps().get("level"), 0);
                            p.setNumID(numId);
                            p.setNumILvl(BigInteger.valueOf(Math.max(0, Math.min(8, level))));
                            renderChildren(p, (VNode) c);
                        }
                    }
                }
                break;
            case "section":
                // For sections, we need to render children first, then apply section properties
                if (parent instanceof XWPFDocument) {
                    XWPFDocument doc = (XWPFDocument) parent;
                    int paraCountBefore = doc.getParagraphs().size();
                    
                    // Increment section counter
                    currentSectionIndex++;
                    
                    // Render all children of this section
                    renderChildren(parent, node);
                    
                    // Apply section properties to the last paragraph in this section
                    // If no paragraphs were created, create one
                    if (doc.getParagraphs().size() == paraCountBefore) {
                        doc.createParagraph();
                    }
                    
                    // Determine if this is the last section
                    boolean isLastSection = (currentSectionIndex >= totalSections);
                    
                    // Apply section properties
                    if (isLastSection) {
                        // Last section: use document-level sectPr for compatibility
                        applySectionPropsToDocument(doc, node);
                    } else {
                        // Not last section: use paragraph-level sectPr for section break
                        applySectionPropsToLastParagraph(doc, node);
                    }
                } else {
                    renderChildren(parent, node);
                }
                break;
            case "paragraph":
                if (parent instanceof XWPFParagraph) {
                    // If parent is already a paragraph (e.g., from ListItem), render children directly
                    applyParagraphProps((XWPFParagraph) parent, node);
                    renderChildren(parent, node);
                } else if (parent instanceof XWPFDocument) {
                    XWPFParagraph p = ((XWPFDocument) parent).createParagraph();
                    applyParagraphProps(p, node);
                    renderChildren(p, node);
                } else if (parent instanceof XWPFHeader) {
                    XWPFParagraph p = ((XWPFHeader) parent).createParagraph();
                    applyParagraphProps(p, node);
                    renderChildren(p, node);
                } else if (parent instanceof XWPFFooter) {
                    XWPFParagraph p = ((XWPFFooter) parent).createParagraph();
                    applyParagraphProps(p, node);
                    renderChildren(p, node);
                } else if (parent instanceof XWPFTableCell) {
                    XWPFParagraph p = ((XWPFTableCell) parent).addParagraph();
                    applyParagraphProps(p, node);
                    renderChildren(p, node);
                }
                break;
            case "heading":
                if (parent instanceof XWPFDocument) {
                    XWPFParagraph p = ((XWPFDocument) parent).createParagraph();
                    
                    // Use styleId from props to reference user-defined styles
                    String styleId = String.valueOf(node.getProps().get("styleId"));
                    if (styleId != null && !styleId.equals("null")) {
                        p.setStyle(styleId);
                    }
                    
                    renderChildren(p, node);
                }
                break;
            case "text":
                if (parent instanceof XWPFParagraph) {
                    XWPFRun run = ((XWPFParagraph) parent).createRun();
                    applyTextProps(run, node);
                    for (Object c : node.getChildren()) {
                        if (c instanceof String) {
                            run.setText((String) c);
                        }
                    }
                }
                break;
            case "br":
                if (parent instanceof XWPFParagraph) {
                    XWPFRun run = ((XWPFParagraph) parent).createRun();
                    try { run.addBreak(); } catch (Exception ignored) {}
                }
                break;
            case "tab":
                if (parent instanceof XWPFParagraph) {
                    XWPFRun run = ((XWPFParagraph) parent).createRun();
                    try { run.addTab(); } catch (Exception ignored) {}
                }
                break;
            case "pagenumber":
                if (parent instanceof XWPFParagraph) {
                    try {
                        XWPFParagraph p = (XWPFParagraph) parent;
                        p.getCTP().addNewFldSimple().setInstr(" PAGE ");
                    } catch (Exception ignored) {}
                }
                break;
            case "link":
                if (parent instanceof XWPFParagraph) {
                    String href = String.valueOf(node.getProps().get("href"));
                    if (href != null && !"null".equals(href)) {
                        XWPFHyperlinkRun hr = ((XWPFParagraph) parent).createHyperlinkRun(href);
                        hr.setUnderline(UnderlinePatterns.SINGLE);
                        hr.setColor("0000FF");
                        // Concatenate text children
                        StringBuilder sb = new StringBuilder();
                        for (Object c : node.getChildren()) {
                            if (c instanceof String) sb.append((String) c);
                        }
                        hr.setText(sb.toString());
                    } else {
                        // Fallback to plain text
                        for (Object c : node.getChildren()) {
                            if (c instanceof String) {
                                XWPFRun run = ((XWPFParagraph) parent).createRun();
                                run.setText((String) c);
                            }
                        }
                    }
                }
                break;
            case "image":
                if (parent instanceof XWPFParagraph) {
                    String src = String.valueOf(node.getProps().get("src"));
                    Integer widthProp = (node.getProps().get("width") instanceof Number) ? ((Number) node.getProps().get("width")).intValue() : null;
                    Integer heightProp = (node.getProps().get("height") instanceof Number) ? ((Number) node.getProps().get("height")).intValue() : null;
                    String fit = node.getProps().get("fit") != null ? String.valueOf(node.getProps().get("fit")).toLowerCase() : null;
                    Integer maxW = (node.getProps().get("maxWidth") instanceof Number) ? ((Number) node.getProps().get("maxWidth")).intValue() : null;
                    Integer maxH = (node.getProps().get("maxHeight") instanceof Number) ? ((Number) node.getProps().get("maxHeight")).intValue() : null;
                    byte[] bytes = readImageBytes(src);
                    if (bytes != null) {
                        try {
                            XWPFRun run = ((XWPFParagraph) parent).createRun();
                            int pictureType = guessPictureType(src, bytes);
                            int targetW;
                            int targetH;
                            BufferedImage img = null;
                            try { img = ImageIO.read(new ByteArrayInputStream(bytes)); } catch (Exception ignored) {}
                            if (img != null && fit != null && (maxW != null || maxH != null)) {
                                int iw = img.getWidth();
                                int ih = img.getHeight();
                                double scaleW = (maxW != null) ? (maxW / (double) Math.max(1, iw)) : Double.POSITIVE_INFINITY;
                                double scaleH = (maxH != null) ? (maxH / (double) Math.max(1, ih)) : Double.POSITIVE_INFINITY;
                                if ("contain".equals(fit)) {
                                    double scale = Math.min(scaleW, scaleH);
                                    if (Double.isInfinite(scale)) {
                                        // Only one bound set
                                        scale = Double.isInfinite(scaleW) ? scaleH : scaleW;
                                    }
                                    if (Double.isInfinite(scale) || scale <= 0) {
                                        targetW = (widthProp != null ? widthProp : iw);
                                        targetH = (heightProp != null ? heightProp : ih);
                                    } else {
                                        targetW = (int) Math.round(iw * scale);
                                        targetH = (int) Math.round(ih * scale);
                                    }
                                } else if ("scaledown".equals(fit)) {
                                    boolean larger = (maxW != null && iw > maxW) || (maxH != null && ih > maxH);
                                    if (larger) {
                                        double scale = Math.min(scaleW, scaleH);
                                        if (Double.isInfinite(scale)) scale = Double.isInfinite(scaleW) ? scaleH : scaleW;
                                        targetW = (int) Math.round(iw * scale);
                                        targetH = (int) Math.round(ih * scale);
                                    } else {
                                        targetW = (widthProp != null ? widthProp : iw);
                                        targetH = (heightProp != null ? heightProp : ih);
                                    }
                                } else {
                                    targetW = (widthProp != null ? widthProp : (img != null ? img.getWidth() : 100));
                                    targetH = (heightProp != null ? heightProp : (img != null ? img.getHeight() : 100));
                                }
                            } else {
                                targetW = (widthProp != null ? widthProp : (img != null ? img.getWidth() : 100));
                                targetH = (heightProp != null ? heightProp : (img != null ? img.getHeight() : 100));
                            }
                            run.addPicture(new ByteArrayInputStream(bytes), pictureType, "image", Units.toEMU(targetW), Units.toEMU(targetH));
                        } catch (Exception ignored) {}
                    }
                }
                break;
            case "table":
                if (parent instanceof XWPFDocument) {
                    XWPFTable table = ((XWPFDocument) parent).createTable();
                    table.removeRow(0);
                    applyTableProps(table, node);
                    TableContext ctx = new TableContext();
                    // Render rows with merge context
                    if (node.getChildren() != null) {
                        for (Object rc : node.getChildren()) {
                            if (rc instanceof VNode && ((VNode) rc).getType().equals("row")) {
                                renderTableRow(table, (VNode) rc, ctx);
                            }
                        }
                    }
                }
                break;
            case "row":
                // Handled in table context rendering
                break;
            case "cell":
                if (parent instanceof XWPFTableRow) {
                    XWPFTableCell cell = ((XWPFTableRow) parent).createCell();
                    applyCellProps(cell, node);
                    renderChildren(cell, node);
                }
                break;
            case "pagebreak":
                if (parent instanceof XWPFDocument) {
                    XWPFParagraph p = ((XWPFDocument) parent).createParagraph();
                    p.setPageBreak(true);
                }
                break;
            case "toc":
                if (parent instanceof XWPFDocument) {
                    renderToc((XWPFDocument) parent, node);
                }
                break;
            case "include":
                renderInclude(parent, node);
                break;
            case "chart":
                // Find the document to add chart to
                XWPFDocument chartDoc = findDocument(parent);
                if (chartDoc != null) {
                    renderChart(chartDoc, node);
                }
                break;
            case "watermark":
                // Find the document to add watermark to
                XWPFDocument watermarkDoc = findDocument(parent);
                if (watermarkDoc != null) {
                    renderWatermark(watermarkDoc, node);
                }
                break;
            case "comment":
                // Comment wraps text content with annotation
                if (parent instanceof XWPFParagraph) {
                    renderComment((XWPFParagraph) parent, node);
                }
                break;
            case "footnote":
                // Footnote adds a reference mark and content at page bottom
                if (parent instanceof XWPFParagraph) {
                    renderFootnote((XWPFParagraph) parent, node);
                }
                break;
            case "endnote":
                // Endnote adds a reference mark and content at document end
                if (parent instanceof XWPFParagraph) {
                    renderEndnote((XWPFParagraph) parent, node);
                }
                break;
            default:
                // Handle Fragment and other unknown types by rendering children
                if (type != null && (type.contains("Fragment") || type.contains("Symbol"))) {
                    // Fragment: transparently render children
                    renderChildren(parent, node);
                } else {
                    System.err.println("Unknown component type: " + type);
                }
        }
    }
    
    private XWPFDocument findDocument(Object obj) {
        if (obj instanceof XWPFDocument) {
            return (XWPFDocument) obj;
        } else if (obj instanceof XWPFParagraph) {
            return ((XWPFParagraph) obj).getDocument();
        } else if (obj instanceof XWPFTableCell) {
            return ((XWPFTableCell) obj).getXWPFDocument();
        } else if (obj instanceof XWPFTable) {
            return ((XWPFTable) obj).getBody().getXWPFDocument();
        } else if (obj instanceof XWPFHeader) {
            return ((XWPFHeader) obj).getXWPFDocument();
        } else if (obj instanceof XWPFFooter) {
            return ((XWPFFooter) obj).getXWPFDocument();
        }
        return null;
    }

    private void renderInclude(Object parent, VNode node) {
        String pathStr = String.valueOf(node.getProps().get("path"));
        if (pathStr == null || "null".equals(pathStr) || pathStr.isEmpty()) {
            System.err.println("Error: <Include> component requires 'path' property");
            return;
        }

        try {
            // Resolve relative path
            Path includePath = basePath.resolve(pathStr).normalize().toAbsolutePath();
            
            // Check for circular includes
            if (includeStack.contains(includePath)) {
                StringBuilder chain = new StringBuilder();
                for (Path p : includeStack) {
                    if (chain.length() > 0) chain.append(" → ");
                    chain.append(p.getFileName());
                }
                chain.append(" → ").append(includePath.getFileName());
                throw new IllegalStateException("Circular include detected: " + chain);
            }
            
            // Read file
            if (!java.nio.file.Files.exists(includePath)) {
                throw new IOException("Include file not found: " + includePath);
            }
            
            String jsxContent = java.nio.file.Files.readString(includePath);
            
            // Compile JSX
            Compiler compiler = new Compiler();
            String jsCode = compiler.compile(jsxContent);
            
            // Execute with same data context
            JsRuntime runtime = new JsRuntime();
            VNode includedVNode = runtime.run(jsCode, dataContext);
            
            // Save current state and update for nested includes
            Path savedBasePath = this.basePath;
            this.basePath = includePath.getParent();
            includeStack.add(includePath);
            
            try {
                // Render included content's children (skip root Document node if present)
                if ("document".equals(includedVNode.getType())) {
                    renderChildren(parent, includedVNode);
                } else {
                    renderNode(parent, includedVNode);
                }
            } finally {
                // Restore state
                includeStack.remove(includePath);
                this.basePath = savedBasePath;
            }
            
        } catch (IllegalStateException e) {
            // Re-throw circular dependency errors immediately
            System.err.println("Error processing <Include path='" + pathStr + "'>: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // Other errors (file not found, compile errors) - print but continue
            System.err.println("Error processing <Include path='" + pathStr + "'>: " + e.getMessage());
        }
    }

    private void renderToc(XWPFDocument document, VNode node) {
        // Extract TOC properties
        String title = String.valueOf(node.getProps().get("title"));
        if (title.equals("null")) {
            title = "目录";
        }
        int maxLevel = toInt(node.getProps().get("maxLevel"), 3);
        if (maxLevel < 1) maxLevel = 1;
        if (maxLevel > 9) maxLevel = 9;
        
        boolean hyperlink = !"false".equals(String.valueOf(node.getProps().get("hyperlink")));
        boolean showPageNumbers = !"false".equals(String.valueOf(node.getProps().get("showPageNumbers")));
        
        // Create title paragraph if provided
        if (!title.isEmpty()) {
            XWPFParagraph titlePara = document.createParagraph();
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(16);
        }
        
        // Create TOC field paragraph
        XWPFParagraph tocPara = document.createParagraph();
        
        // Build TOC field instruction
        StringBuilder instrText = new StringBuilder("TOC \\o \"1-" + maxLevel + "\"");
        if (hyperlink) {
            instrText.append(" \\h");
        }
        instrText.append(" \\z \\u");
        if (!showPageNumbers) {
            instrText.append(" \\n");
        }
        
        // Create field structure using CTR (low-level XML)
        // 1. Begin field
        CTR run1 = tocPara.getCTP().addNewR();
        run1.addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        
        // 2. Field instruction
        CTR run2 = tocPara.getCTP().addNewR();
        run2.addNewInstrText().setStringValue(instrText.toString());
        
        // 3. Separate
        CTR run3 = tocPara.getCTP().addNewR();
        run3.addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        
        // 4. Placeholder text (will be replaced when field is updated in Word)
        CTR run4 = tocPara.getCTP().addNewR();
        run4.addNewT().setStringValue("右键点击此处选择\"更新域\"以生成目录");
        
        // 5. End field
        CTR run5 = tocPara.getCTP().addNewR();
        run5.addNewFldChar().setFldCharType(STFldCharType.END);
    }

    /**
     * Apply section properties to the last paragraph in the document.
     * This creates a new section with its own properties.
     */
    private void applySectionPropsToLastParagraph(XWPFDocument doc, VNode node) {
        if (doc.getParagraphs().isEmpty()) {
            return;
        }
        
        // Get the last paragraph and add section properties to it
        XWPFParagraph lastPara = doc.getParagraphs().get(doc.getParagraphs().size() - 1);
        CTPPr pPr = lastPara.getCTP().isSetPPr() ? lastPara.getCTP().getPPr() : lastPara.getCTP().addNewPPr();
        CTSectPr sectPr = pPr.isSetSectPr() ? pPr.getSectPr() : pPr.addNewSectPr();

        applySectionPropsToSectPr(sectPr, node);
    }

    /**
     * Apply section properties to the document-level sectPr.
     * This is used for the last section or when there's only one section.
     */
    private void applySectionPropsToDocument(XWPFDocument doc, VNode node) {
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        applySectionPropsToSectPr(sectPr, node);
    }

    /**
     * Apply section properties to a CTSectPr object.
     */
    private void applySectionPropsToSectPr(CTSectPr sectPr, VNode node) {
        // Get or create page size object once
        CTPageSz pgSz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();

        // Set page size dimensions
        Object size = node.getProps().get("pageSize");
        if (size != null) {
            String s = String.valueOf(size).toUpperCase();
            if ("A4".equals(s)) {
                pgSz.setW(BigInteger.valueOf(11900));
                pgSz.setH(BigInteger.valueOf(16840));
            } else if ("LETTER".equals(s)) {
                pgSz.setW(BigInteger.valueOf(12240));
                pgSz.setH(BigInteger.valueOf(15840));
            }
        }

        // Set orientation (if specified)
        Object orientation = node.getProps().get("orientation");
        if (orientation != null) {
            String o = String.valueOf(orientation).toLowerCase();
            try {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.Enum val =
                        "landscape".equals(o)
                                ? org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.LANDSCAPE
                                : org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.PORTRAIT;
                pgSz.setOrient(val);
                
                // Swap width and height for landscape orientation
                if ("landscape".equals(o) && pgSz.isSetW() && pgSz.isSetH()) {
                    BigInteger w = BigInteger.valueOf(Long.parseLong(pgSz.getW().toString()));
                    BigInteger h = BigInteger.valueOf(Long.parseLong(pgSz.getH().toString()));
                    pgSz.setW(h);
                    pgSz.setH(w);
                }
            } catch (Throwable ignored) {}
        }

        Object margins = node.getProps().get("margins");
        if (margins instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> m = (java.util.Map<String, Object>) margins;
            CTPageMar pgMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
            Integer top = toTwipsFromInches(m.get("top"));
            Integer bottom = toTwipsFromInches(m.get("bottom"));
            Integer left = toTwipsFromInches(m.get("left"));
            Integer right = toTwipsFromInches(m.get("right"));
            if (top != null) pgMar.setTop(BigInteger.valueOf(top));
            if (bottom != null) pgMar.setBottom(BigInteger.valueOf(bottom));
            if (left != null) pgMar.setLeft(BigInteger.valueOf(left));
            if (right != null) pgMar.setRight(BigInteger.valueOf(right));
        }
    }

    /**
     * Count the total number of section nodes in the VNode tree.
     */
    private int countSections(VNode node) {
        int count = 0;
        if ("section".equals(node.getType())) {
            count = 1;
        }
        if (node.getChildren() != null) {
            for (Object child : node.getChildren()) {
                if (child instanceof VNode) {
                    count += countSections((VNode) child);
                }
            }
        }
        return count;
    }

    private void applyParagraphProps(XWPFParagraph p, VNode node) {
        // Apply style reference if provided
        String styleId = String.valueOf(node.getProps().get("styleId"));
        if (styleId != null && !styleId.equals("null")) {
            p.setStyle(styleId);
        }
        
        Object alignObj = node.getProps().get("align");
        if (alignObj != null) {
            String align = String.valueOf(alignObj).toUpperCase();
            try {
                p.setAlignment(ParagraphAlignment.valueOf(align));
            } catch (Exception ignored) {}
        }

        Object beforeObj = node.getProps().get("before");
        if (beforeObj != null) {
            int twips = toTwipsFromPt(beforeObj, -1);
            if (twips >= 0) p.setSpacingBefore(twips);
        }

        Object afterObj = node.getProps().get("after");
        if (afterObj != null) {
            int twips = toTwipsFromPt(afterObj, -1);
            if (twips >= 0) p.setSpacingAfter(twips);
        }

        Object lineObj = node.getProps().get("line");
        if (lineObj != null) {
            double line = toDouble(lineObj, -1);
            if (line > 0) p.setSpacingBetween(line, LineSpacingRule.AUTO);
        }

        Object indentLeft = node.getProps().get("indentLeft");
        if (indentLeft != null) {
            int twips = toTwipsFromPt(indentLeft, -1);
            if (twips >= 0) p.setIndentationLeft(twips);
        }

        Object indentRight = node.getProps().get("indentRight");
        if (indentRight != null) {
            int twips = toTwipsFromPt(indentRight, -1);
            if (twips >= 0) p.setIndentationRight(twips);
        }

        Object firstLine = node.getProps().get("firstLine");
        if (firstLine != null) {
            int twips = toTwipsFromPt(firstLine, -1);
            if (twips >= 0) p.setIndentationFirstLine(twips);
        }

        // Keep options
        Object keepNext = node.getProps().get("keepWithNext");
        if (keepNext instanceof Boolean && (Boolean) keepNext) {
            if (!p.getCTP().isSetPPr()) p.getCTP().addNewPPr();
            p.getCTP().getPPr().addNewKeepNext();
        }
        Object keepLines = node.getProps().get("keepLines");
        if (keepLines instanceof Boolean && (Boolean) keepLines) {
            if (!p.getCTP().isSetPPr()) p.getCTP().addNewPPr();
            p.getCTP().getPPr().addNewKeepLines();
        }

        Object background = node.getProps().get("background");
        if (background != null) {
            String color = String.valueOf(background).replace("#", "").trim();
            if (!color.isEmpty()) {
                if (!p.getCTP().isSetPPr()) p.getCTP().addNewPPr();
                if (!p.getCTP().getPPr().isSetShd()) p.getCTP().getPPr().addNewShd();
                p.getCTP().getPPr().getShd().setFill(color);
            }
        }

        Object border = node.getProps().get("border");
        if (border != null) {
            if (!p.getCTP().isSetPPr()) p.getCTP().addNewPPr();
            CTPBdr pb = p.getCTP().getPPr().isSetPBdr() ? p.getCTP().getPPr().getPBdr() : p.getCTP().getPPr().addNewPBdr();
            if (border instanceof Boolean) {
                if (!((Boolean) border)) {
                    setBorder(pb.isSetTop() ? pb.getTop() : pb.addNewTop(), STBorder.NONE, 0, null);
                    setBorder(pb.isSetBottom() ? pb.getBottom() : pb.addNewBottom(), STBorder.NONE, 0, null);
                    setBorder(pb.isSetLeft() ? pb.getLeft() : pb.addNewLeft(), STBorder.NONE, 0, null);
                    setBorder(pb.isSetRight() ? pb.getRight() : pb.addNewRight(), STBorder.NONE, 0, null);
                }
            } else if (border instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> b = (java.util.Map<String, Object>) border;
                int sz = 8;
                Object sizePt = b.get("size");
                if (sizePt instanceof Number) sz = (int) Math.round(((Number) sizePt).doubleValue() * 8.0);
                String colorHex = null;
                Object colorObj = b.get("color");
                if (colorObj != null) colorHex = String.valueOf(colorObj).replace("#", "");
                java.util.Set<String> sides = null;
                Object sidesObj = b.get("sides");
                if (sidesObj instanceof java.util.List) {
                    sides = new java.util.HashSet<>();
                    for (Object s : (java.util.List<?>) sidesObj) {
                        if (s != null) sides.add(String.valueOf(s).toLowerCase());
                    }
                }
                boolean all = (sides == null || sides.isEmpty());
                if (all || sides.contains("top")) setBorder(pb.isSetTop() ? pb.getTop() : pb.addNewTop(), STBorder.SINGLE, sz, colorHex);
                if (all || sides.contains("bottom")) setBorder(pb.isSetBottom() ? pb.getBottom() : pb.addNewBottom(), STBorder.SINGLE, sz, colorHex);
                if (all || sides.contains("left")) setBorder(pb.isSetLeft() ? pb.getLeft() : pb.addNewLeft(), STBorder.SINGLE, sz, colorHex);
                if (all || sides.contains("right")) setBorder(pb.isSetRight() ? pb.getRight() : pb.addNewRight(), STBorder.SINGLE, sz, colorHex);
            }
        }

        // Tab stops
        Object tabStops = node.getProps().get("tabStops");
        if (tabStops instanceof java.util.List) {
            @SuppressWarnings("unchecked")
            java.util.List<Object> stops = (java.util.List<Object>) tabStops;
            if (!p.getCTP().isSetPPr()) p.getCTP().addNewPPr();
            CTTabs tabs = p.getCTP().getPPr().isSetTabs() ? p.getCTP().getPPr().getTabs() : p.getCTP().getPPr().addNewTabs();
            for (Object s : stops) {
                if (!(s instanceof java.util.Map)) continue;
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> st = (java.util.Map<String, Object>) s;
                CTTabStop t = tabs.addNewTab();
                String align = String.valueOf(st.getOrDefault("align", "left")).toLowerCase();
                if ("center".equals(align)) t.setVal(STTabJc.CENTER);
                else if ("right".equals(align)) t.setVal(STTabJc.RIGHT);
                else if ("decimal".equals(align)) t.setVal(STTabJc.DECIMAL);
                else if ("bar".equals(align)) t.setVal(STTabJc.BAR);
                else t.setVal(STTabJc.LEFT);
                Object posObj = st.get("pos");
                int posTwips = toTwipsFromPt(posObj, -1);
                if (posTwips >= 0) t.setPos(BigInteger.valueOf(posTwips));
                String leader = String.valueOf(st.getOrDefault("leader", "none")).toLowerCase();
                if ("dots".equals(leader) || "dot".equals(leader)) t.setLeader(STTabTlc.DOT);
                else if ("dashes".equals(leader) || "dash".equals(leader) || "hyphen".equals(leader)) t.setLeader(STTabTlc.HYPHEN);
                else if ("underline".equals(leader)) t.setLeader(STTabTlc.UNDERSCORE);
                else if ("heavy".equals(leader)) t.setLeader(STTabTlc.HEAVY);
                else if ("middledot".equals(leader) || "middleDot".equals(leader)) t.setLeader(STTabTlc.MIDDLE_DOT);
                // else default none
            }
        }
    }

    private void applyTextProps(XWPFRun run, VNode node) {
        // Apply character style reference if provided
        String styleId = String.valueOf(node.getProps().get("styleId"));
        if (styleId != null && !styleId.equals("null")) {
            CTRPr rPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
            rPr.addNewRStyle().setVal(styleId);
        }
        
        Object bold = node.getProps().get("bold");
        if (bold instanceof Boolean && (Boolean) bold) run.setBold(true);
        Object italic = node.getProps().get("italic");
        if (italic instanceof Boolean && (Boolean) italic) run.setItalic(true);
        Object size = node.getProps().get("size");
        if (size instanceof Number) run.setFontSize(((Number) size).intValue());
        Object color = node.getProps().get("color");
        if (color != null) run.setColor(String.valueOf(color).replace("#", ""));
        Object underline = node.getProps().get("underline");
        if (underline != null) {
            UnderlinePatterns pattern = UnderlinePatterns.SINGLE;
            if (underline instanceof Boolean) {
                if (!((Boolean) underline)) pattern = UnderlinePatterns.NONE;
            } else {
                String u = String.valueOf(underline).toLowerCase();
                switch (u) {
                    case "double": pattern = UnderlinePatterns.DOUBLE; break;
                    case "dotted": pattern = UnderlinePatterns.DOTTED; break;
                    case "dash": pattern = UnderlinePatterns.DASH; break;
                    case "none": pattern = UnderlinePatterns.NONE; break;
                    default: pattern = UnderlinePatterns.SINGLE; break;
                }
            }
            run.setUnderline(pattern);
        }
        Object strike = node.getProps().get("strike");
        if (strike instanceof Boolean && (Boolean) strike) run.setStrikeThrough(true);
        Object highlight = node.getProps().get("highlight");
        if (highlight != null) {
            try { run.setTextHighlightColor(String.valueOf(highlight)); } catch (Exception ignored) {}
        }
        Object font = node.getProps().get("font");
        if (font != null) {
            try { run.setFontFamily(String.valueOf(font)); } catch (Exception ignored) {}
        }
    }

    private int toInt(Object obj, int def) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(String.valueOf(obj)); } catch (Exception e) { return def; }
    }

    private Integer toTwipsFromInches(Object obj) {
        if (obj == null) return null;
        try {
            double inches = (obj instanceof Number) ? ((Number) obj).doubleValue() : Double.parseDouble(String.valueOf(obj));
            return (int) Math.round(inches * 1440.0);
        } catch (Exception e) {
            return null;
        }
    }

    private int toTwipsFromPt(Object obj, int def) {
        if (obj instanceof Number) {
            double pt = ((Number) obj).doubleValue();
            return (int) Math.round(pt * 20.0);
        }
        try {
            double pt = Double.parseDouble(String.valueOf(obj));
            return (int) Math.round(pt * 20.0);
        } catch (Exception e) { return def; }
    }

    private double toDouble(Object obj, double def) {
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try { return Double.parseDouble(String.valueOf(obj)); } catch (Exception e) { return def; }
    }

    private void applyTableProps(XWPFTable table, VNode node) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();

        // Apply table style reference if provided
        String styleId = String.valueOf(node.getProps().get("styleId"));
        if (styleId != null && !styleId.equals("null")) {
            tblPr.addNewTblStyle().setVal(styleId);
        }

        Object border = node.getProps().get("border");
        if (border != null) {
            CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
            if (border instanceof Boolean) {
                if (!(Boolean) border) {
                    setAllBorders(borders, STBorder.NONE, 0, null);
                }
            } else if (border instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> b = (java.util.Map<String, Object>) border;
                int sz = 8; // 1pt default (1pt = 8)
                Object sizePt = b.get("size");
                if (sizePt instanceof Number) sz = (int) Math.round(((Number) sizePt).doubleValue() * 8.0);
                String color = null;
                Object colorObj = b.get("color");
                if (colorObj != null) color = String.valueOf(colorObj).replace("#", "");
                setAllBorders(borders, STBorder.SINGLE, sz, color);
            }
        }

        Object width = node.getProps().get("width");
        if (width != null) {
            CTTblWidth w = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
            String s = String.valueOf(width).trim();
            if (s.endsWith("%")) {
                try {
                    double pct = Double.parseDouble(s.substring(0, s.length() - 1));
                    long wVal = Math.round(pct * 50.0); // PCT uses 1/50 percent
                    w.setType(STTblWidth.PCT);
                    w.setW(BigInteger.valueOf(wVal));
                } catch (Exception ignored) {}
            }
        }

        Object align = node.getProps().get("align");
        if (align != null) {
            String a = String.valueOf(align).toUpperCase();
            CTJcTable jc = tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc();
            if ("CENTER".equals(a)) jc.setVal(STJcTable.CENTER);
            else if ("RIGHT".equals(a)) jc.setVal(STJcTable.RIGHT);
            else jc.setVal(STJcTable.LEFT);
        }

        Object layout = node.getProps().get("layout");
        if (layout != null) {
            String l = String.valueOf(layout).toLowerCase();
            CTTblLayoutType tl = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
            if ("fixed".equals(l)) tl.setType(STTblLayoutType.FIXED);
        }

        Object columns = node.getProps().get("columns");
        if (columns instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> cols = (List<Object>) columns;
            CTTblGrid grid = table.getCTTbl().getTblGrid();
            if (grid == null) grid = table.getCTTbl().addNewTblGrid();
            for (Object cw : cols) {
                int twips = toTwipsFromPt(cw, -1);
                if (twips > 0) {
                    grid.addNewGridCol().setW(BigInteger.valueOf(twips));
                }
            }
            // If columns specified but layout not fixed, default to fixed
            if (layout == null) {
                CTTblLayoutType tl = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
                tl.setType(STTblLayoutType.FIXED);
            }
        }
    }

    private void setAllBorders(CTTblBorders borders, STBorder.Enum style, int sz, String color) {
        setBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop(), style, sz, color);
        setBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom(), style, sz, color);
        setBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft(), style, sz, color);
        setBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight(), style, sz, color);
        setBorder(borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH(), style, sz, color);
        setBorder(borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV(), style, sz, color);
    }

    private void setBorder(CTBorder b, STBorder.Enum style, int sz, String color) {
        b.setVal(style);
        b.setSz(BigInteger.valueOf(sz));
        if (color != null) b.setColor(color);
    }

    private void applyCellProps(XWPFTableCell cell, VNode node) {
        CTTcPr tcPr = cell.getCTTc().getTcPr();
        if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();

        // Apply cell paragraph style reference if provided
        String styleId = String.valueOf(node.getProps().get("styleId"));
        if (styleId != null && !styleId.equals("null") && cell.getParagraphs().size() > 0) {
            cell.getParagraphs().get(0).setStyle(styleId);
        }

        Object vAlign = node.getProps().get("vAlign");
        if (vAlign != null) {
            String v = String.valueOf(vAlign).toUpperCase();
            CTVerticalJc jc = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr.addNewVAlign();
            if ("CENTER".equals(v)) jc.setVal(STVerticalJc.CENTER);
            else if ("BOTTOM".equals(v)) jc.setVal(STVerticalJc.BOTTOM);
            else jc.setVal(STVerticalJc.TOP);
        }

        Object padding = node.getProps().get("padding");
        if (padding instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> m = (java.util.Map<String, Object>) padding;
            CTTcMar mar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();
            Integer top = toTwipsFromPt(m.get("top"), -1);
            Integer bottom = toTwipsFromPt(m.get("bottom"), -1);
            Integer left = toTwipsFromPt(m.get("left"), -1);
            Integer right = toTwipsFromPt(m.get("right"), -1);
            if (top != null && top >= 0) {
                CTTblWidth w = mar.isSetTop() ? mar.getTop() : mar.addNewTop();
                w.setType(STTblWidth.DXA);
                w.setW(BigInteger.valueOf(top));
            }
            if (bottom != null && bottom >= 0) {
                CTTblWidth w = mar.isSetBottom() ? mar.getBottom() : mar.addNewBottom();
                w.setType(STTblWidth.DXA);
                w.setW(BigInteger.valueOf(bottom));
            }
            if (left != null && left >= 0) {
                CTTblWidth w = mar.isSetLeft() ? mar.getLeft() : mar.addNewLeft();
                w.setType(STTblWidth.DXA);
                w.setW(BigInteger.valueOf(left));
            }
            if (right != null && right >= 0) {
                CTTblWidth w = mar.isSetRight() ? mar.getRight() : mar.addNewRight();
                w.setType(STTblWidth.DXA);
                w.setW(BigInteger.valueOf(right));
            }
        }

        Object width = node.getProps().get("width");
        if (width != null) {
            if (tcPr.getTcW() == null) tcPr.addNewTcW();
            String s = String.valueOf(width).trim();
            if (s.endsWith("%")) {
                try {
                    double pct = Double.parseDouble(s.substring(0, s.length() - 1));
                    long wVal = Math.round(pct * 50.0); // PCT 1/50 percent
                    tcPr.getTcW().setType(STTblWidth.PCT);
                    tcPr.getTcW().setW(BigInteger.valueOf(wVal));
                } catch (Exception ignored) {}
            } else {
                int twips = toTwipsFromPt(width, -1);
                if (twips >= 0) {
                    tcPr.getTcW().setType(STTblWidth.DXA);
                    tcPr.getTcW().setW(BigInteger.valueOf(twips));
                }
            }
        }

        Object background = node.getProps().get("background");
        if (background != null) {
            String color = String.valueOf(background).replace("#", "").trim();
            if (!color.isEmpty()) {
                if (!tcPr.isSetShd()) tcPr.addNewShd();
                tcPr.getShd().setFill(color);
            }
        }

        Object border = node.getProps().get("border");
        if (border != null) {
            CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
            if (border instanceof Boolean) {
                if (!((Boolean) border)) {
                    setCellBorders(borders, STBorder.NONE, 0, null, null);
                }
            } else if (border instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> b = (java.util.Map<String, Object>) border;
                int sz = 8; // 1pt default
                Object sizePt = b.get("size");
                if (sizePt instanceof Number) sz = (int) Math.round(((Number) sizePt).doubleValue() * 8.0);
                String colorHex = null;
                Object colorObj2 = b.get("color");
                if (colorObj2 != null) colorHex = String.valueOf(colorObj2).replace("#", "");
                java.util.Set<String> sides = null;
                Object sidesObj = b.get("sides");
                if (sidesObj instanceof java.util.List) {
                    sides = new java.util.HashSet<>();
                    for (Object s : (java.util.List<?>) sidesObj) {
                        if (s != null) sides.add(String.valueOf(s).toLowerCase());
                    }
                }
                setCellBorders(borders, STBorder.SINGLE, sz, colorHex, sides);
            }
        }
    }

    private void setCellBorders(CTTcBorders borders, STBorder.Enum style, int sz, String color, java.util.Set<String> sides) {
        boolean all = (sides == null || sides.isEmpty());
        if (all || sides.contains("top")) setBorder(borders.isSetTop() ? borders.getTop() : borders.addNewTop(), style, sz, color);
        if (all || sides.contains("right")) setBorder(borders.isSetRight() ? borders.getRight() : borders.addNewRight(), style, sz, color);
        if (all || sides.contains("bottom")) setBorder(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom(), style, sz, color);
        if (all || sides.contains("left")) setBorder(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft(), style, sz, color);
    }

    private BigInteger createBulletNumbering(XWPFDocument doc, String bulletChar, String bulletFont, int indentLeft, int indentIncrement, int indentHanging) {
        XWPFNumbering numbering = doc.createNumbering();
        CTAbstractNum ctab = CTAbstractNum.Factory.newInstance();
        
        // Set abstractNumId explicitly
        ctab.setAbstractNumId(BigInteger.ZERO);
        
        for (int i = 0; i < 9; i++) {
            CTLvl lvl = ctab.addNewLvl();
            lvl.setIlvl(BigInteger.valueOf(i));
            lvl.addNewStart().setVal(BigInteger.ONE);
            lvl.addNewNumFmt().setVal(STNumberFormat.BULLET);
            // Use custom bullet character (default: Wingdings 'l' displays as • bullet)
            lvl.addNewLvlText().setVal(bulletChar);
            
            // Add paragraph properties with indentation
            if (!lvl.isSetPPr()) lvl.addNewPPr();
            if (!lvl.getPPr().isSetInd()) lvl.getPPr().addNewInd();
            // Configurable indentation: indentLeft + level × indentIncrement
            lvl.getPPr().getInd().setLeft(BigInteger.valueOf(indentLeft + i * indentIncrement));
            lvl.getPPr().getInd().setHanging(BigInteger.valueOf(indentHanging));
            
            // Add run properties with custom bullet font (required for correct symbol display)
            if (!lvl.isSetRPr()) lvl.addNewRPr();
            CTFonts fonts = lvl.getRPr().addNewRFonts();
            fonts.setHint(STHint.DEFAULT);
            fonts.setAscii(bulletFont);
            fonts.setHAnsi(bulletFont);
        }
        XWPFAbstractNum abs = new XWPFAbstractNum(ctab);
        BigInteger absId = numbering.addAbstractNum(abs);
        BigInteger numId = numbering.addNum(absId);
        return numId;
    }

    private BigInteger createDecimalNumbering(XWPFDocument doc, Integer start) {
        return createFormattedNumbering(doc, "decimal", start);
    }

    // Overload for backward compatibility when level configuration not provided
    private BigInteger createFormattedNumbering(XWPFDocument doc, String format, Integer start) {
        return createFormattedNumbering(doc, format, start, null);
    }

    private BigInteger createFormattedNumbering(XWPFDocument doc, String format, Integer start, Object levelConfigObj) {
        XWPFNumbering numbering = doc.createNumbering();
        CTAbstractNum ctab = CTAbstractNum.Factory.newInstance();
        java.util.List<java.util.Map<String, Object>> levelConfig = null;
        if (levelConfigObj instanceof java.util.List) {
            levelConfig = new java.util.ArrayList<>();
            for (Object o : (java.util.List<?>) levelConfigObj) {
                if (o instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> m = (java.util.Map<String, Object>) o;
                    levelConfig.add(m);
                } else {
                    levelConfig.add(null);
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            CTLvl lvl = ctab.addNewLvl();
            lvl.setIlvl(BigInteger.valueOf(i));
            // Determine numFmt, lvlText, indents
            STNumberFormat.Enum nf = mapNumberFormat(format);
            String lvlText = "%" + (i + 1) + ".";
            Integer leftPt = null;
            Integer hangingPt = null;
            if (levelConfig != null && i < levelConfig.size()) {
                java.util.Map<String, Object> cfg = levelConfig.get(i);
                if (cfg != null) {
                    Object f = cfg.get("format");
                    if (f != null) nf = mapNumberFormat(String.valueOf(f).toLowerCase());
                    Object lt = cfg.get("lvlText");
                    if (lt != null) lvlText = String.valueOf(lt);
                    Object ind = cfg.get("indent");
                    if (ind instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> im = (java.util.Map<String, Object>) ind;
                        if (im.get("left") != null) leftPt = toTwipsFromPt(im.get("left"), -1);
                        if (im.get("hanging") != null) hangingPt = toTwipsFromPt(im.get("hanging"), -1);
                    }
                }
            }
            lvl.addNewNumFmt().setVal(nf);
            lvl.addNewLvlText().setVal(lvlText);
            lvl.addNewStart().setVal(BigInteger.ONE);
            if (leftPt != null || hangingPt != null) {
                if (!lvl.isSetPPr()) lvl.addNewPPr();
                if (!lvl.getPPr().isSetInd()) lvl.getPPr().addNewInd();
                if (leftPt != null && leftPt >= 0) lvl.getPPr().getInd().setLeft(BigInteger.valueOf(leftPt));
                if (hangingPt != null && hangingPt >= 0) lvl.getPPr().getInd().setHanging(BigInteger.valueOf(hangingPt));
            }
        }
        XWPFAbstractNum abs = new XWPFAbstractNum(ctab);
        BigInteger absId = numbering.addAbstractNum(abs);
        BigInteger numId = numbering.addNum(absId);
        if (start != null && start > 1) {
            XWPFNum num = numbering.getNum(numId);
            if (num != null) {
                num.getCTNum().addNewLvlOverride().setIlvl(BigInteger.ZERO);
                num.getCTNum().getLvlOverrideArray(0).addNewStartOverride().setVal(BigInteger.valueOf(start));
            }
        }
        return numId;
    }

    private STNumberFormat.Enum mapNumberFormat(String format) {
        if (format == null) return STNumberFormat.DECIMAL;
        switch (format) {
            case "lowerletter": return STNumberFormat.LOWER_LETTER;
            case "upperletter": return STNumberFormat.UPPER_LETTER;
            case "lowerroman": return STNumberFormat.LOWER_ROMAN;
            case "upperroman": return STNumberFormat.UPPER_ROMAN;
            default: return STNumberFormat.DECIMAL;
        }
    }

    private void applyRowProps(XWPFTableRow row, VNode node) {
        Object header = node.getProps().get("header");
        if (header instanceof Boolean && (Boolean) header) {
            // Prefer high-level API if available (more stable across POI versions)
            try {
                java.lang.reflect.Method m = XWPFTableRow.class.getMethod("setRepeatHeader", boolean.class);
                m.invoke(row, true);
                return;
            } catch (Exception ignore) {
                // Fallback to low-level schema manipulation without relying on STOnOff enums
            }

            try {
                if (row.getCtRow().getTrPr() == null) row.getCtRow().addNewTrPr();
                // Presence of <w:tblHeader/> is sufficient to mark header rows in many WordprocessingML consumers
                row.getCtRow().getTrPr().addNewTblHeader();
            } catch (Exception ignored) {}
        }

        Object height = node.getProps().get("height");
        if (height != null) {
            int twips = toTwipsFromPt(height, -1);
            if (twips > 0) {
                try {
                    row.setHeight(twips);
                } catch (Exception ignored) {
                    // Fallback via schema if needed
                    try {
                        if (row.getCtRow().getTrPr() == null) row.getCtRow().addNewTrPr();
                        row.getCtRow().getTrPr().addNewTrHeight().setVal(BigInteger.valueOf(twips));
                    } catch (Exception ignored2) {}
                }
            }
        }
    }

    private static class TableContext {
        List<Integer> pending = new ArrayList<>(); // remaining rows of vMerge for each column
    }

    private void renderTableRow(XWPFTable table, VNode rowNode, TableContext ctx) {
        XWPFTableRow row = table.createRow();
        // Ensure the newly created row starts without the default first cell
        try {
            while (row.getTableCells().size() > 0) {
                row.removeCell(0);
            }
        } catch (Exception ignored) {}
        try {
            while (row.getCtRow().sizeOfTcArray() > 0) {
                row.getCtRow().removeTc(0);
            }
        } catch (Exception ignored) {}
        applyRowProps(row, rowNode);

        int col = 0;
        // Insert pending vMerge continue cells at row start
        for (; col < ctx.pending.size(); col++) {
            Integer remain = ctx.pending.get(col);
            if (remain != null && remain > 0) {
                XWPFTableCell c = row.createCell();
                ensureTcPr(c).addNewVMerge().setVal(STMerge.CONTINUE);
                ctx.pending.set(col, remain - 1);
            } else {
                break; // next cells may be explicit
            }
        }

        if (rowNode.getChildren() != null) {
            for (Object cc : rowNode.getChildren()) {
                if (!(cc instanceof VNode) || !((VNode) cc).getType().equals("cell")) continue;
                VNode cellNode = (VNode) cc;
                int colspan = Math.max(1, toInt(cellNode.getProps().get("colspan"), 1));
                int rowspan = Math.max(1, toInt(cellNode.getProps().get("rowspan"), 1));
                XWPFTableCell cell = row.createCell();
                // gridSpan
                if (colspan > 1) {
                    ensureTcPr(cell).addNewGridSpan().setVal(BigInteger.valueOf(colspan));
                }
                // vMerge
                if (rowspan > 1) {
                    ensureTcPr(cell).addNewVMerge().setVal(STMerge.RESTART);
                    // mark pending for covered columns
                    for (int i = 0; i < colspan; i++) {
                        int idx = col + i;
                        while (ctx.pending.size() <= idx) ctx.pending.add(0);
                        int remain = Math.max(0, ctx.pending.get(idx));
                        ctx.pending.set(idx, Math.max(remain, rowspan - 1));
                    }
                }
                applyCellProps(cell, cellNode);
                renderChildren(cell, cellNode);
                col += colspan;
            }
        }

        // Insert trailing continue cells for remaining pending columns
        for (; col < ctx.pending.size(); col++) {
            Integer remain = ctx.pending.get(col);
            if (remain != null && remain > 0) {
                XWPFTableCell c = row.createCell();
                ensureTcPr(c).addNewVMerge().setVal(STMerge.CONTINUE);
                ctx.pending.set(col, remain - 1);
            }
        }
    }

    private CTTcPr ensureTcPr(XWPFTableCell cell) {
        CTTcPr tcPr = cell.getCTTc().getTcPr();
        if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();
        return tcPr;
    }

    private byte[] readImageBytes(String src) {
        if (src == null) return null;
        try {
            if (src.startsWith("data:")) {
                int comma = src.indexOf(",");
                if (comma > 0) {
                    String meta = src.substring(5, comma); // e.g. image/png;base64
                    String data = src.substring(comma + 1);
                    if (meta.contains("base64")) {
                        return Base64.getDecoder().decode(data);
                    }
                }
            } else {
                File f = new File(src);
                if (f.exists()) return Files.readAllBytes(f.toPath());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private int guessPictureType(String src, byte[] bytes) {
        if (src != null) {
            String s = src.toLowerCase();
            if (s.contains("png")) return Document.PICTURE_TYPE_PNG;
            if (s.contains("jpg") || s.contains("jpeg")) return Document.PICTURE_TYPE_JPEG;
        }
        // Fallback by magic numbers
        if (bytes != null && bytes.length > 8) {
            if (bytes[0] == (byte)0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E) return Document.PICTURE_TYPE_PNG;
            if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8) return Document.PICTURE_TYPE_JPEG;
        }
        return Document.PICTURE_TYPE_PNG;
    }

    /**
     * Render a Chart component into the document.
     * 
     * Supported chart types: bar, pie, line, area, column
     * 
     * Props:
     * - type: String - Chart type (bar, pie, line, area, column). Default: "bar"
     * - title: String - Chart title. Default: null
     * - width: Number - Chart width in pixels. Default: 500
     * - height: Number - Chart height in pixels. Default: 300
     * - data: Array - Simple data format: [{label: "A", value: 10}, {label: "B", value: 20}]
     * - categories: Array - Category labels for multi-series: ["Q1", "Q2", "Q3"]
     * - series: Array - Multi-series data: [{name: "Series1", values: [10, 20, 30]}, ...]
     * - colors: Array - Custom colors: ["#FF0000", "#00FF00", "#0000FF"]
     * - legend: Boolean - Show legend. Default: true
     * - legendPosition: String - Legend position (bottom, top, left, right). Default: "bottom"
     */
    @SuppressWarnings("unchecked")
    private void renderChart(XWPFDocument doc, VNode node) {
        try {
            // Parse props
            String chartType = node.getProps().get("type") != null 
                ? String.valueOf(node.getProps().get("type")).toLowerCase() : "bar";
            String title = node.getProps().get("title") != null 
                ? String.valueOf(node.getProps().get("title")) : null;
            if ("null".equals(title)) title = null;
            
            int width = toInt(node.getProps().get("width"), 500);
            int height = toInt(node.getProps().get("height"), 300);
            boolean showLegend = !"false".equals(String.valueOf(node.getProps().get("legend")));
            String legendPos = node.getProps().get("legendPosition") != null 
                ? String.valueOf(node.getProps().get("legendPosition")).toLowerCase() : "bottom";
            
            // Get data
            Object dataObj = node.getProps().get("data");
            Object categoriesObj = node.getProps().get("categories");
            Object seriesObj = node.getProps().get("series");
            Object colorsObj = node.getProps().get("colors");
            
            // Create chart
            XWPFChart chart = doc.createChart(Units.toEMU(width), Units.toEMU(height));
            
            // Set title
            if (title != null && !title.isEmpty()) {
                chart.setTitleText(title);
                chart.setTitleOverlay(false);
            }
            
            // Configure legend
            if (showLegend) {
                XDDFChartLegend legend = chart.getOrAddLegend();
                switch (legendPos) {
                    case "top": legend.setPosition(LegendPosition.TOP); break;
                    case "left": legend.setPosition(LegendPosition.LEFT); break;
                    case "right": legend.setPosition(LegendPosition.RIGHT); break;
                    default: legend.setPosition(LegendPosition.BOTTOM); break;
                }
            }
            
            // Parse colors
            byte[][] customColors = null;
            if (colorsObj instanceof List) {
                List<Object> colorList = (List<Object>) colorsObj;
                customColors = new byte[colorList.size()][];
                for (int i = 0; i < colorList.size(); i++) {
                    String colorStr = String.valueOf(colorList.get(i)).replace("#", "");
                    try {
                        int rgb = Integer.parseInt(colorStr, 16);
                        customColors[i] = new byte[] {
                            (byte) ((rgb >> 16) & 0xFF),
                            (byte) ((rgb >> 8) & 0xFF),
                            (byte) (rgb & 0xFF)
                        };
                    } catch (Exception e) {
                        customColors[i] = new byte[] {(byte)0x33, (byte)0x66, (byte)0x99}; // Default blue
                    }
                }
            }
            
            // Process data based on format
            if (dataObj instanceof List) {
                // Simple format: [{label: "A", value: 10}, ...]
                List<Object> dataList = (List<Object>) dataObj;
                String[] categories = new String[dataList.size()];
                Double[] values = new Double[dataList.size()];
                
                for (int i = 0; i < dataList.size(); i++) {
                    Object item = dataList.get(i);
                    if (item instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) item;
                        categories[i] = map.get("label") != null ? String.valueOf(map.get("label")) : "Item " + (i + 1);
                        values[i] = map.get("value") instanceof Number 
                            ? ((Number) map.get("value")).doubleValue() : 0.0;
                    } else {
                        categories[i] = "Item " + (i + 1);
                        values[i] = 0.0;
                    }
                }
                
                createSingleSeriesChart(chart, chartType, categories, values, "Data", customColors);
                
            } else if (categoriesObj instanceof List && seriesObj instanceof List) {
                // Multi-series format
                List<Object> catList = (List<Object>) categoriesObj;
                List<Object> serList = (List<Object>) seriesObj;
                
                String[] categories = new String[catList.size()];
                for (int i = 0; i < catList.size(); i++) {
                    categories[i] = String.valueOf(catList.get(i));
                }
                
                createMultiSeriesChart(chart, chartType, categories, serList, customColors);
            } else {
                // Default sample data
                String[] defaultCats = {"Category A", "Category B", "Category C", "Category D"};
                Double[] defaultVals = {25.0, 35.0, 20.0, 20.0};
                createSingleSeriesChart(chart, chartType, defaultCats, defaultVals, "Sample Data", customColors);
            }
            
        } catch (Exception e) {
            System.err.println("Error creating chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createSingleSeriesChart(XWPFChart chart, String chartType, String[] categories, 
                                         Double[] values, String seriesName, byte[][] colors) {
        // Create data sources
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories);
        XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);
        
        switch (chartType) {
            case "pie":
                createPieChart(chart, categoryData, valueData, seriesName, colors);
                break;
            case "line":
                createLineChart(chart, categoryData, valueData, seriesName, colors);
                break;
            case "area":
                createAreaChart(chart, categoryData, valueData, seriesName, colors);
                break;
            case "column":
                createColumnChart(chart, categoryData, valueData, seriesName, colors, false);
                break;
            case "bar":
            default:
                createColumnChart(chart, categoryData, valueData, seriesName, colors, true);
                break;
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createMultiSeriesChart(XWPFChart chart, String chartType, String[] categories, 
                                        List<Object> seriesList, byte[][] colors) {
        XDDFDataSource<String> categoryData = XDDFDataSourcesFactory.fromArray(categories);
        
        switch (chartType) {
            case "pie":
                // Pie chart only uses first series
                if (!seriesList.isEmpty() && seriesList.get(0) instanceof Map) {
                    Map<String, Object> firstSeries = (Map<String, Object>) seriesList.get(0);
                    String name = firstSeries.get("name") != null ? String.valueOf(firstSeries.get("name")) : "Series 1";
                    Double[] values = extractValues(firstSeries.get("values"), categories.length);
                    XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);
                    createPieChart(chart, categoryData, valueData, name, colors);
                }
                break;
            case "line":
                createMultiLineChart(chart, categoryData, seriesList, colors);
                break;
            case "area":
                createMultiAreaChart(chart, categoryData, seriesList, colors);
                break;
            case "column":
                createMultiColumnChart(chart, categoryData, seriesList, colors, false);
                break;
            case "bar":
            default:
                createMultiColumnChart(chart, categoryData, seriesList, colors, true);
                break;
        }
    }
    
    private void createPieChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                XDDFNumericalDataSource<Double> values, String seriesName, byte[][] colors) {
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle(seriesName, null);
        
        // Apply colors to pie slices
        if (colors != null && data instanceof XDDFPieChartData) {
            // Colors are applied per data point in pie charts
        }
        
        chart.plot(data);
    }
    
    private void createLineChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                 XDDFNumericalDataSource<Double> values, String seriesName, byte[][] colors) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.LINE, categoryAxis, valueAxis);
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle(seriesName, null);
        
        // Apply color
        if (colors != null && colors.length > 0) {
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                XDDFColor.from(colors[0])
            );
            XDDFLineProperties line = new XDDFLineProperties();
            line.setFillProperties(fill);
            ((XDDFLineChartData.Series) series).setLineProperties(line);
        }
        
        chart.plot(data);
    }
    
    private void createAreaChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                 XDDFNumericalDataSource<Double> values, String seriesName, byte[][] colors) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.AREA, categoryAxis, valueAxis);
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle(seriesName, null);
        
        // Apply color
        if (colors != null && colors.length > 0) {
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                XDDFColor.from(colors[0])
            );
            series.setFillProperties(fill);
        }
        
        chart.plot(data);
    }
    
    private void createColumnChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                   XDDFNumericalDataSource<Double> values, String seriesName, 
                                   byte[][] colors, boolean horizontal) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(horizontal ? AxisPosition.LEFT : AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(horizontal ? AxisPosition.BOTTOM : AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
        ((XDDFBarChartData) data).setBarDirection(horizontal ? BarDirection.BAR : BarDirection.COL);
        
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle(seriesName, null);
        
        // Apply color
        if (colors != null && colors.length > 0) {
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                XDDFColor.from(colors[0])
            );
            series.setFillProperties(fill);
        }
        
        chart.plot(data);
    }
    
    @SuppressWarnings("unchecked")
    private void createMultiLineChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                      List<Object> seriesList, byte[][] colors) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.LINE, categoryAxis, valueAxis);
        
        int colorIdx = 0;
        for (Object serObj : seriesList) {
            if (serObj instanceof Map) {
                Map<String, Object> serMap = (Map<String, Object>) serObj;
                String name = serMap.get("name") != null ? String.valueOf(serMap.get("name")) : "Series";
                Double[] values = extractValues(serMap.get("values"), (int) categories.getPointCount());
                XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);
                
                XDDFChartData.Series series = data.addSeries(categories, valueData);
                series.setTitle(name, null);
                
                // Apply color
                if (colors != null && colorIdx < colors.length) {
                    XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                        XDDFColor.from(colors[colorIdx])
                    );
                    XDDFLineProperties line = new XDDFLineProperties();
                    line.setFillProperties(fill);
                    ((XDDFLineChartData.Series) series).setLineProperties(line);
                }
                colorIdx++;
            }
        }
        
        chart.plot(data);
    }
    
    @SuppressWarnings("unchecked")
    private void createMultiAreaChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                      List<Object> seriesList, byte[][] colors) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.AREA, categoryAxis, valueAxis);
        
        int colorIdx = 0;
        for (Object serObj : seriesList) {
            if (serObj instanceof Map) {
                Map<String, Object> serMap = (Map<String, Object>) serObj;
                String name = serMap.get("name") != null ? String.valueOf(serMap.get("name")) : "Series";
                Double[] values = extractValues(serMap.get("values"), (int) categories.getPointCount());
                XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);
                
                XDDFChartData.Series series = data.addSeries(categories, valueData);
                series.setTitle(name, null);
                
                // Apply color
                if (colors != null && colorIdx < colors.length) {
                    XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                        XDDFColor.from(colors[colorIdx])
                    );
                    series.setFillProperties(fill);
                }
                colorIdx++;
            }
        }
        
        chart.plot(data);
    }
    
    @SuppressWarnings("unchecked")
    private void createMultiColumnChart(XWPFChart chart, XDDFDataSource<String> categories, 
                                        List<Object> seriesList, byte[][] colors, boolean horizontal) {
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(horizontal ? AxisPosition.LEFT : AxisPosition.BOTTOM);
        XDDFValueAxis valueAxis = chart.createValueAxis(horizontal ? AxisPosition.BOTTOM : AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        
        XDDFChartData data = chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
        ((XDDFBarChartData) data).setBarDirection(horizontal ? BarDirection.BAR : BarDirection.COL);
        
        int colorIdx = 0;
        for (Object serObj : seriesList) {
            if (serObj instanceof Map) {
                Map<String, Object> serMap = (Map<String, Object>) serObj;
                String name = serMap.get("name") != null ? String.valueOf(serMap.get("name")) : "Series";
                Double[] values = extractValues(serMap.get("values"), (int) categories.getPointCount());
                XDDFNumericalDataSource<Double> valueData = XDDFDataSourcesFactory.fromArray(values);
                
                XDDFChartData.Series series = data.addSeries(categories, valueData);
                series.setTitle(name, null);
                
                // Apply color
                if (colors != null && colorIdx < colors.length) {
                    XDDFSolidFillProperties fill = new XDDFSolidFillProperties(
                        XDDFColor.from(colors[colorIdx])
                    );
                    series.setFillProperties(fill);
                }
                colorIdx++;
            }
        }
        
        chart.plot(data);
    }
    
    @SuppressWarnings("unchecked")
    private Double[] extractValues(Object valuesObj, int expectedLength) {
        Double[] result = new Double[expectedLength];
        for (int i = 0; i < expectedLength; i++) {
            result[i] = 0.0;
        }
        
        if (valuesObj instanceof List) {
            List<Object> valuesList = (List<Object>) valuesObj;
            for (int i = 0; i < Math.min(valuesList.size(), expectedLength); i++) {
                Object val = valuesList.get(i);
                if (val instanceof Number) {
                    result[i] = ((Number) val).doubleValue();
                }
            }
        }
        
        return result;
    }
    
    /**
     * Renders a watermark component into the document.
     * Watermarks are text overlays that appear behind the main content on every page.
     * 
     * Props supported:
     * - text: String - The watermark text. Required.
     * - color: String - Text color in hex format (e.g., "#CCCCCC"). Default: "#C0C0C0" (light gray)
     * - fontFamily: String - Font family name. Default: "Cambria"
     * - fontSize: Number - Font size in points. Default: 88
     * - rotation: Number - Rotation angle in degrees (clockwise). Default: -45 (diagonal)
     * - opacity: Number - Opacity from 0.0 to 1.0. Default: 0.5
     */
    private void renderWatermark(XWPFDocument doc, VNode node) {
        try {
            // Extract props
            String text = node.getProps().get("text") != null 
                ? String.valueOf(node.getProps().get("text")) : "WATERMARK";
            if ("null".equals(text) || text.isEmpty()) {
                text = "WATERMARK";
            }
            
            String color = node.getProps().get("color") != null 
                ? String.valueOf(node.getProps().get("color")) : "#C0C0C0";
            if (color.startsWith("#")) {
                color = color.substring(1); // Remove # prefix
            }
            if ("null".equals(color) || color.isEmpty()) {
                color = "C0C0C0";
            }
            // Convert hex to VML color format (silver, gray, or #RRGGBB)
            String vmlColor = "#" + color.toUpperCase();
            
            String fontFamily = node.getProps().get("fontFamily") != null 
                ? String.valueOf(node.getProps().get("fontFamily")) : "Cambria";
            if ("null".equals(fontFamily) || fontFamily.isEmpty()) {
                fontFamily = "Cambria";
            }
            
            int fontSize = toInt(node.getProps().get("fontSize"), 88);
            int rotation = toInt(node.getProps().get("rotation"), -45);
            double opacity = 0.5;
            Object opacityObj = node.getProps().get("opacity");
            if (opacityObj instanceof Number) {
                opacity = ((Number) opacityObj).doubleValue();
            } else if (opacityObj != null) {
                try {
                    opacity = Double.parseDouble(String.valueOf(opacityObj));
                } catch (NumberFormatException ignored) {}
            }
            // Clamp opacity between 0 and 1
            opacity = Math.max(0.0, Math.min(1.0, opacity));
            
            // Create HeaderFooterPolicy if not exists
            XWPFHeaderFooterPolicy policy = doc.getHeaderFooterPolicy();
            if (policy == null) {
                policy = doc.createHeaderFooterPolicy();
            }
            
            // Create watermark in all header types (DEFAULT, FIRST, EVEN)
            createWatermarkHeader(doc, policy, STHdrFtr.DEFAULT, text, vmlColor, fontFamily, fontSize, rotation, opacity, 1);
            createWatermarkHeader(doc, policy, STHdrFtr.FIRST, text, vmlColor, fontFamily, fontSize, rotation, opacity, 2);
            createWatermarkHeader(doc, policy, STHdrFtr.EVEN, text, vmlColor, fontFamily, fontSize, rotation, opacity, 3);
            
        } catch (Exception e) {
            System.err.println("Error creating watermark: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createWatermarkHeader(XWPFDocument doc, XWPFHeaderFooterPolicy policy, STHdrFtr.Enum headerType,
                                       String text, String color, String fontFamily, 
                                       int fontSize, int rotation, double opacity, int idx) throws Exception {
        // Create the watermark paragraph with VML shape
        XWPFParagraph watermarkPara = createWatermarkParagraph(doc, policy, text, color, fontFamily, fontSize, rotation, opacity, idx);
        
        // Create the header with this paragraph
        XWPFParagraph[] pars = new XWPFParagraph[1];
        pars[0] = watermarkPara;
        policy.createHeader(headerType, pars);
    }
    
    private XWPFParagraph createWatermarkParagraph(XWPFDocument doc, XWPFHeaderFooterPolicy policy, String text, 
                                                   String color, String fontFamily, int fontSize,
                                                   int rotation, double opacity, int idx) {
        // Create a CTP (paragraph XML) with VML watermark
        CTP p = CTP.Factory.newInstance();
        
        // Get rsid from document body if available
        CTBody ctBody = doc.getDocument().getBody();
        byte[] rsidr = null;
        byte[] rsidrdefault = null;
        if (ctBody.sizeOfPArray() > 0) {
            CTP ctp = ctBody.getPArray(0);
            rsidr = ctp.getRsidR();
            rsidrdefault = ctp.getRsidRDefault();
        }
        if (rsidr != null) p.setRsidP(rsidr);
        if (rsidrdefault != null) p.setRsidRDefault(rsidrdefault);
        
        // Add paragraph properties
        CTPPr pPr = p.addNewPPr();
        pPr.addNewPStyle().setVal("Header");
        
        // Add run with picture (VML)
        CTR r = p.addNewR();
        CTRPr rPr = r.addNewRPr();
        rPr.addNewNoProof();
        
        // Create VML picture
        CTPicture pict = r.addNewPict();
        CTGroup group = CTGroup.Factory.newInstance();
        
        // Create shapetype (text wave shape type #136)
        CTShapetype shapetype = group.addNewShapetype();
        shapetype.setId("_x0000_t136");
        shapetype.setCoordsize("1600,21600");
        shapetype.setSpt(136);
        shapetype.setAdj("10800");
        shapetype.setPath2("m@7,0l@8,0m@5,21600l@6,21600e");
        
        // Add formulas for the shape type
        CTFormulas formulas = shapetype.addNewFormulas();
        formulas.addNewF().setEqn("sum #0 0 10800");
        formulas.addNewF().setEqn("prod #0 2 1");
        formulas.addNewF().setEqn("sum 21600 0 @1");
        formulas.addNewF().setEqn("sum 0 0 @2");
        formulas.addNewF().setEqn("sum 21600 0 @3");
        formulas.addNewF().setEqn("if @0 @3 0");
        formulas.addNewF().setEqn("if @0 21600 @1");
        formulas.addNewF().setEqn("if @0 0 @2");
        formulas.addNewF().setEqn("if @0 @4 21600");
        formulas.addNewF().setEqn("mid @5 @6");
        formulas.addNewF().setEqn("mid @8 @5");
        formulas.addNewF().setEqn("mid @7 @8");
        formulas.addNewF().setEqn("mid @6 @7");
        formulas.addNewF().setEqn("sum @6 0 @5");
        
        // Add path
        CTPath path = shapetype.addNewPath();
        path.setTextpathok(STTrueFalse.T);
        path.setConnecttype(STConnectType.CUSTOM);
        path.setConnectlocs("@9,0;@10,10800;@11,21600;@12,10800");
        path.setConnectangles("270,180,90,0");
        
        // Add textpath to shapetype
        CTTextPath shapeTypeTextPath = shapetype.addNewTextpath();
        shapeTypeTextPath.setOn(STTrueFalse.T);
        shapeTypeTextPath.setFitshape(STTrueFalse.T);
        
        // Add handles
        CTHandles handles = shapetype.addNewHandles();
        CTH h = handles.addNewH();
        h.setPosition("#0,bottomRight");
        h.setXrange("6629,14971");
        
        // Add lock
        CTLock lock = shapetype.addNewLock();
        lock.setExt(STExt.EDIT);
        
        // Create the actual shape
        CTShape shape = group.addNewShape();
        shape.setId("PowerPlusWaterMarkObject" + idx);
        shape.setSpid("_x0000_s102" + (4 + idx));
        shape.setType("#_x0000_t136");
        
        // Calculate style based on rotation and opacity
        // Width and height depend on font size
        int width = Math.max(200, fontSize * 5); // pt
        int height = Math.max(100, (int)(fontSize * 2.5)); // pt
        
        // Build style string
        StringBuilder style = new StringBuilder();
        style.append("position:absolute;");
        style.append("margin-left:0;margin-top:0;");
        style.append("width:").append(width).append("pt;");
        style.append("height:").append(height).append("pt;");
        style.append("z-index:-251654144;");
        style.append("mso-wrap-edited:f;");
        style.append("mso-position-horizontal:center;");
        style.append("mso-position-horizontal-relative:margin;");
        style.append("mso-position-vertical:center;");
        style.append("mso-position-vertical-relative:margin;");
        if (rotation != 0) {
            style.append("rotation:").append(rotation).append(";");
        }
        
        shape.setStyle(style.toString());
        
        // Wrap coordinates (standard watermark wrap)
        shape.setWrapcoords("616 5068 390 16297 39 16921 -39 17155 7265 17545 7186 17467 -39 17467 18904 17467 10507 17467 8710 17545 18904 17077 18787 16843 18358 16297 18279 12554 19178 12476 20701 11774 20779 11228 21131 10059 21248 8811 21248 7563 20975 6316 20935 5380 19490 5146 14022 5068 2616 5068");
        
        // Set fill color with opacity
        if (opacity < 1.0) {
            // For semi-transparent, use fill with opacity
            shape.setFillcolor(color);
            shape.setStroked(STTrueFalse.FALSE);
            // VML opacity uses percentage string or decimal
            CTFill fill = shape.addNewFill();
            fill.setOpacity(String.valueOf((int)(opacity * 65536)) + "f"); // VML uses 65536 as 100%
        } else {
            shape.setFillcolor(color);
            shape.setStroked(STTrueFalse.FALSE);
        }
        
        // Add textpath to shape
        CTTextPath shapeTextPath = shape.addNewTextpath();
        shapeTextPath.setStyle("font-family:\"" + fontFamily + "\";font-size:" + fontSize + "pt");
        shapeTextPath.setString(text);
        
        // Set the VML group to the picture
        pict.set(group);
        
        // Return as XWPFParagraph
        return new XWPFParagraph(p, doc);
    }

    /**
     * Render a Comment component that wraps text with annotation
     * Props:
     *   - author: String (required) - Comment author name
     *   - text: String (required) - Comment content
     *   - initials: String (optional) - Author initials
     *   - date: String (optional) - Comment date (ISO format)
     * Children: Text content to be annotated
     */
    private void renderComment(XWPFParagraph para, VNode node) {
        try {
            // Get props
            String author = String.valueOf(node.getProps().get("author"));
            if (author == null || "null".equals(author)) {
                author = "Author";
            }
            
            String commentText = String.valueOf(node.getProps().get("text"));
            if (commentText == null || "null".equals(commentText)) {
                commentText = "";
            }
            
            String initials = String.valueOf(node.getProps().get("initials"));
            if (initials == null || "null".equals(initials)) {
                initials = null;
            }
            
            String date = String.valueOf(node.getProps().get("date"));
            if (date == null || "null".equals(date)) {
                date = null;
            }
            
            // Find the document
            XWPFDocument doc = findDocument(para);
            if (doc == null) {
                System.err.println("Error: Cannot find document for comment");
                return;
            }
            
            // Generate unique comment ID
            BigInteger commentId = BigInteger.valueOf(nextCommentId++);
            
            // Create or get comments container
            XWPFComments comments = doc.createComments();
            
            // Create the comment
            XWPFComment comment = comments.createComment(commentId);
            comment.setAuthor(author);
            if (initials != null) {
                comment.setInitials(initials);
            }
            // Note: Date parsing omitted for simplicity; use current date if needed
            
            // Add comment text as paragraph
            XWPFParagraph commentPara = comment.createParagraph();
            XWPFRun commentRun = commentPara.createRun();
            commentRun.setText(commentText);
            
            // Get underlying CTP for low-level XML manipulation
            CTP ctp = para.getCTP();
            
            // Add comment range start
            CTMarkupRange commentStart = ctp.addNewCommentRangeStart();
            commentStart.setId(commentId);
            
            // Render children (the annotated text)
            renderChildren(para, node);
            
            // Add comment range end
            CTMarkupRange commentEnd = ctp.addNewCommentRangeEnd();
            commentEnd.setId(commentId);
            
            // Add comment reference
            CTR commentRef = ctp.addNewR();
            commentRef.addNewCommentReference().setId(commentId);
            
        } catch (Exception e) {
            System.err.println("Error: Failed to render Comment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Render a Footnote component that adds a reference mark and content at page bottom
     * Props:
     *   - text: String (required) - Footnote content text
     * The footnote reference mark will be added inline where the component appears
     */
    private void renderFootnote(XWPFParagraph para, VNode node) {
        try {
            // Get props
            String footnoteText = String.valueOf(node.getProps().get("text"));
            if (footnoteText == null || "null".equals(footnoteText)) {
                footnoteText = "";
            }
            
            // Find the document
            XWPFDocument doc = findDocument(para);
            if (doc == null) {
                System.err.println("Error: Cannot find document for footnote");
                return;
            }
            
            // Create the footnote
            XWPFFootnote footnote = doc.createFootnote();
            
            // Add footnote content
            XWPFParagraph footnotePara = footnote.createParagraph();
            XWPFRun footnoteRun = footnotePara.createRun();
            footnoteRun.setText(footnoteText);
            
            // Get the footnote ID
            BigInteger footnoteId = footnote.getId();
            
            // Add footnote reference in the main paragraph
            XWPFRun run = para.createRun();
            CTP ctp = para.getCTP();
            CTR ctr = ctp.addNewR();
            ctr.addNewFootnoteReference().setId(footnoteId);
            
        } catch (Exception e) {
            System.err.println("Error: Failed to render Footnote: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Render an Endnote component that adds a reference mark and content at document end
     * Props:
     *   - text: String (required) - Endnote content text
     * The endnote reference mark will be added inline where the component appears
     */
    private void renderEndnote(XWPFParagraph para, VNode node) {
        try {
            // Get props
            String endnoteText = String.valueOf(node.getProps().get("text"));
            if (endnoteText == null || "null".equals(endnoteText)) {
                endnoteText = "";
            }
            
            // Find the document
            XWPFDocument doc = findDocument(para);
            if (doc == null) {
                System.err.println("Error: Cannot find document for endnote");
                return;
            }
            
            // Create the endnote
            XWPFEndnote endnote = doc.createEndnote();
            
            // Add endnote content
            XWPFParagraph endnotePara = endnote.createParagraph();
            XWPFRun endnoteRun = endnotePara.createRun();
            endnoteRun.setText(endnoteText);
            
            // Get the endnote ID
            BigInteger endnoteId = endnote.getId();
            
            // Add endnote reference in the main paragraph
            XWPFRun run = para.createRun();
            CTP ctp = para.getCTP();
            CTR ctr = ctp.addNewR();
            ctr.addNewEndnoteReference().setId(endnoteId);
            
        } catch (Exception e) {
            System.err.println("Error: Failed to render Endnote: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
