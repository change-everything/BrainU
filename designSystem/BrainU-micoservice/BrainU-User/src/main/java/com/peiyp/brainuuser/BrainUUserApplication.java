package com.peiyp.brainuuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.peiyp.brainuuser.mapper")
public class BrainUUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainUUserApplication.class, args);
    }

}
