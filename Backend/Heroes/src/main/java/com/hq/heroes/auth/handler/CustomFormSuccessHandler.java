package com.hq.heroes.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.service.RefreshTokenService;
import com.hq.heroes.auth.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CustomFormSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // create JWT
        // 사원 번호
        String username = authentication.getName();
        // 역할 USER, ADMIN
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // access 30분
        String access = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);
        response.setHeader("access", access);

        // refresh 1일
        Integer expireS = 24 * 60 * 60;
        String refresh = jwtUtil.createJwt("refresh", username, role, expireS * 1000L);
        response.addCookie(CookieUtil.createCookie("refresh", refresh, expireS));

        // refresh 토큰 DB 저장
        refreshTokenService.saveRefresh(username, expireS, refresh);

        // json 을 ObjectMapper 로 직렬화하여 전달
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("employeeId", username);

        new ObjectMapper().writeValue(response.getWriter(), responseData);
    }
}