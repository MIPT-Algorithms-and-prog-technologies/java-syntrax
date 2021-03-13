package org.atpfivt.jsyntrax.generators;

import org.atpfivt.jsyntrax.generators.elements.Element;
import org.atpfivt.jsyntrax.styles.NodeStyle;
import org.atpfivt.jsyntrax.styles.StyleConfig;
import org.atpfivt.jsyntrax.util.StringUtils;
import org.atpfivt.jsyntrax.util.Pair;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SVGCanvas {
    private final StyleConfig style;
    private final HashMap<String, Integer> tagcnt = new HashMap<>();
    private final ArrayList<Element> elements = new ArrayList<>();

    public SVGCanvas(StyleConfig style) {
        this.style = style;
    }

    // default prefix = "x", default suffix = ""
    public String newTag(String prefix, String suffix) {
        String f = prefix + "___" + suffix;
        Integer value = tagcnt.getOrDefault(f, 0);
        tagcnt.put(f, value + 1);
        return prefix + value.toString() + suffix;
    }

    public void addElement(Element e) {
        this.elements.add(e);
    }

    public void addTagByTag(String addTag, String tag) {
        for (Element e : elements) {
            if (e.isTagged(tag)) {
                e.addTag(addTag);
            }
        }
    }

    public void dropTag(final String tag) {
        for (Element e : elements) {
            if (e.isTagged(tag)) {
                e.delTag(tag);
            }
        }
    }

    public void moveByTag(String tag, int dx, int dy) {
        for (Element e : elements) {
            if (e.isTagged(tag)) {
                e.getStart().f += dx;
                e.getStart().s += dy;
                e.getEnd().f += dx;
                e.getEnd().s += dy;
            }
        }
    }

    public void scaleByTag(String tag, double scale) {
        for (Element e : elements) {
            if (e.isTagged(tag)) {
                e.scale(scale);
            }
        }
    }

    public Optional<String> getCanvasTag() {
        return elements.stream().findAny().flatMap(Element::getAnyTag);
    }

    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getBoundingBoxByTag(String tag) {
        Pair<Integer, Integer> start = null;
        Pair<Integer, Integer> end = null;
        for (Element e: this.elements) {
            if (e.isTagged(tag)) {
                if (start == null) {
                    start = new Pair<>(e.getStart());
                    end = new Pair<>(e.getEnd());
                }
                start.f = Math.min(start.f, e.getStart().f);
                start.f = Math.min(start.f, e.getEnd().f);
                start.s = Math.min(start.s, e.getStart().s);
                start.s = Math.min(start.s, e.getEnd().s);

                end.f = Math.max(end.f, e.getStart().f);
                end.f = Math.max(end.f, e.getEnd().f);
                end.s = Math.max(end.s, e.getStart().s);
                end.s = Math.max(end.s, e.getEnd().s);
            }
        }
        return new Pair<>(start, end);
    }

    public String generateSVG() {
        StringBuilder sb = new StringBuilder();
        double scale = style.getScale();

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> res = getBoundingBoxByTag("all");

        // move to picture to (0, 0)
        moveByTag("all", -res.f.f, -res.f.s);
        scaleByTag("all", scale);
        moveByTag("all", style.getPadding(), style.getPadding());

        res = getBoundingBoxByTag("all");
        Pair<Integer, Integer> end = res.s;

        int w = end.f + style.getPadding();
        int h = end.s + style.getPadding();

        // collect fonts
        HashMap<String, Pair<Font, Color>> fonts = new HashMap<>();
        fonts.put("title_font", new Pair<>(this.style.getTitleFont(), this.style.getTextColor()));
        for (NodeStyle ns : this.style.getNodeStyles()) {
            fonts.put(ns.getName() + "_font", new Pair<>(ns.getFont(), ns.getTextColor()));
        }

        // header
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
                .append("<!-- Created by Jyntrax https://github.com/atp-mipt/jsyntrax -->\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\"\n");
        sb.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n");
        sb.append("xml:space=\"preserve\"\n");
        sb.append("width=\"").append(w).append("\" ")
                .append("height=\"").append(h).append("\" ")
                .append("version=\"1.1\">\n");
        // styles
        sb.append("<style type=\"text/css\">\n");
        sb.append("<![CDATA[\n");
        // fonts
        for (Map.Entry<String, Pair<Font, Color>> fontPair : fonts.entrySet()) {
            String fontName = fontPair.getKey();
            String fontFamily = fontPair.getValue().f.getName();
            String fontSize = Integer.toString((int) (fontPair.getValue().f.getSize() * scale));
            String fontWeight = "normal";
            if ((fontPair.getValue().f.getStyle() & Font.BOLD) == Font.BOLD) {
                fontWeight = "bold";
            }
            String fontStyle = "normal";
            if ((fontPair.getValue().f.getStyle() & Font.ITALIC) == Font.ITALIC) {
                fontStyle = "italic";
            }

            String hex = StringUtils.toHex(fontPair.getValue().s);

            sb.append(".").append(fontName).append(" ");
            sb.append("{fill:").append(hex).append("; text-anchor:middle;\n");
            sb.append("font-family:").append(fontFamily).append("; ");
            sb.append("font-size:").append(fontSize).append("pt; ");
            sb.append("font-weight:").append(fontWeight).append("; ");
            sb.append("font-style:").append(fontStyle).append("; ");
            sb.append("}\n");
        }
        // other fonts
        sb.append(".label {fill: #000; text-anchor:middle; font-size:16pt; font-weight:bold; font-family:Sans;}\n");
        sb.append(".link {fill: #0D47A1;}\n");
        sb.append(".link:hover {fill: #0D47A1; text-decoration:underline;}\n");
        sb.append(".link:visited {fill: #4A148C;}\n");
        // close
        sb.append("]]>\n</style>\n");
        // defs
        sb.append("<defs>\n");
        sb.append("<marker id=\"arrow\" markerWidth=\"5\" markerHeight=\"4\" ")
                .append("refX=\"2.5\" refY=\"2\" orient=\"auto\" markerUnits=\"strokeWidth\">\n");
        String hex = StringUtils.toHex(this.style.getLineColor());
        sb.append("<path d=\"M0,0 L0.5,2 L0,4 L4.5,2 z\" fill=\"").append(hex).append("\" />\n");
        sb.append("</marker>\n</defs>\n");

        // elements
        if (!style.isTransparent()) {
            sb.append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>\n");
        }
        for (Element e : this.elements) {

            if (style.isShadow()) {
                e.addShadow(sb,  this.style);
            }
            e.toSVG(sb, this.style);
        }
        // end
        sb.append("</svg>\n");
        return sb.toString();
    }
}
