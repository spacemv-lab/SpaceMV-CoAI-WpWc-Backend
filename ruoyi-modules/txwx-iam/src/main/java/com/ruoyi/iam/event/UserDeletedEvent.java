package com.ruoyi.iam.event;

import java.io.Serializable;

/**
 * 用户注销事件（MQ/本地事件桥接）
 *
 * @author txwx
 */
public class UserDeletedEvent implements Serializable
{
    private Long userId;
    private String username;

    public UserDeletedEvent() {}

    public UserDeletedEvent(Long userId, String username)
    {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}
