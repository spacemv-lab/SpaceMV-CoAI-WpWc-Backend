package com.ruoyi.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户通道关联实体
 *
 * @author txwx
 */
@Data
@TableName("iam_user_channel")
public class IamUserChannel implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 iam_user.id */
    private Long userId;

    /** 产品线标识（服务端注入） */
    private String productLine;

    /** phone/email/wechat/dingtalk/feishu */
    private String channelType;

    /** 通道账号（手机号/邮箱/OpenID） */
    private String channelAccount;

    /** 1=主通道 0=副通道 */
    private String isPrimary;

    /** 第三方用户信息（JSON，MVP始终null） */
    private String channelUserInfo;

    /** 绑定时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date bindTime;

    /** 0=正常 1=解绑 */
    private String status;

    /** 逻辑删除 */
    @TableLogic
    private String delFlag;
}
