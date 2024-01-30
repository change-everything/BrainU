package com.peiyp.brainuuser.config;

import com.peiyp.brainuuser.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author PeiYP
 * @since 2023年02月10日 18:14
 */
@Configuration
public class BrainUWebConfig extends WebMvcConfigurationSupport {
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor()).addPathPatterns("/**").excludePathPatterns("/doctor-info/**");
        super.addInterceptors(registry);
    }
}
