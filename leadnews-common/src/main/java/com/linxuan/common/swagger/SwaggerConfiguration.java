package com.linxuan.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 创建Swagger配置类，需要让Spring Boot扫描到，有两种方式：
 *   1.继承WebMvcConfigurationSupport
 *   2.在spring.factories标明，这里采用第二种
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Docket 实例关联上 apiInfo()
     * buildDocket方法名称任意
     *
     * @return Docket Swagger实例Bean是Docket，所以通过配置Docket实例来配置Swaggger
     */
    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                // Docket 实例关联上 buildApiInfo()
                .apiInfo(buildApiInfo())
                // 通过.select()方法，去配置扫描接口
                .select()
                // 要扫描的API(Controller)基础包，RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.basePackage("com.linxuan"))
                // 配置如何通过path过滤，即这里扫描所有请求
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 可以通过apiInfo()属性配置文档信息，这些配置的信息在http://localhost:8080/swagger-ui.html显示
     *
     * @return apiInfo
     */
    private ApiInfo buildApiInfo() {
        Contact contact = new Contact("林炫", "", "");
        return new ApiInfoBuilder()
                .title("头条平台")
                .description("头条后台api")
                .contact(contact)
                .version("1.0.0")
                .build();
    }
}