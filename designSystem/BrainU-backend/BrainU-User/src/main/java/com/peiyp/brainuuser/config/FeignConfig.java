package com.peiyp.brainuuser.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author PeiYP
 * @since 2023年05月17日 19:00
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     *
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //        获取请求体
        javax.servlet.http.HttpServletRequest request = attributes.getRequest();
        //        获取token
        String token = request.getHeader("Authorization");
        //        注入feign的请求头
        template.header("Authorization",token);
    }

}
