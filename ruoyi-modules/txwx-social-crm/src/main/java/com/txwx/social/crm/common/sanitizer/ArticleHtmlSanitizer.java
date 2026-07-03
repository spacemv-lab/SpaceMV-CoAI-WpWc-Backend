package com.txwx.social.crm.common.sanitizer;

import com.ruoyi.common.core.utils.html.HTMLFilter;

import java.util.*;

public class ArticleHtmlSanitizer {

    private static final HTMLFilter CONTENT_FILTER;

    static {
        Map<String, List<String>> allowed = new HashMap<>();

        allowed.put("a", Arrays.asList("href", "target", "rel", "style"));
        allowed.put("img", Arrays.asList("src", "width", "height", "alt", "style", "data-chart-slug", "data-wechat-src"));
        allowed.put("span", Arrays.asList("style", "data-chart-slug", "data-map-token", "class"));
        allowed.put("section", Arrays.asList("style", "data-tools", "data-id", "data-role", "data-pm-slice", "class"));
        allowed.put("div", Arrays.asList("style", "class"));

        allowed.put("p", Collections.singletonList("style"));
        allowed.put("h1", Collections.singletonList("style"));
        allowed.put("h2", Collections.singletonList("style"));
        allowed.put("h3", Collections.singletonList("style"));

        allowed.put("b", Collections.singletonList("style"));
        allowed.put("strong", Collections.singletonList("style"));
        allowed.put("i", Collections.singletonList("style"));
        allowed.put("em", Collections.singletonList("style"));
        allowed.put("u", Collections.singletonList("style"));
        allowed.put("s", Collections.singletonList("style"));
        allowed.put("mark", Collections.singletonList("style"));
        allowed.put("code", Collections.singletonList("style"));
        allowed.put("br", Collections.emptyList());
        allowed.put("hr", Collections.emptyList());
        allowed.put("blockquote", Collections.singletonList("style"));
        allowed.put("pre", Collections.singletonList("style"));
        allowed.put("ul", Collections.singletonList("style"));
        allowed.put("ol", Collections.singletonList("style"));
        allowed.put("li", Collections.singletonList("style"));
        allowed.put("table", Arrays.asList("style", "width"));
        allowed.put("tbody", Collections.singletonList("style"));
        allowed.put("thead", Collections.singletonList("style"));
        allowed.put("tr", Collections.singletonList("style"));
        allowed.put("td", Arrays.asList("style", "colspan", "rowspan"));
        allowed.put("th", Arrays.asList("style", "colspan", "rowspan"));

        Map<String, Object> config = new HashMap<>();
        config.put("vAllowed", allowed);
        config.put("vSelfClosingTags", new String[]{"img", "br", "hr"});
        config.put("vNeedClosingTags", new String[]{"a", "b", "strong", "i", "em", "u", "s", "mark", "p", "h1", "h2", "h3", "blockquote", "pre", "code", "span", "li", "section", "div", "ul", "ol", "table", "tbody", "thead", "tr", "td", "th"});
        config.put("vDisallowed", new String[]{});
        config.put("vAllowedProtocols", new String[]{"http", "https", "mailto"});
        config.put("vProtocolAtts", new String[]{"src", "href"});
        config.put("vRemoveBlanks", new String[]{"a", "b", "strong", "i", "em", "u", "span"});
        config.put("vAllowedEntities", new String[]{"amp", "gt", "lt", "quot", "nbsp"});
        config.put("stripComment", true);
        config.put("encodeQuotes", true);
        config.put("alwaysMakeTags", false);

        CONTENT_FILTER = new HTMLFilter(config);
    }

    public static String sanitizeContentHtml(String html) {
        if (html == null) return null;
        return normalizeHtml(CONTENT_FILTER.filter(html));
    }

    public static String stripAllTags(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "").trim();
    }

    public static String normalizeHtml(String html) {
        if (html == null) return null;
        // Fix XssFilter corruption artifact: \" → " within HTML content
        html = html.replace("\\\"", "\"");
        return html;
    }
}
