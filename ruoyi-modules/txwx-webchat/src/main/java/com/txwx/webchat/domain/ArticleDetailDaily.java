package com.txwx.webchat.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 发表内容发表详细数据
 */
@Data
@ExcelIgnoreUnannotated
public class ArticleDetailDaily {

    private String ref_date;

    private String msgid;

    private Integer publish_type;

    private String title;

    private String content_url;

    private List<DetailList> detail_list;

    @Data
    public static class DetailList {

        private String stat_date;
        private Integer read_user;
        private List<ArticleReadDaily.ReadUserSource> read_user_source;
        private Integer share_user;
        private Integer zaikan_user;
        private Integer like_user;
        private Integer comment_count;
        private Integer collection_user;
        private Integer praise_money;
        private Integer read_subscribe_user;
        private Float read_delivery_rate;
        private Float read_finish_rate;
        private Float read_avg_activetime;
        private List<ReadJumpPosition> read_jump_position;

        /**
         * 将单日明细转换为 ClickHouse 的一行 (23个字段)
         */
        public Object[] toClickHouseRow(String refDate, String msgId, Integer pubType, String title, String url) {
            int[] sources = new int[8]; // all, msg, chat, moments, home, other, rec, search
            if (this.read_user_source != null) {
                for (ArticleReadDaily.ReadUserSource source : this.read_user_source) {
                    String desc = source.getScene_desc();
                    int count = source.getUser_count();
                    if (desc == null) continue;
                    switch (desc) {
                        case "全部": sources[0] = count; break;
                        case "公众号消息": sources[1] = count; break;
                        case "聊天会话": sources[2] = count; break;
                        case "朋友圈": sources[3] = count; break;
                        case "公众号主页": sources[4] = count; break;
                        case "其他": sources[5] = count; break;
                        case "推荐": sources[6] = count; break;
                        case "搜一搜": sources[7] = count; break;
                    }
                }
            }

            return new Object[]{
                    this.stat_date,        // 1
                    refDate,               // 2
                    msgId,                 // 3
                    pubType,               // 4
                    this.read_user,        // 5
                    sources[0], sources[1], sources[2], sources[3], // 6,7,8,9
                    sources[4], sources[5], sources[6], sources[7], // 10,11,12,13
                    this.share_user,       // 14
                    this.zaikan_user,      // 15
                    this.like_user,        // 16
                    this.comment_count,    // 17
                    this.collection_user,  // 18
                    this.praise_money,     // 19
                    this.read_subscribe_user, // 20
                    this.read_delivery_rate,  // 21
                    this.read_finish_rate,    // 22
                    this.read_avg_activetime,  // 23
                    title,
                    url,
            };
        }
    }

    @Data
    public static class ReadJumpPosition {
        private Integer position;
        private Float rate;
    }

    public List<Object[]> toFlattenObjectList() {
        List<Object[]> rows = new ArrayList<>();
        if (detail_list == null || detail_list.isEmpty()) {
            return rows;
        }

        for (DetailList detail : detail_list) {
            rows.add(detail.toClickHouseRow(this.ref_date, this.msgid, this.publish_type, this.title, this.content_url));
        }
        return rows;
    }

    public String toSting() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("ref_date", getRef_date())
                .append("msgid", getMsgid())
                .append("publish_type", getPublish_type())
                .toString();
    }
}
