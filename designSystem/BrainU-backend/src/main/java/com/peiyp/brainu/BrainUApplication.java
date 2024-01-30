package com.peiyp.brainu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.peiyp.brainu.mapper")
public class BrainUApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrainUApplication.class, args);
    }

}
