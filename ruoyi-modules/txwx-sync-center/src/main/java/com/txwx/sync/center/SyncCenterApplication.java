package com.txwx.sync.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 同步任务中心启动类
 *
 * @author txwx
 * @date 2026-04-03
 */
@SpringBootApplication
@EnableFeignClients
public class SyncCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncCenterApplication.class, args);
        System.out.println("========================================");
        System.out.println("同步任务中心启动成功！");
        System.out.println("========================================");
    }
}
