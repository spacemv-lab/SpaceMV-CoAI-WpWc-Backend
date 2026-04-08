package com.txwx.social.dashboard.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @description: 公众号每天的关注和取消关注人数
 */
@Data
public class WebChatUser {

    /**
     * @description: 数据的日期
     */
    private String ref_date;

    /**
     * @description: 用户的渠道，数值代表的含义如下：0代表其他合计、1代表公众号搜索、
     * 17代表名片分享、30代表扫描二维码、57代表文章内账号名称、100代表微信广告、161代表他人转载、
     * 149代表小程序关注、200代表视频号、201代表直播
     */
    private int user_source;

    /**
     * @description: 新增的用户数量
     */
    private int new_user;

    /**
     * @description: 取消关注的用户数量
     */
    private int cancel_user;

    public Object[] toObject(Long platformId, Long productId){
        return new Object[]{ref_date, user_source, new_user, cancel_user, platformId, productId};
    }

    public String toSting(){
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("refDate", getRef_date())
                .append("userSource", getUser_source())
                .append("newUser", getNew_user())
                .append("cancelUser", getCancel_user())
                .toString();
    }
}
