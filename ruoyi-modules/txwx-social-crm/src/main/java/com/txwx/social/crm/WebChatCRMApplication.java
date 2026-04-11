package com.txwx.social.crm;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableRyFeignClients
@EnableScheduling
@SpringBootApplication
@MapperScan("com.txwx.social.crm.mapper")
@ComponentScan(basePackages = "com.txwx.social.crm")
public class WebChatCRMApplication {

    public static void main(String[] args){
        SpringApplication.run(WebChatCRMApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  天巡微小CRM服务启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
