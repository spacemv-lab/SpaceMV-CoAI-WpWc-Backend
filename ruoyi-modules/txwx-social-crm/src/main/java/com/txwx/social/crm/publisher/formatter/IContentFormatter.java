package com.txwx.social.crm.publisher.formatter;

/**
 * 内容格式化接口。
 * 将 contentJson（ProseMirror JSON）渲染为各平台原生格式。
 * 新增平台只需新增实现类。
 */
public interface IContentFormatter {

    /**
     * 获取支持的目标平台编码。
     */
    String getTargetCode();

    /**
     * 将 contentJson 渲染为输出字符串。
     *
     * @param contentJson ProseMirror JSON
     * @param stylePreset 样式预设名（可为 null）
     * @return 格式化后的内容
     */
    String format(String contentJson, String stylePreset);

    /**
     * 获取预览内容。
     */
    default String preview(String contentJson, String stylePreset) {
        return format(contentJson, stylePreset);
    }
}
