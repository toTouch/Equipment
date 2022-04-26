package com.xiliulou.afterserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zgw
 * @date 2022/4/26 16:00
 * @mood
 */
@Configuration
@ConfigurationProperties(prefix = "app-config")
@Data
public class AppConfig {
    private String username;
    private String password;
    private String appId;
    private String appSecret;
    private String ossAccessKeyId;
    private String ossAccessKeySecret;
}
