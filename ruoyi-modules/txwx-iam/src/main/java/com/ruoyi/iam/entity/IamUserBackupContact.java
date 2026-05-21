package com.ruoyi.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户备用联系方式
 *
 * @author txwx
 */
@Data
@TableName("iam_user_backup_contact")
public class IamUserBackupContact implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 备用手机号 */
    private String bakPhone;

    /** 备用邮箱 */
    private String bakEmail;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 逻辑删除 */
    @TableLogic
    private String delFlag;
}
