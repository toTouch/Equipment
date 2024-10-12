package com.xiliulou.afterserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "product-config")
@Data
public class ProductConfig {
    private String key;
    private String apiKey;
    private String saasTcpKey;
    private String huaweiKey;
    private String tcpKey;
    private String huaweiAccessKey;
    private String huaweiAccessSecret;
    private String endpoint;

}
