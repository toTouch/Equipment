package com.xiliulou.afterserver.util;

import com.xiliulou.security.bean.TokenUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/12/14 18:53
 * @mood
 */
public class SecurityUtils {

    public static Long getUid() {
        TokenUser userInfo = getUserInfo();
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return userInfo.getUid();
    }

    public static TokenUser getUserInfo() {
        Authentication authentication = null;
        TokenUser user = null;
        try {
            authentication = SecurityContextHolder.getContext().getAuthentication();

            if (null == authentication) {
                return null;
            }
            user = (TokenUser) authentication.getPrincipal();
        } catch (Exception e) {

        }
        if (Objects.isNull(user)) {
            return null;
        }
        return user;
    }

    public static boolean isAdmin() {
        return Objects.equals(getUid(), 1) || Objects.equals(getUid(), 2);
    }
}
