package com.txwx.social.crm.publisher.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SpaceMV 问道网站内容格式化器。
 * 将 contentJson（ProseMirror JSON）渲染为语义 HTML + {{chart:slug}} 占位符。
 * 样式由问道平台 CSS 控制，不注入内联样式。
 */
@Slf4j
@Component
public class WendaoFormatter implements IContentFormatter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getTargetCode() {
        return "SITE_WENDAO";
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
                    html.append(renderNode(node));
                }
            }
            return html.toString();
        } catch (JsonProcessingException e) {
            log.error("解析 contentJson 失败: {}", e.getMessage());
            return contentJson;
        }
    }

    @Override
    public String preview(String contentJson, String stylePreset) {
        return format(contentJson, stylePreset);
    }

    private String renderNode(JsonNode node) {
        String type = node.has("type") ? node.get("type").asText() : "paragraph";

        switch (type) {
            case "heading":
                return renderHeading(node);
            case "paragraph":
                return renderParagraph(node);
            case "blockquote":
                return renderBlockquote(node);
            case "bulletList":
                return renderBulletList(node);
            case "orderedList":
                return renderOrderedList(node);
            case "chart":
                return renderChart(node);
            case "image":
                return renderImage(node);
            case "horizontalRule":
                return "<hr>";
            default:
                return renderParagraph(node);
        }
    }

    private String renderHeading(JsonNode node) {
        int level = node.has("attrs") && node.get("attrs").has("level")
                ? node.get("attrs").get("level").asInt(2) : 2;
        String text = extractText(node);
        return String.format("<h%d>%s</h%d>", level, text, level);
    }

    private String renderParagraph(JsonNode node) {
        StringBuilder html = new StringBuilder("<p>");
        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode child : content) {
                if (child.has("text")) {
                    html.append(escapeHtml(child.get("text").asText()));
                } else if (child.has("type")) {
                    String childType = child.get("type").asText();
                    if ("chart".equals(childType)) {
                        html.append(renderChartInline(child));
                    } else if ("map".equals(childType)) {
                        html.append(renderMapInline(child));
                    }
                }
            }
        }
        html.append("</p>");
        return html.toString();
    }

    private String renderBlockquote(JsonNode node) {
        return String.format("<blockquote><p>%s</p></blockquote>", extractText(node));
    }

    private String renderBulletList(JsonNode node) {
        return renderListItems(node, "ul");
    }

    private String renderOrderedList(JsonNode node) {
        return renderListItems(node, "ol");
    }

    private String renderListItems(JsonNode node, String tag) {
        StringBuilder html = new StringBuilder();
        html.append("<").append(tag).append(">");
        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode item : content) {
                html.append("<li>").append(extractText(item)).append("</li>");
            }
        }
        html.append("</").append(tag).append(">");
        return html.toString();
    }

    private String renderChart(JsonNode node) {
        String slug = node.has("attrs") && node.get("attrs").has("slug")
                ? node.get("attrs").get("slug").asText() : "";
        return String.format("<p>{{chart:%s}}</p>", slug);
    }

    private String renderChartInline(JsonNode node) {
        String slug = node.has("attrs") && node.get("attrs").has("slug")
                ? node.get("attrs").get("slug").asText() : "";
        return String.format("{{chart:%s}}", slug);
    }

    private String renderMapInline(JsonNode node) {
        String token = node.has("attrs") && node.get("attrs").has("token")
                ? node.get("attrs").get("token").asText() : "";
        return String.format("{{map:%s}}", token);
    }

    private String renderImage(JsonNode node) {
        String src = node.has("attrs") && node.get("attrs").has("src")
                ? node.get("attrs").get("src").asText() : "";
        String alt = node.has("attrs") && node.get("attrs").has("alt")
                ? node.get("attrs").get("alt").asText() : "";
        return String.format("<img src=\"%s\" alt=\"%s\" style=\"max-width:100%%\">", src, escapeHtml(alt));
    }

    private String extractText(JsonNode node) {
        StringBuilder text = new StringBuilder();
        JsonNode content = node.get("content");
        if (content != null && content.isArray()) {
            for (JsonNode child : content) {
                if (child.has("text")) {
                    text.append(escapeHtml(child.get("text").asText()));
                } else if (child.has("content")) {
                    text.append(extractText(child));
                }
            }
        }
        return text.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
