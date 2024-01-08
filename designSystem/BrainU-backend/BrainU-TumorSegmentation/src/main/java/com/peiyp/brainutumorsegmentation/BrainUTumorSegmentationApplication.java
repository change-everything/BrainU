package com.peiyp.brainutumorsegmentation;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@MapperScan("com.peiyp.brainutumorsegmentation.mapper")
public class BrainUTumorSegmentationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainUTumorSegmentationApplication.class, args);
    }

}
