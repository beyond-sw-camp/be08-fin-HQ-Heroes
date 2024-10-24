package com.hq.heroes.auth.service;

import com.hq.heroes.auth.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshRepository refreshRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("쿠키에 있는 refresh 토큰을 확인하고 Redis에서 존재 여부를 확인하는 테스트")
    void checkRefresh() {
        // given
        Cookie refreshCookie = new Cookie("refresh", "refreshToken2024100001");
        Cookie[] cookies = {refreshCookie};
        when(request.getCookies()).thenReturn(cookies);
        when(refreshRepository.existsByRefreshToken("refreshToken2024100001")).thenReturn(true);

        // when
        String result = refreshTokenService.checkRefresh(request);

        // then
        assertNotNull(result);
        assertEquals("refreshToken2024100001", result);
        verify(refreshRepository, times(1)).existsByRefreshToken("refreshToken2024100001");

        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("쿠키에 refresh 토큰이 없을 때 null 반환 테스트")
    void checkRefresh_noToken() {
        // given
        when(request.getCookies()).thenReturn(null);

        // when
        String result = refreshTokenService.checkRefresh(request);

        // then
        assertNull(result);
        verify(refreshRepository, never()).existsByRefreshToken(anyString());

        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("Redis에 저장된 refresh 토큰이 없을 때 null 반환 테스트")
    void checkRefresh_noRedisToken() {
        // given
        Cookie refreshCookie = new Cookie("refresh", "refreshToken2024100001");
        Cookie[] cookies = {refreshCookie};
        when(request.getCookies()).thenReturn(cookies);
        when(refreshRepository.existsByRefreshToken("refreshToken2024100001")).thenReturn(false);

        // when
        String result = refreshTokenService.checkRefresh(request);

        // then
        assertNull(result);
        verify(refreshRepository, times(1)).existsByRefreshToken("refreshtokenrefreshToken2024100001");

        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("Redis에 refresh 토큰 저장 테스트")
    void saveRefresh() {
        // given
        String employeeId = "2024100001";
        String refreshToken = "refreshToken2024100001";
        int expireS = 3600;

        // when
        refreshTokenService.saveRefresh(employeeId, expireS, refreshToken);
        // then
        verify(refreshRepository, times(1)).save(refreshToken, employeeId, expireS * 1000L);
    }

    @Test
    @DisplayName("Redis에서 refresh 토큰 삭제 테스트")
    void deleteRefresh() {
        // given
        String refreshToken = "refreshToken2024100001";

        // when
        refreshTokenService.deleteRefresh(refreshToken);

        // then
        verify(refreshRepository, times(1)).deleteByRefreshToken(refreshToken);
    }
}
