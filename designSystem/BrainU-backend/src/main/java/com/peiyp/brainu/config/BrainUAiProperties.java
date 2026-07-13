package com.peiyp.brainu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Python inference service connection settings.
 */
@Data
@ConfigurationProperties(prefix = "brainu.ai")
public class BrainUAiProperties {

    /**
     * Internal HTTP address of the Python inference service.
     */
    private String baseUrl = "http://127.0.0.1:50007";

    /**
     * Timeout for establishing a connection, in milliseconds.
     */
    private int connectTimeout = 5000;

    /**
     * Timeout while waiting for a segmentation result, in milliseconds.
     */
    private int readTimeout = 1800000;

    /**
     * Timeout for obtaining a connection from the HTTP pool, in milliseconds.
     */
    private int connectionRequestTimeout = 5000;
}
