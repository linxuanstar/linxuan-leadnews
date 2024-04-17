package com.linxuan.search.config;

import com.linxuan.search.interceptor.AppTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置使拦截器生效，拦截所有的请求
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AppTokenInterceptor requestUrlInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestUrlInterceptor).addPathPatterns("/**");
    }
}