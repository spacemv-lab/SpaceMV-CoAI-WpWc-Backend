package com.txwx.social.dashboard.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @description: 公众号每天的图文阅读概括数据
 */
@Data
@EqualsAndHashCode
@ToString
public class WebChatUserRead {
    /**
     * @description: 数据的日期
     */
    private String ref_date;

    /**
     * @description: 用户从哪里进入来阅读该图文。99999999.全部；0:会话;1.好友;2.朋友圈;4.历史消息页;5.其他;6.看一看;7.搜一搜；
     */
    private int user_source;

    /**
     * @description: 图文页（点击群发图文卡片进入的页面）的阅读人数
     */
    private int int_page_read_user;

    /**
     * @description: 图文页的阅读次数
     */
    private int int_page_read_count;

    /**
     * @description: 原文页（点击图文页“阅读原文”进入的页面）的阅读人数，无原文页时此处数据为0
     */
    private int ori_page_read_user;

    /**
     * @description: 原文页的阅读次数
     */
    private int ori_page_read_count;

    /**
     * @description: 分享的人数
     */
    private int share_user;

    /**
     * @description: 分享的次数
     */
    private int share_count;

    /**
     * @description: 收藏的人数
     */
    private int add_to_fav_user;

    /**
     * @description: 收藏的次数
     */
    private int add_to_fav_count;

    /**
     * 所属自媒体账号id
     */
    private Long account_id;

    public Object[] toObject(Long account_id){
        return new Object[]{ref_date, user_source, int_page_read_user, int_page_read_count, ori_page_read_user, ori_page_read_count, share_user, share_count, add_to_fav_user, add_to_fav_count, account_id };
    }

}
