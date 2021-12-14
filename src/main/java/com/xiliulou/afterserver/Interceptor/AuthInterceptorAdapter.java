package com.xiliulou.afterserver.Interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.exception.CusTomBusinessAccessDeniedException;
import com.xiliulou.afterserver.jwt.JwtHelper;
import com.xiliulou.afterserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//@Slf4j
//@Configuration
public class AuthInterceptorAdapter extends HandlerInterceptorAdapter {
//    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
//
//    /**
//     * 不拦截的urls
//     */
//    private static List<String> urls;
//    @Autowired
//    JwtHelper jwtHelper;
//    @Autowired
//    UserService userService;
//
//    public static final String ADMIN_URL = "/admin/**";
//    public static final String SUPER_ADMIN_URL = "/super/**";
//    public static final String RIDER_URL = "/user/**";
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
//            Exception {
//        String requestUri = request.getServletPath();
//        //请求放行，无需验证权限
//        if (pathMatcher(requestUri)) {
//            return true;
//        }
//
//        if (request.getMethod().equals("OPTIONS")){
//            return true;
//        }
//
//        User user = checkToken(request);
//
//        return true;
//    }
//
//    private User checkToken(HttpServletRequest request) {
//        Map<String, Object> body = null;
//        try {
//            body = jwtHelper.validateTokenAndGetClaims(request);
//
//        } catch (Exception e) {
//            log.error("jwtHelper.validateTokenAndGetClaims error  msg :[{}]", e);
//            throw new CusTomBusinessAccessDeniedException("token异常!");
//        }
//        if (ObjectUtil.isEmpty(body)) {
//            throw new CusTomBusinessAccessDeniedException("token异常!");
//        }
//        log.info("hhh" + body.get("id").toString());
//        Long uid = Long.parseLong((String) body.get("id"));
//        User user = userService.getById(uid);
//        // TODO: 2021/2/2 0002   ThreadLocal
//
//        if (Objects.isNull(user)) {
//            throw new CusTomBusinessAccessDeniedException("用户不存在!");
//        }
//        request.setAttribute("uid", uid);
//        return user;
//    }
//
//    private boolean pathMatcher(String requestUri) {
//
//        for (String url : urls) {
//            if (antPathMatcher.match(url, requestUri)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    static {
//
//        urls = new ArrayList<>();
//
//        urls.add("/auth/**");
//        urls.add("/error**");
//        urls.add(RIDER_URL);
//    }


}