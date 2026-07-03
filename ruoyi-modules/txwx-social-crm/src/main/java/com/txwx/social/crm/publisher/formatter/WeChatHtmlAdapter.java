package com.txwx.social.crm.publisher.formatter;

import com.txwx.social.crm.common.sanitizer.ArticleHtmlSanitizer;
import com.txwx.social.crm.domain.content.ContentArticle;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WeChatHtmlAdapter {

    private static final Pattern CHART_SHORTCODE_PATTERN = Pattern.compile("\\{\\{chart:([a-zA-Z0-9-_]+)}}");
    private static final Pattern MAP_SHORTCODE_PATTERN = Pattern.compile("\\{\\{map:([a-zA-Z0-9-_]+)}}");
    private static final Pattern CHART_SPAN_PATTERN = Pattern.compile(
            "<span[^>]*data-chart-slug\\s*=\\s*[\"']([a-zA-Z0-9-_]+)[\"'][^>]*>.*?</span>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern MAP_SPAN_PATTERN = Pattern.compile(
            "<span[^>]*data-map-token\\s*=\\s*[\"']([a-zA-Z0-9-_]+)[\"'][^>]*>.*?</span>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern STYLE_ATTR_PATTERN = Pattern.compile("style\\s*=\\s*(['\"])(.*?)\\1", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EMPTY_STYLE_PATTERN = Pattern.compile("\\sstyle\\s*=\\s*(['\"])\\s*\\1", Pattern.CASE_INSENSITIVE);

    public String adapt(ContentArticle article, Map<String, String> chartImageMap, Map<String, String> mapImageMap) {
        String html = ArticleHtmlSanitizer.normalizeHtml(article.getContentHtml());
        if (html == null || html.isBlank()) {
            return "";
        }

        String adapted = html;
        adapted = replaceChartSpans(adapted, chartImageMap);
        adapted = replaceChartShortcodes(adapted, chartImageMap);
        adapted = replaceMapSpans(adapted, mapImageMap);
        adapted = replaceMapShortcodes(adapted, mapImageMap);
        adapted = normalizeInlineStyles(adapted);
        adapted = ArticleHtmlSanitizer.sanitizeContentHtml(adapted);
        return wrapSection(adapted);
    }

    private String replaceChartSpans(String html, Map<String, String> chartImageMap) {
        Matcher matcher = CHART_SPAN_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String slug = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(buildChartHtml(slug, chartImageMap.get(slug))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replaceChartShortcodes(String html, Map<String, String> chartImageMap) {
        Matcher matcher = CHART_SHORTCODE_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String slug = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(buildChartHtml(slug, chartImageMap.get(slug))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replaceMapSpans(String html, Map<String, String> mapImageMap) {
        Matcher matcher = MAP_SPAN_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(buildMapHtml(token, mapImageMap.get(token))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replaceMapShortcodes(String html, Map<String, String> mapImageMap) {
        Matcher matcher = MAP_SHORTCODE_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(buildMapHtml(token, mapImageMap.get(token))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String buildMapHtml(String token, String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return "<section style=\"margin:16px 0;text-align:center\">" +
                    "<img src=\"" + escapeHtml(imageUrl) + "\" alt=\"" + escapeHtml(token) + "\" " +
                    "style=\"max-width:100%;height:auto;display:block;margin:0 auto;border-radius:4px;\">" +
                    "</section>";
        }
        return "<section style=\"border:1px solid #e8e8e8;border-radius:8px;padding:16px;text-align:center;margin:16px 0\">" +
                "<span style=\"color:#548DD4;font-weight:600\">查看地图</span>" +
                "<br><span style=\"color:#999;font-size:12px\">" + escapeHtml(token) + "</span>" +
                "</section>";
    }

    private String buildChartHtml(String slug, String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return "<section style=\"margin:16px 0;text-align:center\">" +
                    "<img src=\"" + escapeHtml(imageUrl) + "\" alt=\"" + escapeHtml(slug) + "\" " +
                    "style=\"max-width:100%;height:auto;display:block;margin:0 auto;border-radius:4px;\">" +
                    "</section>";
        }
        return "<section style=\"border:1px solid #e8e8e8;border-radius:8px;padding:16px;text-align:center;margin:16px 0\">" +
                "<span style=\"color:#548DD4;font-weight:600\">查看图表</span>" +
                "<br><span style=\"color:#999;font-size:12px\">" + escapeHtml(slug) + "</span>" +
                "</section>";
    }

    private String normalizeInlineStyles(String html) {
        Matcher matcher = STYLE_ATTR_PATTERN.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String normalized = filterStyleDeclarations(matcher.group(2));
            matcher.appendReplacement(sb, Matcher.quoteReplacement("style=\"" + normalized + "\""));
        }
        matcher.appendTail(sb);
        return EMPTY_STYLE_PATTERN.matcher(sb.toString()).replaceAll("");
    }

    private String filterStyleDeclarations(String rawStyle) {
        if (rawStyle == null || rawStyle.isBlank()) {
            return "";
        }
        StringBuilder style = new StringBuilder();
        String[] declarations = rawStyle.split(";");
        for (String declaration : declarations) {
            String trimmed = declaration == null ? "" : declaration.trim();
            if (trimmed.isEmpty() || !trimmed.contains(":")) {
                continue;
            }
            String property = trimmed.substring(0, trimmed.indexOf(':')).trim().toLowerCase();
            if (isAllowedStyleProperty(property)) {
                if (style.length() > 0) {
                    style.append(';');
                }
                style.append(trimmed);
            }
        }
        return style.toString();
    }

    private boolean isAllowedStyleProperty(String property) {
        return property.equals("color")
                || property.equals("background")
                || property.equals("background-color")
                || property.equals("font-size")
                || property.equals("font-weight")
                || property.equals("font-style")
                || property.equals("text-decoration")
                || property.equals("text-align")
                || property.equals("line-height")
                || property.equals("letter-spacing")
                || property.equals("margin")
                || property.equals("margin-top")
                || property.equals("margin-right")
                || property.equals("margin-bottom")
                || property.equals("margin-left")
                || property.equals("padding")
                || property.equals("padding-top")
                || property.equals("padding-right")
                || property.equals("padding-bottom")
                || property.equals("padding-left")
                || property.equals("border")
                || property.equals("border-top")
                || property.equals("border-right")
                || property.equals("border-bottom")
                || property.equals("border-left")
                || property.equals("border-radius")
                || property.equals("display")
                || property.equals("width")
                || property.equals("min-width")
                || property.equals("max-width")
                || property.equals("height")
                || property.equals("min-height")
                || property.equals("max-height")
                || property.equals("box-sizing")
                || property.equals("border-collapse")
                || property.equals("border-spacing")
                || property.equals("vertical-align");
    }

    private String wrapSection(String innerHtml) {
        return "<section style=\"padding:10px 16px;font-family:-apple-system,'Microsoft YaHei',sans-serif\">"
                + innerHtml + "</section>";
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
