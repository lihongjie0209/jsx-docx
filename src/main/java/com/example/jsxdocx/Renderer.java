package com.example.jsxdocx;

import com.example.jsxdocx.model.VNode;
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
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.math.BigInteger;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Renderer {

    public void renderToDocx(VNode vDom, String outputPath) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {

            if (!"document".equals(vDom.getType())) {
                throw new IllegalArgumentException("Root element must be <Document>");
            }

            renderChildren(document, vDom);

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
                applySectionProps(parent, node);
                renderChildren(parent, node);
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
                    String level = String.valueOf(node.getProps().getOrDefault("level", "1"));
                    p.setStyle("Heading" + level);
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
            default:
                System.err.println("Unknown component type: " + type);
        }
    }

    private void applySectionProps(Object parent, VNode node) {
        if (!(parent instanceof XWPFDocument)) return;
        XWPFDocument doc = (XWPFDocument) parent;
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        Object size = node.getProps().get("pageSize");
        if (size != null) {
            String s = String.valueOf(size).toUpperCase();
            CTPageSz pgSz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
            if ("A4".equals(s)) {
                pgSz.setW(BigInteger.valueOf(11900));
                pgSz.setH(BigInteger.valueOf(16840));
            } else if ("LETTER".equals(s)) {
                pgSz.setW(BigInteger.valueOf(12240));
                pgSz.setH(BigInteger.valueOf(15840));
            }
        }

        Object orientation = node.getProps().get("orientation");
        if (orientation != null) {
            String o = String.valueOf(orientation).toLowerCase();
            CTPageSz pgSz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
            try {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.Enum val =
                        "landscape".equals(o)
                                ? org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.LANDSCAPE
                                : org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.PORTRAIT;
                pgSz.setOrient(val);
            } catch (Throwable ignored) {}
            // Note: width/height swap is not required for Word to respect orientation
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

    private void applyParagraphProps(XWPFParagraph p, VNode node) {
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
}
