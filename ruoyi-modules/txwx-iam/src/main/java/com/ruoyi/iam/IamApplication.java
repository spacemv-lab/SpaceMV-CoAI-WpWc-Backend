package com.ruoyi.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

/**
 * IAM 身份认证服务启动类
 *
 * @author txwx
 */
@EnableRyFeignClients
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
public class IamApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(IamApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  IAM身份认证服务启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-----.--.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_.'          \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'          \n" +
                " |  | \\ `'   /|   `-.'  /           \n" +
                " |  |  \\    /  \\      /            \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
