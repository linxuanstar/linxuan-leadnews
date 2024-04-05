package com.linxuan.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描降级代码类的包
 */
@Configuration
@ComponentScan("com.linxuan.feign.api.article.fallback")
public class InitConfig {
}
