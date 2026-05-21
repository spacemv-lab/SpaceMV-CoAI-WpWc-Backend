package com.ruoyi.iam.event;

import java.io.Serializable;

/**
 * 用户注册事件（MQ/本地事件桥接）
 *
 * @author txwx
 */
public class UserCreatedEvent implements Serializable
{
    private Long userId;
    private String username;
    private String channelType;
    private String channelAccount;

    public UserCreatedEvent() {}

    public UserCreatedEvent(Long userId, String username, String channelType, String channelAccount)
    {
        this.userId = userId;
        this.username = username;
        this.channelType = channelType;
        this.channelAccount = channelAccount;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getChannelType() { return channelType; }
    public String getChannelAccount() { return channelAccount; }
}
