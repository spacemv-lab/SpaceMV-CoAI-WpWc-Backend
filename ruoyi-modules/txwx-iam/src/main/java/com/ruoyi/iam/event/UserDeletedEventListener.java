package com.ruoyi.iam.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.iam.entity.IamUserBackupContact;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.entity.IamUserProduct;
import com.ruoyi.iam.mapper.IamUserBackupContactMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import com.ruoyi.system.api.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 用户注销事件消费者
 * <p>
 * 监听 UserDeletedEvent，通过 Feign RPC 同步删除 sys_user。
 * 事件由 DeactivateService.processExpiredDeactivations() 在冷静期到期后发布。
 * 通过 iam_user_product 映射表查询对应的 sys_user.userId，调用 system 模块删除。
 * 与注册同步使用同一种 Feign + @InnerAuth 模式，保持一致性。
 *
 * @author txwx
 */
@Slf4j
@Component
public class UserDeletedEventListener
{
    private final IamUserProductMapper iamUserProductMapper;
    private final IamUserChannelMapper iamUserChannelMapper;
    private final IamUserBackupContactMapper iamUserBackupContactMapper;
    private final RemoteUserService remoteUserService;

    public UserDeletedEventListener(IamUserProductMapper iamUserProductMapper,
                                    IamUserChannelMapper iamUserChannelMapper,
                                    IamUserBackupContactMapper iamUserBackupContactMapper,
                                    RemoteUserService remoteUserService)
    {
        this.iamUserProductMapper = iamUserProductMapper;
        this.iamUserChannelMapper = iamUserChannelMapper;
        this.iamUserBackupContactMapper = iamUserBackupContactMapper;
        this.remoteUserService = remoteUserService;
    }

    @EventListener
    public void handleUserDeleted(UserDeletedEvent event)
    {
        try
        {
            Long userId = event.getUserId();
            Long productUserId = null;

            // 先查映射表，避免后面软删后查不到
            LambdaQueryWrapper<IamUserProduct> mappingQuery = new LambdaQueryWrapper<>();
            mappingQuery.eq(IamUserProduct::getIamUserId, userId);
            IamUserProduct mapping = iamUserProductMapper.selectOne(mappingQuery);
            if (mapping != null && mapping.getProductUserId() != null)
            {
                productUserId = mapping.getProductUserId();
            }

            // 批量软删关联数据（使用专用 XML 方法，绕过 @TableLogic 不更新 del_flag 的问题）
            int cc = iamUserChannelMapper.softDeleteByUserId(userId);
            int bc = iamUserBackupContactMapper.softDeleteByUserId(userId);
            int pc = iamUserProductMapper.softDeleteByIamUserId(userId);

            log.info("UserDeletedEvent: 关联表软删完成, userId={}, channel={}, backupContact={}, product={}",
                userId, cc, bc, pc);

            // 同步删除 sys_user
            if (productUserId == null)
            {
                log.warn("UserDeletedEvent: 未找到 iam_user_id={} 的映射记录，跳过 sys_user 删除", userId);
                return;
            }

            R<Boolean> result = remoteUserService.deleteIamUser(productUserId, SecurityConstants.INNER);
            if (R.isSuccess(result))
            {
                log.info("UserDeletedEvent: sys_user 删除成功, iamUserId={}, productUserId={}",
                    userId, productUserId);
            }
            else
            {
                log.warn("UserDeletedEvent: sys_user 删除返回失败, iamUserId={}, productUserId={}, msg={}",
                    userId, productUserId, result != null ? result.getMsg() : "null");
            }
        }
        catch (Exception e)
        {
            log.error("UserDeletedEvent: 处理异常, iamUserId={}", event.getUserId(), e);
        }
    }
}
