package com.linxuan.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
// 根据application.yml文件获取配置，将properties和yml配置文件属性转化为bean对象使用
@ConfigurationProperties(prefix = "minio")
public class MinIOConfigProperties implements Serializable {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}