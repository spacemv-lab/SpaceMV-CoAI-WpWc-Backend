package com.txwx.webchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebChatConfig {
    /**
     * 资源映射路径 前缀
     */
    @Value("${webchat.appid}")
    public String appid;

    @Value("${webchat.secret}")
    private String secret;

    @Value("${webchat.insertusersql}")
    private String insertusersql;

    @Value("${webchat.insertarticleperdaysql}")
    private String insertarticleperdaysql;

    @Value("${webchat.insertuserreadsql}")
    private String insertuserreadsql;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getInsertusersql() {
        return insertusersql;
    }

    public void setInsertusersql(String insertusersql) {
        this.insertusersql = insertusersql;
    }

    public String getInsertarticleperdaysql() {
        return insertarticleperdaysql;
    }

    public void setInsertarticleperdaysql(String insertarticleperdaysql) {
        this.insertarticleperdaysql = insertarticleperdaysql;
    }

    public String getInsertuserreadsql() {
        return insertuserreadsql;
    }

    public void setInsertuserreadsql(String insertuserreadsql) {
        this.insertuserreadsql = insertuserreadsql;
    }
}
