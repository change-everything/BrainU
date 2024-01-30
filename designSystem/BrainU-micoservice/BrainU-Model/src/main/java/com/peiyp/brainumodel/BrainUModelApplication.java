package com.peiyp.brainumodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author PeiYP
 * @since 2023年05月06日 23:40
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@MapperScan("com.peiyp.brainumodel.mapper")
public class BrainUModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrainUModelApplication.class, args);
    }
}
