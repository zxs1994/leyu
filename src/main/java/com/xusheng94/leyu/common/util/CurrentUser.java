package com.xusheng94.leyu.common.util;

import com.xusheng94.leyu.config.security.LoginUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {

    private CurrentUser() {}

    public static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static LoginUser getLoginUser() {
        Authentication auth = getAuth();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        }

        return null;
    }

    public static Long getUserId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getUserId() : null;
    }

    public static boolean isPlatformUser() {
        LoginUser user = getLoginUser();
        return user != null && user.isPlatformUser();
    }

    public static boolean isTenantUser() {
        LoginUser user = getLoginUser();
        return user != null && user.isTenantUser();
    }

    public static boolean isSystemUser() {
        LoginUser user = getLoginUser();
        return user != null && user.isSystemUser();
    }

    public static Long getTenantId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getTenantId() : null;
    }

    public static String getEmail() {
        LoginUser user = getLoginUser();
        return user != null ? user.getEmail() : null;
    }

    public static String getSource() {
        LoginUser user = getLoginUser();
        return user != null ? user.getSource() : null;
    }

    public static boolean isLogin() {
        return getLoginUser() != null;
    }
}
