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

    @Value("${webchat.insertpublishedarticlesql}")
    private String insertpublishedarticlesql;

    @Value("${webchat.insertarticlereaddailysql}")
    private String insertarticlereaddailysql;

    @Value("${webchat.insertarticlesummarydailysql}")
    private String insertarticlesummarydailysql;

    @Value("${webchat.insertarticlesharedailysql}")
    private String insertarticlesharedailysql;

    @Value("${webchat.insertdwsuserssql}")
    private String insertdwsuserssql;

    @Value("${webchat.insertarticledetaildailysql}")
    private String insertarticledetaildailysql;

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

    public String getInsertpublishedarticlesql() {
        return insertpublishedarticlesql;
    }

    public void setInsertpublishedarticlesql(String insertpublishedarticlesql) {
        this.insertpublishedarticlesql = insertpublishedarticlesql;
    }

    public String getInsertarticlereaddailysql() {
        return insertarticlereaddailysql;
    }

    public void setInsertarticlereaddailysql(String insertarticlereaddailysql) {
        this.insertarticlereaddailysql = insertarticlereaddailysql;
    }

    public String getInsertarticlesummarydailysql() {
        return insertarticlesummarydailysql;
    }

    public void setInsertarticlesummarydailysql(String insertarticlesummarydailysql) {
        this.insertarticlesummarydailysql = insertarticlesummarydailysql;
    }

    public String getInsertarticlesharedailysql() {
        return insertarticlesharedailysql;
    }

    public void setInsertarticlesharedailysql(String insertarticlesharedailysql) {
        this.insertarticlesharedailysql = insertarticlesharedailysql;
    }

    public String getInsertdwsuserssql() {
        return insertdwsuserssql;
    }

    public void setInsertdwsuserssql(String insertdwsuserssql) {
        this.insertdwsuserssql = insertdwsuserssql;
    }

    public String getInsertarticledetaildailysql() {
        return insertarticledetaildailysql;
    }

    public void setInsertarticledetaildailysql(String insertarticledetaildailysql) {
        this.insertarticledetaildailysql = insertarticledetaildailysql;
    }
}
