package com.peiyp.brainugateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BrainUGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainUGatewayApplication.class, args);
    }

}
