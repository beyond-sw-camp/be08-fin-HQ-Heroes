package com.hq.heroes.auth.service;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            refresh = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("refresh"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String storedRefresh = refreshTokenService.checkRefresh(request);
        if (storedRefresh == null) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String newAccess = jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);

        Integer expiredS = 60 * 60 * 24;
        String newRefresh = jwtUtil.createJwt("refresh", username, role, expiredS * 1000L);

        refreshTokenService.deleteRefresh(refresh);
        refreshTokenService.saveRefresh(username, expiredS, newRefresh);

        response.setHeader("access", newAccess);

        Cookie newRefreshCookie = CookieUtil.createCookie("refresh", newRefresh, expiredS);
        CookieUtil.addSameSiteCookie(newRefreshCookie, response);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
