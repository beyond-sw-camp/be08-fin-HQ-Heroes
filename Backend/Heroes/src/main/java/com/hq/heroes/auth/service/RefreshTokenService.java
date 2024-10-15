package com.hq.heroes.auth.service;

import com.hq.heroes.auth.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshRepository refreshRepository;

    public String checkRefresh(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {
            return null;
        }

        // Redis에서 refresh 토큰이 저장되어 있는지 확인
        boolean exists = refreshRepository.existsByRefreshToken(refresh);

        if (!exists) {
            return null; // Redis에 저장된 토큰이 없으면 null 반환
        }

        return refresh;
    }

    public void saveRefresh(String employeeId, Integer expireS, String refresh) {
        // Refresh 토큰을 저장 (레디스에 저장)
        refreshRepository.save(refresh, employeeId, expireS * 1000L); // expireS를 초 단위로 설정
    }

    public void deleteRefresh(String refresh) {
        // Redis에서 Refresh 토큰 삭제
        refreshRepository.deleteByRefreshToken(refresh);
    }
}
