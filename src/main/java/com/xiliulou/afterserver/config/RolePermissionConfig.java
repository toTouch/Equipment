package com.xiliulou.afterserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Hardy
 * @date 2021/12/15 9:58
 * @mood
 */
@Configuration
@ConfigurationProperties("userrole")
@Data
@RefreshScope
public class RolePermissionConfig {

    /**
     * 不应该给其他租户看的权限
     */
    private List<Long> unShow;
}
