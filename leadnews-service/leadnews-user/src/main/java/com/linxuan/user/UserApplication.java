package com.linxuan.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * EnableFeignClients开启feign远程调用接口功能需要使用该注解
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.linxuan.user.mapper")
@EnableFeignClients(basePackages = "com.linxuan.feign.api")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
