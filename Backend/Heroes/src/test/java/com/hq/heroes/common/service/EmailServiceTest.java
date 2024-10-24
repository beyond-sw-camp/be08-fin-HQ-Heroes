package com.hq.heroes.common.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    @DisplayName("6자리 인증 코드 생성 테스트")
    void createCode() {
        // when
        String code = emailService.createCode();

        // then
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));  // 숫자 6자리 확인
        System.out.println("Test Result: 생성된 인증 코드: " + code);
    }

    @Test
    @DisplayName("Redis에서 인증 코드 조회 테스트")
    void getAuthCode() {
        // given
        String email = "test@example.com";
        String authCode = "123456";

        // opsForValue()가 valueOperations를 반환하도록 모킹
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(authCode);

        // when
        String result = emailService.getAuthCode(email);

        // then
        assertEquals(authCode, result);
        System.out.println("Test Result: Redis에서 조회한 인증 코드: " + result);
    }
}
