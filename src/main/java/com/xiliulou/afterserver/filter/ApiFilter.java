package com.xiliulou.afterserver.filter;

import cn.hutool.core.util.StrUtil;
import com.xiliulou.afterserver.util.SignUtils;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.core.web.R;
import com.xiliulou.security.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/2/25 15:29
 * @mood
 */

@Slf4j
public abstract class ApiFilter implements Filter {

    private RequestMatcher requiresAuthenticationRequestMatcher;


    public ApiFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!this.requiresAuthentication(httpServletRequest, response)) {
            filterChain.doFilter(httpServletRequest, response);
            return;
        }

        try {
            String body = IOUtils.toString(httpServletRequest.getReader());
            if (StrUtil.isEmpty(body)) {
                ResponseUtil.out(response, R.fail("AUTH.1002", "参数错误"));
                return;
            }

            ApiRequestQuery apiRequestQuery = JsonUtil.fromJson(body, ApiRequestQuery.class);
            if (Objects.isNull(apiRequestQuery)) {
                ResponseUtil.out(response, R.fail("AUTH.1002", "参数错误"));
                return;
            }

            Pair<Boolean, String> checkParamsResult = checkRequestParamsIsLegal(apiRequestQuery);
            if (!checkParamsResult.getLeft()) {
                ResponseUtil.out(response, R.fail("AUTH.1002", checkParamsResult.getRight()));
                return;
            }

            String tenantAppInfo = getTenantAppInfo();

            String signature = SignUtils.getSignature(apiRequestQuery,tenantAppInfo);
            if (!apiRequestQuery.getSign().equals(signature)) {
                ResponseUtil.out(response, R.fail("AUTH.1004", "签名失败!"));
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);


        } catch (Exception e) {
            log.error(" FILTER ERROR! ", e);
            ResponseUtil.out(response, R.fail("AUTH.1001", "系统错误"));
        }


    }


    private boolean requiresAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse response) {
        return this.requiresAuthenticationRequestMatcher.matches(httpServletRequest);

    }

    /**
     * 获取相应的租户app信息
     *
     * @param
     * @return
     */
    public abstract String getTenantAppInfo();


    protected Pair<Boolean, String> checkRequestParamsIsLegal(ApiRequestQuery apiRequestQuery) {


        if (StrUtil.isEmpty(apiRequestQuery.getAppId())) {
            return Pair.of(false, "appId不能为空！");
        }

        if (StrUtil.isEmpty(apiRequestQuery.getSign())) {
            return Pair.of(false, "签名不能为空");
        }

        if (StrUtil.isEmpty(apiRequestQuery.getData())) {
            return Pair.of(false, "data不能为空");
        }

        if (Objects.isNull(apiRequestQuery.getRequestTime())) {
            return Pair.of(false, "requestTime不能为空！");
        }

        if ((apiRequestQuery.getRequestTime() - System.currentTimeMillis()) > 20000) {
            return Pair.of(false, "requestTime不合法");
        }

        if ((System.currentTimeMillis() - apiRequestQuery.getRequestTime()) > 5 * 1000 * 60L) {
            return Pair.of(false, "请求已经过期！");
        }

        return Pair.of(true, null);
    }
}
