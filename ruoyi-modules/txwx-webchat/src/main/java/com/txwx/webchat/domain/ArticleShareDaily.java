package com.txwx.webchat.domain;

import com.alibaba.fastjson2.annotation.JSONField;

public class ArticleShareDaily {

    @JSONField(name = "ref_date")
    private String refDate;

    @JSONField(name = "msgid")
    private String msgid;

    @JSONField(name = "detail")
    private ArticleShareDetail detail;

    public String getRefDate() {
        return refDate;
    }

    public void setRefDate(String refDate) {
        this.refDate = refDate;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public ArticleShareDetail getDetail() {
        return detail;
    }

    public void setDetail(ArticleShareDetail detail) {
        this.detail = detail;
    }

    /**
     * @description: 转换为Object数组用于批量插入ClickHouse
     */
    public Object[] toObject() {
        return new Object[]{
            refDate,
            msgid,
            detail != null ? detail.getShareUser() : 0
        };
    }

    /**
     * @description: 分享详情内部类
     */
    public static class ArticleShareDetail {
        @JSONField(name = "share_user")
        private Integer shareUser;

        public Integer getShareUser() {
            return shareUser;
        }

        public void setShareUser(Integer shareUser) {
            this.shareUser = shareUser;
        }
    }

    @Override
    public String toString() {
        return "ArticleShareDaily{" +
                "refDate='" + refDate + '\'' +
                ", msgid='" + msgid + '\'' +
                ", detail=" + detail +
                '}';
    }
}

