package com.txwx.social.dashboard.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @description: 发表内容每日阅读数据
 */
@Data
public class ArticleReadDaily {

    /**
     * @description: 统计日期
     */
    private String ref_date;

    /**
     * @description: 消息ID (msgid_index格式)
     */
    private String msgid;

    /**
     * @description: 每篇文章的详细数据
     */
    private Detail detail;

    /**
     * @description: 文章详细数据
     */
    @Data
    public static class Detail {
        /**
         * @description: 阅读人数（即read_user_source里面来源为全部的阅读人数）
         */
        private int read_user;

        /**
         * @description: 阅读数据（包含来源）
         */
        private List<ReadUserSource> read_user_source;
    }

    /**
     * @description: 阅读来源数据
     */
    @Data
    public static class ReadUserSource {
        /**
         * @description: 阅读人数
         */
        private int user_count;

        /**
         * @description: 阅读场景来源。包含如下场景：全部、公众号消息、聊天会话、朋友圈、公众号主页、其他、推荐、搜一搜
         */
        private String scene_desc;
    }

    public Object[] toObject() {
        if (detail == null) {
            return new Object[]{ref_date, msgid, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        // 初始化各来源字段
        int readUserAll = 0;
        int readUserMsg = 0;
        int readUserChat = 0;
        int readUserMoments = 0;
        int readUserHomepage = 0;
        int readUserOther = 0;
        int readUserRecommend = 0;
        int readUserSearch = 0;

        // 解析read_user_source数组
        if (detail.getRead_user_source() != null) {
            for (ReadUserSource source : detail.getRead_user_source()) {
                String sceneDesc = source.getScene_desc();
                int userCount = source.getUser_count();

                if (sceneDesc != null) {
                    switch (sceneDesc) {
                        case "全部":
                            readUserAll = userCount;
                            break;
                        case "公众号消息":
                            readUserMsg = userCount;
                            break;
                        case "聊天会话":
                            readUserChat = userCount;
                            break;
                        case "朋友圈":
                            readUserMoments = userCount;
                            break;
                        case "公众号主页":
                            readUserHomepage = userCount;
                            break;
                        case "其他":
                            readUserOther = userCount;
                            break;
                        case "推荐":
                            readUserRecommend = userCount;
                            break;
                        case "搜一搜":
                            readUserSearch = userCount;
                            break;
                    }
                }
            }
        }

        return new Object[]{
            ref_date,
            msgid,
            detail.getRead_user(),           // read_user_total
            readUserAll,                     // read_user_source_all
            readUserMsg,                     // read_user_source_msg
            readUserChat,                    // read_user_source_chat
            readUserMoments,                 // read_user_source_moments
            readUserHomepage,                // read_user_source_homepage
            readUserOther,                   // read_user_source_other
            readUserRecommend,               // read_user_source_recommend
            readUserSearch                   // read_user_source_search
        };
    }

    public String toSting() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("ref_date", getRef_date())
                .append("msgid", getMsgid())
                .append("read_user", detail != null ? detail.getRead_user() : 0)
                .toString();
    }
}
