package com.txwx.social.dashboard.domain;

import com.txwx.social.dashboard.domain.entity.DwsBizsummaryChannelDaily;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 发表内容概况总数据
 */
@Data
public class ArticleSummaryDaily {

    /**
     * @description: 统计日期
     */
    private LocalDate ref_date;

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
         * @description: 阅读人数
         */
        private int read_user;

        /**
         * @description: 阅读数据（包含来源）
         */
        private List<ReadUserSource> read_user_source;

        /**
         * @description: 分享人数
         */
        private Long share_user;

        /**
         * @description: 爱心赞人数
         */
        private int zaikan_user;

        /**
         * @description: 拇指赞人数
         */
        private int like_user;

        /**
         * @description: 留言条数
         */
        private int comment_count;

        /**
         * @description: 微信收藏人数
         */
        private Long collection_user;

        /**
         * @description: 跳转原文人数
         */
        private Long redirect_ori_page_user;

        /**
         * @description: 发布篇数
         */
        private Long send_page_count;
    }

    @Data
    public static class ReadUserSource {
        private Long user_count;
        private String scene_desc;
    }

    public Object[] toObject(Long platformId, Long productId) {
        if (detail == null) {
            return new Object[]{
                ref_date,
                0, 0, 0, 0, 0, 0, 0, 0, 0,     // read_user (9个字段: total + 8个来源)
                0,                             // share_user
                0,                             // zaikan_user
                0,                             // like_user
                0,                             // comment_count
                0,                             // collection_user
                0,                             // redirect_ori_page_user
                0,                             // send_page_count
                0,                              // platform_id
                0                               // product_id
            };
        }

        // 初始化阅读来源字段
        Long readUserAll = 0L;
        Long readUserMsg = 0L;
        Long readUserChat = 0L;
        Long readUserMoments = 0L;
        Long readUserHomepage = 0L;
        Long readUserOther = 0L;
        Long readUserRecommend = 0L;
        Long readUserSearch = 0L;

        // 解析read_user_source数组
        if (detail.getRead_user_source() != null) {
            for (ReadUserSource source : detail.getRead_user_source()) {
                String sceneDesc = source.getScene_desc();
                Long userCount = source.getUser_count();

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
            detail.getRead_user(),           // read_user_total
            readUserAll,                     // read_user_source_all
            readUserMsg,                     // read_user_source_msg
            readUserChat,                    // read_user_source_chat
            readUserMoments,                 // read_user_source_moments
            readUserHomepage,                // read_user_source_homepage
            readUserOther,                   // read_user_source_other
            readUserRecommend,               // read_user_source_recommend
            readUserSearch,                  // read_user_source_search
            detail.getShare_user(),           // share_user
            detail.getZaikan_user(),          // zaikan_user
            detail.getLike_user(),            // like_user
            detail.getComment_count(),        // comment_count
            detail.getCollection_user(),       // collection_user
            detail.getRedirect_ori_page_user(), // redirect_ori_page_user
            detail.getSend_page_count(),       // send_page_count
            platformId,                         // platform_id
            productId
        };
    }

    public List<DwsBizsummaryChannelDaily> toDwsContentData(Long platformId, Long productId) {
        List<DwsBizsummaryChannelDaily> res = new ArrayList<>();
        if (detail != null && detail.getRead_user_source() != null) {
            for (ReadUserSource source : detail.getRead_user_source()) {
                if (source.getScene_desc().equals("全部")) {
                    res.add(new DwsBizsummaryChannelDaily(
                            ref_date,
                            source.getUser_count(),
                            detail.getShare_user(),
                            0L,
                            detail.getRedirect_ori_page_user(),
                            0L,
                            detail.getCollection_user(),
                            detail.getSend_page_count(),
                            source.getScene_desc(),
                            platformId,
                            productId
                    ));
                }else {
                    res.add(new DwsBizsummaryChannelDaily(
                            ref_date,
                            source.getUser_count(),
                            0L,
                            0L,
                            0L,
                            0L,
                            0L,
                            0L,
                            source.getScene_desc(),
                            platformId,
                            productId
                    ));
                }
            }
        }
        return res;
    }

    public String toSting() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("ref_date", getRef_date())
                .append("read_user", detail != null ? detail.getRead_user() : 0)
                .append("share_user", detail != null ? detail.getShare_user() : 0)
                .append("zaikan_user", detail != null ? detail.getZaikan_user() : 0)
                .append("like_user", detail != null ? detail.getLike_user() : 0)
                .append("comment_count", detail != null ? detail.getComment_count() : 0)
                .append("collection_user", detail != null ? detail.getCollection_user() : 0)
                .append("redirect_ori_page_user", detail != null ? detail.getRedirect_ori_page_user() : 0)
                .append("send_page_count", detail != null ? detail.getSend_page_count() : 0)
                .toString();
    }
}
