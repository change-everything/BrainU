package com.peiyp.brainu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author PeiYP
 * @since 2023年03月22日 23:02
 */
@Data
@Component
@ConfigurationProperties("minio")
public class MinioProperties {
    /**
     * 访问地址
     */
    private String endpoint;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
}
