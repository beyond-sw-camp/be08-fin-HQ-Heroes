package com.hq.heroes.auth.service;

import com.hq.heroes.auth.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class OAuth2JwtHeaderService {
    public String oauth2JwtHeaderSet(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String access = null;

        if(cookies == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad";
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("access")){
                access = cookie.getValue();
            }
        }

        if(access == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "bad";
        }

        // 클라이언트의 access 토큰 쿠키를 만료
        response.addCookie(CookieUtil.createCookie("access", null, 0));
        response.addHeader("access", access);
        response.setStatus(HttpServletResponse.SC_OK);

        return "success";
    }
}