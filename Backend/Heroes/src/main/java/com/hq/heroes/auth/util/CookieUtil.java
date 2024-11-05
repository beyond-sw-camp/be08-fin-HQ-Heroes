package com.hq.heroes.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static Cookie createCookie(String key, String value, Integer expiredS) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiredS);

        // SameSite=None; Secure 설정 추가
        cookie.setSecure(true); // HTTPS 사용 시 필요
        return cookie;
    }

    public static void addSameSiteCookie(Cookie cookie, HttpServletResponse response) {
        StringBuilder cookieValue = new StringBuilder(cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + "; HttpOnly; Secure; SameSite=None;");
        if (cookie.getMaxAge() > 0) {
            cookieValue.append(" Max-Age=").append(cookie.getMaxAge()).append(";");
        }
        response.addHeader("Set-Cookie", cookieValue.toString());
    }
}
