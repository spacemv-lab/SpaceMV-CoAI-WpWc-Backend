package com.txwx.social.crm.publisher.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 微信公众号 135 格式内容格式化器。
 * 将 contentJson（ProseMirror JSON）渲染为微信 135 编辑器风格的嵌套 section HTML。
 *
 * 渲染优先级: 元素 styleOverrides > stylePreset > Formatter 默认值
 */
@Slf4j
@Component
public class WeChatFormatter implements IContentFormatter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getTargetCode() {
        return "WECHAT_OFFICIAL_ACCOUNT";
    }

    @Override
    public String format(String contentJson, String stylePreset) {
        if (contentJson == null || contentJson.isBlank()) {
            return "";
        }
        try {
            JsonNode doc = objectMapper.readTree(contentJson);
            StringBuilder html = new StringBuilder();
            JsonNode content = doc.get("content");
            if (content != null && content.isArray()) {
                for (JsonNode node : content) {
                    html.append(renderNode(node, getDefaultPreset(stylePreset)));
                }
            }
            return wrapSection(html.toString());
        } catch (JsonProcessingException e) {
            log.error("解析 contentJson 失败: {}", e.getMessage());
            return contentJson;
        }
    }

    @Override
    public String preview(String contentJson, String stylePreset) {
        return format(contentJson, stylePreset);
    }

    private String renderNode(JsonNode node, Map<String, Object> preset) {
        String type = node.has("type") ? node.get("type").asText() : "paragraph";
        Map<String, Object> styleOverrides = extractStyleOverrides(node);

        switch (type) {
            case "heading":
                return renderHeading(node, preset, styleOverrides);
            case "paragraph":
                return renderParagraph(node, preset, styleOverrides);
            case "blockquote":
                return renderBlockquote(node, preset, styleOverrides);
            case "bulletList":
            case "orderedList":
                return renderList(node, preset, styleOverrides);
            case "chart":
                return renderChart(node);
            case "map":
                return renderMap(node);
            case "image":
                return renderImage(node, styleOverrides);
            case "horizontalRule":
                return "<hr style=\"border: 1px solid #e8e8e8; margin: 20px 0;\">";
            case "styledSection":
                return renderStyledSection(node, preset);
            case "hardBreak":
                return "<br>";
            default:
                return renderParagraph(node, preset, styleOverrides);
        }
    }

    private String renderHeading(JsonNode node, Map<String, Object> preset, Map<String, Object> overrides) {
        int level = node.has("attrs") && node.get("attrs").has("level")
                ? node.get("attrs").get("level").asInt(2) : 2;
        String html = renderInlineContent(node);
        String typeKey = level == 1 ? "h1" : level == 2 ? "h2" : "h3";
        String color = getStyle(typeKey, "color", overrides, preset, "#333333");
        String fontSize = getStyle(typeKey, "fontSize", overrides, preset, level == 1 ? "22px" : level == 2 ? "20px" : "17px");
        String fontWeight = getStyle(typeKey, "fontWeight", overrides, preset, "bold");
        String borderLeft = getStyle(typeKey, "borderLeft", overrides, preset, "3px solid #548DD4");
        String paddingLeft = getStyle(typeKey, "paddingLeft", overrides, preset, "12px");
        String margin = getStyle(typeKey, "margin", overrides, preset, level == 2 ? "24px 0 12px" : "16px 0 10px");

        return String.format(
                "<section><section style=\"border-left:%s;padding-left:%s\">" +
                "<h%d style=\"color:%s;font-size:%s;font-weight:%s;margin:%s\">%s</h%d>" +
                "</section></section>",
                borderLeft, paddingLeft, level, color, fontSize, fontWeight, margin, html, level);
    }

    private String renderParagraph(JsonNode node, Map<String, Object> preset, Map<String, Object> overrides) {
        String html = renderInlineContent(node);
        String color = getStyle("p", "color", overrides, preset, "#333333");
        String fontSize = getStyle("p", "fontSize", overrides, preset, "14px");
        String lineHeight = getStyle("p", "lineHeight", overrides, preset, "1.75");
        String margin = getStyle("p", "margin", overrides, preset, "0 0 10px");

        return String.format(
                "<p style=\"color:%s;font-size:%s;line-height:%s;margin:%s\">%s</p>",
                color, fontSize, lineHeight, margin, html);
    }

    private String renderBlockquote(JsonNode node, Map<String, Object> preset, Map<String, Object> overrides) {
        String html = renderInlineContent(node);
        String bgColor = getStyle("blockquote", "bgColor", overrides, preset, "#F6F9FD");
        String borderRadius = getStyle("blockquote", "borderRadius", overrides, preset, "8px");
        String padding = getStyle("blockquote", "padding", overrides, preset, "16px");
        String margin = getStyle("blockquote", "margin", overrides, preset, "16px 0");

        return String.format(
                "<section style=\"background:%s;border-radius:%s;padding:%s;margin:%s\"><p>%s</p></section>",
                bgColor, borderRadius, padding, margin, html);
    }

    private String renderList(JsonNode node, Map<String, Object> preset, Map<String, Object> overrides) {
        String listType = node.get("type").asText();
        boolean ordered = "orderedList".equals(listType);
        StringBuilder html = new StringBuilder();
        html.append(ordered ? "<ol>" : "<ul>");

        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode item : content) {
                html.append("<li>");
                html.append(renderInlineContent(item));
                html.append("</li>");
            }
        }
        html.append(ordered ? "</ol>" : "</ul>");
        return html.toString();
    }

    private String renderChart(JsonNode node) {
        String slug = node.has("attrs") && node.get("attrs").has("slug")
                ? node.get("attrs").get("slug").asText() : "";
        String imageUrl = getStringAttr(node, "wechatImageUrl");
        if (imageUrl.isBlank()) {
            imageUrl = getStringAttr(node, "imageUrl");
        }
        if (!imageUrl.isBlank()) {
            return String.format(
                    "<section style=\"margin:16px 0;text-align:center\">" +
                    "<img src=\"%s\" alt=\"%s\" style=\"max-width:100%%;height:auto;display:block;margin:0 auto;border-radius:4px;\">" +
                    "</section>",
                    escapeHtml(imageUrl), escapeHtml(slug));
        }
        return String.format(
                "<section style=\"border:1px solid #e8e8e8;border-radius:8px;" +
                "padding:16px;text-align:center;margin:16px 0\">" +
                "<span style=\"color:#548DD4;font-weight:600\">📊 查看完整图表 →</span>" +
                "<br><span style=\"color:#999;font-size:12px\">%s</span></section>",
                escapeHtml(slug));
    }

    private String renderMap(JsonNode node) {
        String token = node.has("attrs") && node.get("attrs").has("token")
                ? node.get("attrs").get("token").asText() : "";
        String imageUrl = getStringAttr(node, "wechatImageUrl");
        if (imageUrl.isBlank()) {
            imageUrl = getStringAttr(node, "imageUrl");
        }
        if (!imageUrl.isBlank()) {
            return String.format(
                    "<section style=\"margin:16px 0;text-align:center\">" +
                    "<img src=\"%s\" alt=\"%s\" style=\"max-width:100%%;height:auto;display:block;margin:0 auto;border-radius:4px;\">" +
                    "</section>",
                    escapeHtml(imageUrl), escapeHtml(token));
        }
        return String.format(
                "<section style=\"border:1px solid #e8e8e8;border-radius:8px;" +
                "padding:16px;text-align:center;margin:16px 0\">" +
                "<span style=\"color:#548DD4;font-weight:600\">查看地图</span>" +
                "<br><span style=\"color:#999;font-size:12px\">%s</span></section>",
                escapeHtml(token));
    }

    private String renderImage(JsonNode node, Map<String, Object> overrides) {
        String src = node.has("attrs") && node.get("attrs").has("src")
                ? node.get("attrs").get("src").asText() : "";
        String borderRadius = overrides.containsKey("borderRadius")
                ? (String) overrides.get("borderRadius") : "8px";

        return String.format(
                "<img src=\"%s\" style=\"border-radius:%s;max-width:100%%;display:block;margin:10px 0\">",
                escapeHtml(src), borderRadius);
    }

    private String renderStyledSection(JsonNode node, Map<String, Object> preset) {
        String style = node.has("attrs") && node.get("attrs").has("style")
                && node.get("attrs").get("style") != null
                && !node.get("attrs").get("style").isNull()
                ? node.get("attrs").get("style").asText() : "";
        StringBuilder html = new StringBuilder();
        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode child : content) {
                html.append(renderNode(child, preset));
            }
        }
        if (html.isEmpty()) {
            return "";
        }
        String styleAttr = style.isBlank() ? "" : " style=\"" + escapeHtml(style) + "\"";
        return "<section" + styleAttr + ">" + html + "</section>";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDefaultPreset(String stylePreset) {
        Map<String, Object> preset = new HashMap<>();
        if (stylePreset == null) return preset;

        switch (stylePreset) {
            case "gewu-wechat-blue":
                preset.put("h2.color", "#548DD4");
                preset.put("h2.fontSize", "20px");
                preset.put("h2.borderLeft", "3px solid #548DD4");
                preset.put("p.color", "#333333");
                preset.put("p.fontSize", "14px");
                preset.put("p.lineHeight", "1.75");
                preset.put("blockquote.bgColor", "#F6F9FD");
                preset.put("blockquote.borderRadius", "8px");
                break;
            case "gewu-fast-black":
                preset.put("h2.color", "#1a1a1a");
                preset.put("h2.fontSize", "18px");
                preset.put("h2.borderLeft", "3px solid #1a1a1a");
                preset.put("p.color", "#444444");
                preset.put("p.fontSize", "15px");
                preset.put("p.lineHeight", "1.6");
                preset.put("blockquote.bgColor", "#F5F5F5");
                preset.put("blockquote.borderRadius", "0px");
                break;
        }
        return preset;
    }

    private Map<String, Object> extractStyleOverrides(JsonNode node) {
        Map<String, Object> overrides = new HashMap<>();
        if (node.has("attrs") && node.get("attrs").has("styleOverrides")
                && !node.get("attrs").get("styleOverrides").isNull()) {
            JsonNode so = node.get("attrs").get("styleOverrides");
            Iterator<String> fields = so.fieldNames();
            while (fields.hasNext()) {
                String field = fields.next();
                overrides.put(field, so.get(field).asText());
            }
        }
        return overrides;
    }

    private String renderInlineContent(JsonNode node) {
        StringBuilder html = new StringBuilder();
        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode child : content) {
                html.append(renderInlineNode(child));
            }
        }
        return html.toString();
    }

    private String renderInlineNode(JsonNode node) {
        String type = node.has("type") ? node.get("type").asText() : "text";
        if ("hardBreak".equals(type)) {
            return "<br>";
        }
        if ("chart".equals(type)) {
            return renderChart(node);
        }
        if ("map".equals(type)) {
            return renderMap(node);
        }
        if (!node.has("text")) {
            StringBuilder html = new StringBuilder();
            JsonNode content = node.get("content");
            if (content != null && content.isArray()) {
                for (JsonNode child : content) {
                    html.append(renderInlineNode(child));
                }
            }
            return html.toString();
        }
        String text = escapeHtml(node.get("text").asText());
        text = applyMarks(node, text);
        return text;
    }

    private String getStringAttr(JsonNode node, String attrName) {
        if (!node.has("attrs") || !node.get("attrs").has(attrName)
                || node.get("attrs").get(attrName).isNull()) {
            return "";
        }
        return node.get("attrs").get(attrName).asText("");
    }

    private String applyMarks(JsonNode node, String text) {
        if (!node.has("marks")) return text;
        JsonNode marks = node.get("marks");
        for (JsonNode mark : marks) {
            String markType = mark.get("type").asText();
            switch (markType) {
                case "bold":
                    text = "<strong>" + text + "</strong>";
                    break;
                case "italic":
                    text = "<em>" + text + "</em>";
                    break;
                case "underline":
                    text = "<span style=\"text-decoration:underline\">" + text + "</span>";
                    break;
                case "strike":
                    text = "<s>" + text + "</s>";
                    break;
                case "code":
                    text = "<code>" + text + "</code>";
                    break;
                case "link":
                    String href = mark.has("attrs") && mark.get("attrs").has("href")
                            ? mark.get("attrs").get("href").asText() : "";
                    text = "<a href=\"" + escapeHtml(href) + "\">" + text + "</a>";
                    break;
                case "textStyle":
                    if (mark.has("attrs")) {
                        StringBuilder style = new StringBuilder();
                        JsonNode attrs = mark.get("attrs");
                        if (attrs.has("color") && !attrs.get("color").isNull()) {
                            style.append("color:").append(attrs.get("color").asText()).append(";");
                        }
                        if (attrs.has("fontSize") && !attrs.get("fontSize").isNull()) {
                            style.append("font-size:").append(attrs.get("fontSize").asText()).append(";");
                        }
                        if (style.length() > 0) {
                            text = "<span style=\"" + style + "\">" + text + "</span>";
                        }
                    }
                    break;
            }
        }
        return text;
    }

    /**
     * 按优先级获取样式值: styleOverrides > stylePreset > 默认值
     *
     * @param type        元素类型前缀，如 "h2"、"p"、"blockquote"
     * @param key         样式键名，如 "color"、"fontSize"
     * @param overrides   元素级样式覆盖（最优先）
     * @param preset      样式预设（中间优先级）
     * @param defaultValue 默认值（最低优先级）
     */
    private String getStyle(String type, String key, Map<String, Object> overrides,
                            Map<String, Object> preset, String defaultValue) {
        // 优先: styleOverrides > preset > 默认值
        if (overrides.containsKey(key)) {
            return (String) overrides.get(key);
        }
        // 从 preset 中按 "type.key" 格式查询
        String presetKey = type + "." + key;
        if (preset.containsKey(presetKey)) {
            return (String) preset.get(presetKey);
        }
        return defaultValue;
    }

    /**
     * HTML 转义：防止 XSS 注入。
     * 将 < > & " 等特殊字符转为 HTML 实体。
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String wrapSection(String innerHtml) {
        return "<section style=\"padding:10px 16px;font-family:-apple-system,'Microsoft YaHei',sans-serif\">"
                + innerHtml + "</section>";
    }
}
