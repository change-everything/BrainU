package com.peiyp.brainu.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Pooled HTTP client used only for calls to the Python inference process.
 */
@Configuration
@EnableConfigurationProperties(BrainUAiProperties.class)
public class BrainUAiConfig {

    @Bean(destroyMethod = "close")
    public PoolingHttpClientConnectionManager brainuAiConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(10);
        manager.setDefaultMaxPerRoute(5);
        return manager;
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient brainuAiHttpClient(
            BrainUAiProperties properties,
            @Qualifier("brainuAiConnectionManager") PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(properties.getConnectTimeout())
                .setSocketTimeout(properties.getReadTimeout())
                .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(60, TimeUnit.SECONDS)
                .build();
    }

    @Bean("brainuAiRestTemplate")
    public RestTemplate brainuAiRestTemplate(
            @Qualifier("brainuAiHttpClient") CloseableHttpClient httpClient) {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
