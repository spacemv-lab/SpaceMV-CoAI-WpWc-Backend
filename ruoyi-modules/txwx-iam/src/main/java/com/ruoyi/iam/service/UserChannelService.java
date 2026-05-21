package com.ruoyi.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.iam.dto.ChannelResponse;
import com.ruoyi.iam.dto.response.BackupContactResponse;
import com.ruoyi.iam.entity.IamUserProduct;
import com.ruoyi.iam.entity.IamUserBackupContact;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamUserBackupContactMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户通道管理
 *
 * @author txwx
 */
@Slf4j
@Service
public class UserChannelService
{
    private static final String PRODUCT_LINE = "spacemv-coai";

    private final IamUserChannelMapper channelMapper;
    private final IamUserBackupContactMapper backupContactMapper;
    private final VerifyCodeService verifyCodeService;
    private final IamUserProductMapper iamUserProductMapper;
    private final RemoteUserService remoteUserService;

    public UserChannelService(IamUserChannelMapper channelMapper,
                              IamUserBackupContactMapper backupContactMapper,
                              VerifyCodeService verifyCodeService,
                              IamUserProductMapper iamUserProductMapper,
                              RemoteUserService remoteUserService)
    {
        this.channelMapper = channelMapper;
        this.backupContactMapper = backupContactMapper;
        this.verifyCodeService = verifyCodeService;
        this.iamUserProductMapper = iamUserProductMapper;
        this.remoteUserService = remoteUserService;
    }

    /**
     * 绑定通道
     */
    public void bindChannel(Long userId, String channelType, String channelAccount, String verifyCode)
    {
        // 1. 验证码校验
        if (!verifyCodeService.verifyCode(channelType, channelAccount, verifyCode))
        {
            throw new ServiceException("验证码错误或已过期");
        }

        // 2. 该用户该类型检查
        LambdaQueryWrapper<IamUserChannel> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(IamUserChannel::getUserId, userId)
            .eq(IamUserChannel::getChannelType, channelType)
            .eq(IamUserChannel::getStatus, "0");
        if (channelMapper.selectCount(userWrapper) > 0)
        {
            throw new ServiceException("您已绑定该类型通道");
        }

        // 3. 全局唯一性检查
        LambdaQueryWrapper<IamUserChannel> globalWrapper = new LambdaQueryWrapper<>();
        globalWrapper.eq(IamUserChannel::getProductLine, PRODUCT_LINE)
            .eq(IamUserChannel::getChannelType, channelType)
            .eq(IamUserChannel::getChannelAccount, channelAccount)
            .eq(IamUserChannel::getStatus, "0");
        if (channelMapper.selectCount(globalWrapper) > 0)
        {
            throw new ServiceException("该通道已被注册");
        }

        // 4. 创建通道
        IamUserChannel channel = new IamUserChannel();
        channel.setUserId(userId);
        channel.setProductLine(PRODUCT_LINE);
        channel.setChannelType(channelType);
        channel.setChannelAccount(channelAccount);
        channel.setIsPrimary("0");
        channel.setStatus("0");

        channelMapper.insert(channel);

        // 5. 同步到 sys_user（Feign RPC）
        syncChannelToSysUser(userId, channelType, channelAccount);
    }

    /**
     * 解绑通道（v1.1: email 双验证）
     */
    public void unbindChannel(Long userId, String channelType, String verifyCode, String password)
    {
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getUserId, userId)
            .eq(IamUserChannel::getChannelType, channelType)
            .eq(IamUserChannel::getStatus, "0");
        IamUserChannel channel = channelMapper.selectOne(wrapper);

        if (channel == null)
        {
            throw new ServiceException("通道不存在");
        }

        // 按类型区分验证方式
        if ("phone".equals(channelType))
        {
            if (!verifyCodeService.verifyCode("phone", channel.getChannelAccount(), verifyCode))
            {
                throw new ServiceException("验证码错误");
            }
        }
        else if ("email".equals(channelType))
        {
            // v1.1: email 解绑需要双验证（密码 + 邮箱验证码）
            if (!verifyCodeService.verifyCode("email", channel.getChannelAccount(), verifyCode))
            {
                throw new ServiceException("邮箱验证码错误");
            }
            if (password == null || password.isBlank())
            {
                throw new ServiceException("解绑邮箱需要密码验证");
            }
            // 密码校验由 Controller 层委托 AuthService 完成，避免循环依赖
        }

        // 如果是主通道解绑，需要检查是否有其他通道
        if ("1".equals(channel.getIsPrimary()))
        {
            LambdaQueryWrapper<IamUserChannel> otherWrapper = new LambdaQueryWrapper<>();
            otherWrapper.eq(IamUserChannel::getUserId, userId)
                .ne(IamUserChannel::getId, channel.getId())
                .eq(IamUserChannel::getStatus, "0");
            long otherCount = channelMapper.selectCount(otherWrapper);
            if (otherCount == 0)
            {
                throw new ServiceException("主通道不能单独解绑，请先绑定其他通道");
            }
        }

        channel.setStatus("1");
        channelMapper.updateById(channel);

        // 解绑后清空 sys_user 对应字段（Feign RPC）
        syncChannelToSysUser(userId, channelType, null);
    }

    /**
     * 查询通道列表（脱敏）
     */
    public List<ChannelResponse> listChannels(Long userId)
    {
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getUserId, userId)
            .eq(IamUserChannel::getStatus, "0");
        List<IamUserChannel> channels = channelMapper.selectList(wrapper);

        if (channels == null || channels.isEmpty())
        {
            return java.util.Collections.emptyList();
        }

        return channels.stream().map(ch -> {
            ChannelResponse resp = new ChannelResponse();
            resp.setId(ch.getId());
            resp.setChannelType(ch.getChannelType());
            resp.setChannelAccount(maskChannel(ch.getChannelType(), ch.getChannelAccount()));
            resp.setIsPrimary(ch.getIsPrimary());
            resp.setBindTime(ch.getBindTime() != null ? ch.getBindTime().toString() : null);
            resp.setStatus(ch.getStatus());
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 查询备用联系方式（v1.1 新增）
     */
    public BackupContactResponse queryBackupContact(Long userId)
    {
        IamUserBackupContact backup = backupContactMapper.selectByUserId(userId);
        if (backup == null || (backup.getBakPhone() == null && backup.getBakEmail() == null))
        {
            return null;
        }
        BackupContactResponse resp = new BackupContactResponse();
        resp.setBakPhone(maskChannel("phone", backup.getBakPhone()));
        resp.setRawBakPhone(backup.getBakPhone());  // 原始值
        resp.setBakEmail(maskChannel("email", backup.getBakEmail()));
        resp.setRawBakEmail(backup.getBakEmail());  // 原始值
        resp.setHasBakPhone(backup.getBakPhone() != null);
        resp.setHasBakEmail(backup.getBakEmail() != null);
        return resp;
    }

    /**
     * 通道绑定/解绑后同步 sys_user 的 phonenumber / email 字段
     * <p>
     * 通过 iam_user_product 映射表查询 sys_user.userId，
     * 使用 Feign RPC 调用 system 模块更新。
     * 解绑时 account 传 null，表示清空该字段。
     */
    private void syncChannelToSysUser(Long userId, String channelType, String channelAccount)
    {
        try
        {
            // 查询映射表
            LambdaQueryWrapper<IamUserProduct> query = new LambdaQueryWrapper<>();
            query.eq(IamUserProduct::getIamUserId, userId);
            IamUserProduct mapping = iamUserProductMapper.selectOne(query);

            if (mapping == null || mapping.getProductUserId() == null)
            {
                log.warn("通道同步 sys_user 跳过: 未找到 iam_user_id={} 的映射记录", userId);
                return;
            }

            // 只处理 phone/email 类型通道
            if (!"phone".equals(channelType) && !"email".equals(channelType))
            {
                return;
            }

            SysUser sysUser = new SysUser();
            sysUser.setUserId(mapping.getProductUserId());
            if ("phone".equals(channelType))
            {
                sysUser.setPhonenumber(channelAccount);
            }
            else if ("email".equals(channelType))
            {
                sysUser.setEmail(channelAccount);
            }

            R<Boolean> result = remoteUserService.updateProfile(sysUser, SecurityConstants.INNER);
            if (R.isSuccess(result))
            {
                log.info("通道同步 sys_user 成功: userId={}, type={}, account={}",
                    userId, channelType, channelAccount);
            }
            else
            {
                log.warn("通道同步 sys_user 返回失败: userId={}, type={}, msg={}",
                    userId, channelType, result != null ? result.getMsg() : "null");
            }
        }
        catch (Exception e)
        {
            log.error("通道同步 sys_user 异常: userId={}, type={}", userId, channelType, e);
        }
    }

    private String maskChannel(String type, String account)
    {
        if ("phone".equals(type) && account != null && account.length() > 7)
        {
            return account.substring(0, 3) + "****" + account.substring(account.length() - 4);
        }
        if ("email".equals(type) && account != null && account.contains("@"))
        {
            String local = account.substring(0, account.indexOf("@"));
            if (local.length() > 1)
            {
                return local.charAt(0) + "**" + account.substring(account.indexOf("@"));
            }
        }
        return account;
    }
}
