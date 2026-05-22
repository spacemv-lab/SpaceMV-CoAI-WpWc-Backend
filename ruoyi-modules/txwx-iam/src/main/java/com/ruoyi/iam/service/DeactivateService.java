/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.sign.RsaUtils;
import com.ruoyi.iam.dto.DeactivateStatusResponse;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.mapper.IamUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户注销管理
 *
 * @author txwx
 */
@Slf4j
@Service
public class DeactivateService
{
    private static final int COOLDOWN_DAYS = 7;
    private static final String SCHEDULER_LOCK_KEY = "iam:scheduler:deactivate:lock";
    private static final long LOCK_TTL_SECONDS = 120;

    private final IamUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;
    private final StringRedisTemplate redisTemplate;

    public DeactivateService(IamUserMapper userMapper, PasswordEncoder passwordEncoder,
                             UserEventPublisher userEventPublisher,
                             StringRedisTemplate redisTemplate)
    {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 提交注销申请
     */
    @Transactional
    public void submitDeactivation(Long userId, String password)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        if (!"0".equals(user.getStatus()))
        {
            throw new ServiceException("账号已停用");
        }
        if (!"0".equals(user.getDeleteStatus()))
        {
            throw new ServiceException("账号已处于注销申请中");
        }
        try {
            if (!passwordEncoder.matches(RsaUtils.decryptByPrivateKey(password), user.getPasswordHash()))
            {
                throw new ServiceException("密码错误");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Date now = new Date();
        user.setDeleteStatus("1");
        user.setDeleteApplyTime(now);
        user.setDeleteScheduledAt(new Date(now.getTime() + COOLDOWN_DAYS * 86400000L));
        userMapper.updateById(user);
    }

    /**
     * 取消注销
     */
    @Transactional
    public void cancelDeactivate(Long userId)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        if (!"1".equals(user.getDeleteStatus()))
        {
            throw new ServiceException("不在冷静期");
        }

        user.setDeleteStatus("0");
        user.setDeleteScheduledAt(null);
        user.setDeleteApplyTime(null);
        userMapper.updateById(user);
    }

    /**
     * 查询注销状态
     */
    public DeactivateStatusResponse getStatus(Long userId)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }

        DeactivateStatusResponse resp = new DeactivateStatusResponse();

        if ("2".equals(user.getDeleteStatus()))
        {
            resp.setStatus("2");
            resp.setDeleteApplyTime(user.getDeleteApplyTime());
            resp.setDeleteScheduledAt(user.getDeleteScheduledAt());
            resp.setRemainingDays(0L);
            return resp;
        }

        if ("1".equals(user.getDeleteStatus()) && user.getDeleteScheduledAt() != null)
        {
            long diff = user.getDeleteScheduledAt().getTime() - System.currentTimeMillis();
            long remainingDays = Math.max(0, diff / 86400000L);
            resp.setStatus("1");
            resp.setDeleteApplyTime(user.getDeleteApplyTime());
            resp.setDeleteScheduledAt(user.getDeleteScheduledAt());
            resp.setRemainingDays(remainingDays);
            return resp;
        }

        resp.setStatus("0");
        resp.setRemainingDays(0L);
        return resp;
    }

    /**
     * 定时清理到期注销用户（每分钟执行）
     * <p>
     * 使用 Redis SETNX 分布式锁防止多实例并发执行。
     * 锁 TTL 120s（远大于单次执行时间），执行完成后立即释放。
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void processExpiredDeactivations()
    {
        log.info("[DeactivateScheduler] 开始执行到期注销检查...");

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
            SCHEDULER_LOCK_KEY, "locked", LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked))
        {
            log.info("[DeactivateScheduler] 未获取到分布式锁，其他实例可能正在处理");
            return;
        }

        try
        {
            Date now = new Date();
            LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IamUser::getDeleteStatus, "1")
                .le(IamUser::getDeleteScheduledAt, now)
                .last("LIMIT 50");

            List<IamUser> expiredUsers = userMapper.selectList(wrapper);
            log.info("[DeactivateScheduler] 查询到 {} 个到期待注销用户", expiredUsers.size());
            if (expiredUsers.isEmpty())
            {
                return;
            }

            for (IamUser user : expiredUsers)
            {
                userMapper.updateDeactivateExpired(user.getId());
                userEventPublisher.publishUserDeleted(user.getId(), user.getUsername());
                log.info("[DeactivateScheduler] 用户 {} (username={}) 注销完成",
                    user.getId(), user.getUsername());
            }
        }
        finally
        {
            redisTemplate.delete(SCHEDULER_LOCK_KEY);
            log.info("[DeactivateScheduler] 执行完毕，分布式锁已释放");
        }
    }
}
