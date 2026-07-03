/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.ruoyi.iam.event.UserCreatedEvent;
import com.ruoyi.iam.event.UserDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 用户事件发布器（MVP 用 Spring ApplicationEvent 桥接，Phase 2 迁移 MQ）
 *
 * @author txwx
 */
@Slf4j
@Service
public class UserEventPublisher
{
    private final ApplicationEventPublisher eventPublisher;

    public UserEventPublisher(ApplicationEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布用户注册事件
     */
    public void publishUserCreated(Long userId, String username, String channelType, String channelAccount)
    {
        UserCreatedEvent event = new UserCreatedEvent(userId, username, channelType, channelAccount);
        eventPublisher.publishEvent(event);
        log.info("UserCreatedEvent published: userId={}, username={}", userId, username);
    }

    /**
     * 发布用户注销事件
     */
    public void publishUserDeleted(Long userId, String username)
    {
        UserDeletedEvent event = new UserDeletedEvent(userId, username);
        eventPublisher.publishEvent(event);
        log.info("UserDeletedEvent published: userId={}, username={}", userId, username);
    }
}
