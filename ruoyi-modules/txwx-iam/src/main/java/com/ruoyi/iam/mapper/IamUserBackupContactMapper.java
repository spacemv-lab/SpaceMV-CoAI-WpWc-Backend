package com.ruoyi.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.iam.entity.IamUserBackupContact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户备用联系方式 Mapper
 *
 * @author txwx
 */
@Mapper
public interface IamUserBackupContactMapper extends BaseMapper<IamUserBackupContact>
{
    /**
     * 按用户ID查询备用联系方式
     */
    IamUserBackupContact selectByUserId(@Param("userId") Long userId);

    /**
     * 按用户ID软删（硬编码SQL绕过 @TableLogic 跳过 del_flag 的问题）
     */
    int softDeleteByUserId(@Param("userId") Long userId);
}
