package com.ruoyi.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.iam.entity.IamUserChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户通道 Mapper 接口
 *
 * @author txwx
 */
@Mapper
public interface IamUserChannelMapper extends BaseMapper<IamUserChannel>
{
    /** 按通道账号查询 */
    IamUserChannel selectByChannel(
        @Param("productLine") String productLine,
        @Param("channelType") String channelType,
        @Param("channelAccount") String channelAccount);

    /** 按用户ID查询所有活跃通道 */
    List<IamUserChannel> selectByUserId(@Param("userId") Long userId);

    /** 按用户ID+产品线+通道类型查询 */
    IamUserChannel selectByUserIdAndType(
        @Param("userId") Long userId,
        @Param("productLine") String productLine,
        @Param("channelType") String channelType);

    int insert(IamUserChannel channel);

    int unbind(@Param("id") Long id);

    int updateIsPrimary(@Param("id") Long id);

    /**
     * 按用户ID软删（硬编码SQL绕过 @TableLogic 跳过 del_flag 的问题）
     */
    int softDeleteByUserId(@Param("userId") Long userId);
}
