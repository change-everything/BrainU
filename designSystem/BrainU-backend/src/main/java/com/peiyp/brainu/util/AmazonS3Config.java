package com.peiyp.brainu.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.peiyp.brainu.config.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * AWS连接MioIO方式
 * @author peiYP
 * @date   2022/12/15 14:23
 **/
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class AmazonS3Config {

    @Resource
    private MinioProperties minioProperties;

    @Bean(name = "amazonS3")
    public AmazonS3 amazonS3() {
        //设置连接时的参数
        ClientConfiguration config = new ClientConfiguration();
        //设置连接方式，可选参数为HTTP和HTTPS
        boolean httpsFlag = minioProperties.getEndpoint().toLowerCase().startsWith("https");
        config.setProtocol(httpsFlag ? Protocol.HTTPS : Protocol.HTTP);
        //设置网络访问超时时间
        config.setConnectionTimeout(5000);
        config.setUseExpectContinue(true);
        AWSCredentials credentials = new BasicAWSCredentials(minioProperties.getUserName(), minioProperties.getPassword());
        //设置Endpoint
        AwsClientBuilder.EndpointConfiguration end_point = new AwsClientBuilder.EndpointConfiguration(minioProperties.getEndpoint(), Regions.US_EAST_1.name());
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(end_point)
                .withPathStyleAccessEnabled(true).build();
        return amazonS3;
    }

}

