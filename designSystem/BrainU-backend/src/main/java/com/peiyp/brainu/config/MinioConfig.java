package com.peiyp.brainu.config;

import com.peiyp.brainu.util.ParallelMinioClient;
import com.peiyp.brainu.util.ReflectUtils;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;


/**
 * @author PeiYP
 * @since 2023年03月22日 23:07
 */
@Data
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@Slf4j
public class MinioConfig {

    @Resource
    private MinioProperties minioProperties;

    /**
     * 注入minio 客户端
     */
    @Bean
    public MinioClient minioClient() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing (trust any client certificate)
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing (trust any server certificate)
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
            log.error("Install the all-trusting trust manager error:{}", e.getMessage());
        }


        // Create a custom OkHttpClient that trusts all certificates
        OkHttpClient customHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();

        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getUserName(), minioProperties.getPassword())
                .httpClient(customHttpClient)
                .build();
    }

    @Bean
    @ConditionalOnBean({MinioClient.class})
    @ConditionalOnMissingBean(ParallelMinioClient.class)
    public ParallelMinioClient parallelMinioClient(MinioClient minioClient) {
        MinioAsyncClient asyncClient = ReflectUtils.getFieldValue(minioClient, "asyncClient");
        return new ParallelMinioClient(asyncClient);
    }

}
