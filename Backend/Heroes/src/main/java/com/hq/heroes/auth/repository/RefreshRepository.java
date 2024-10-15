package com.hq.heroes.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RefreshRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // RefreshToken 저장
    public void save(String refreshToken, String employeeId, Long expiration) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(refreshToken, employeeId, expiration, TimeUnit.MILLISECONDS);
    }

    // RefreshToken 조회
    public String findEmployeeIdByRefreshToken(String refreshToken) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(refreshToken);
    }

    // RefreshToken 존재 여부 확인
    public boolean existsByRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    // RefreshToken 삭제
    public void deleteByRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    // 모든 RefreshToken 조회
    public List<String> findAllEmployeeIds() {
        // Redis에 저장된 모든 키 중 'refresh' 관련 키 조회
        Set<String> keys = redisTemplate.keys("*");

        if (keys == null || keys.isEmpty()) {
            return List.of(); // 키가 없을 경우 빈 리스트 반환
        }

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // 각 키에 대한 employeeId 가져오기
        return keys.stream()
                .map(valueOps::get)
                .collect(Collectors.toList());
    }
}
