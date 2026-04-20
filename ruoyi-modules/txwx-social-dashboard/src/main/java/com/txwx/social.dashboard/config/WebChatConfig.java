package com.txwx.social.dashboard.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
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

    @Value("${webchat.insertdwsbizsummarychanneldailysql}")
    private String insertdwsbizsummarychanneldailysql;

    @Value("${webchat.insertarticledetaildailysql}")
    private String insertarticledetaildailysql;
}
