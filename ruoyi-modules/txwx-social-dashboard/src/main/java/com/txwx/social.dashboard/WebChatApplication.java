package com.txwx.social.dashboard;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableFeignClients(basePackages = {"com.txwx.social.api.client", "com.ruoyi"})
@SpringBootApplication
@MapperScan("com.txwx.social.dashboard.mapper")
@ComponentScan(basePackages = {
        "com.ruoyi.common.clickhouse.service",
        "com.txwx.social"})
@EnableScheduling
public class WebChatApplication {
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
