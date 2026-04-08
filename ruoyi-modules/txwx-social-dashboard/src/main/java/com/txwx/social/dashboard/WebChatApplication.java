package com.txwx.social.dashboard;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import com.txwx.social.api.spi.SyncServiceProviderFactory;
import com.txwx.social.dashboard.spi.wechat.WechatSyncProvider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableRyFeignClients
@EnableScheduling
@SpringBootApplication
@MapperScan("com.txwx.**.mapper")
public class WebChatApplication {

    /**
     * 启动时注册 SPI 提供者到 sync-center 的工厂中
     */
    @Bean
    public SyncServiceProviderFactory syncServiceProviderFactory(WechatSyncProvider wechatSyncProvider) {
        SyncServiceProviderFactory factory = new SyncServiceProviderFactory();
        factory.registerProvider("wechat", wechatSyncProvider);
        return factory;
    }

    public static void main(String[] args){
        SpringApplication.run(WebChatApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  天巡微小微信公众号后台启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
