package com.cjy.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 生鲜商城后端服务入口。
 *
 * 启动后：
 *   - 接口地址 http://localhost:8080/api/**
 *   - H2 控制台 http://localhost:8080/h2-console
 */
@SpringBootApplication
public class ShopServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopServerApplication.class, args);
    }
}
