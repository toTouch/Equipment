package com.xiliulou.afterserver.filter;

import feign.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * @author zgw
 * @date 2022/2/25 17:07
 * @mood
 */
@Slf4j
@Component()
@Order(101)
public class CompressionApiFilter extends ApiFilter {
    static final String AFTER_COMPRESSION_URL = "/app/compression/**";

    public CompressionApiFilter() {
        super(new AntPathRequestMatcher(AFTER_COMPRESSION_URL, Request.HttpMethod.POST.name()));
    }

    @Override
    public String getTenantAppInfo() {
        return "B/1aF4XecFWalszX3Uytf+G9ce+OGNsswsnASDc6Clc=";
    }
}
