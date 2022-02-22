package com.xiliulou.afterserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author zgw
 * @date 2022/2/21 16:54
 * @mood
 */
@Configuration
@ConfigurationProperties("oss")
@Data
@RefreshScope
public class OssConfig {
    private String bucket;
}
