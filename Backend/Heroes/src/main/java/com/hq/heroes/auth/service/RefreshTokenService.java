package com.hq.heroes.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public String checkRefresh(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return null;
        }

        // Redis에 저장되어 있는지 확인
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String storedRefresh = opsForValue.get(refresh);

        // Refresh 토큰이 없거나, 만료되었으면 null 반환
        if (storedRefresh == null) {
            return null;
        }

        return refresh;
    }

    public void saveRefresh(String employeeId, Integer expireS, String refresh) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(refresh, employeeId, expireS, TimeUnit.SECONDS);  // 만료 시간 설정
    }

    public void deleteRefresh(String refresh) {
        redisTemplate.delete(refresh);
    }
}
