package com.peiyp.brainuauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.peiyp.brainuauth.mapper")
public class BrainUAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainUAuthApplication.class, args);
    }

}
