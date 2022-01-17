package com.xiliulou.afterserver.service.token;

import com.xiliulou.afterserver.entity.LoginInfo;
import com.xiliulou.afterserver.service.LoginInfoService;
import com.xiliulou.security.authentication.AuthenticationSuccessPostProcessor;
import com.xiliulou.security.bean.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hardy
 * @date 2021/12/13 15:20
 * @mood
 */
@Slf4j
@Service
public class LoginSuccessPostProcessor implements AuthenticationSuccessPostProcessor {
    private final String UNKNOWN = "unknown";
    @Autowired
    LoginInfoService loginInfoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, SecurityUser user, Integer type) throws IOException, ServletException {
        LoginInfo loginInfo = new LoginInfo();
        String ip = getIP(request);
        loginInfo.setIp(ip);
        loginInfo.setUid(user.getUid());
        loginInfo.setType(type);
        loginInfo.setLoginTime(System.currentTimeMillis());
        loginInfoService.insert(loginInfo);
    }

    public String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : ip.split(",")[0];
    }
}
