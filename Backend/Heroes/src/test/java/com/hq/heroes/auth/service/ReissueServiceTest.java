package com.hq.heroes.auth.service;

import com.hq.heroes.auth.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReissueServiceTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ReissueService reissueService;

    @Test
    @DisplayName("성공적으로 새로운 access 및 refresh 토큰을 발급하는 테스트")
    void reissue_success() {
        // given
        Cookie refreshCookie = new Cookie("refresh", "validRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isExpired("validRefreshToken")).thenReturn(false);
        when(jwtUtil.getCategory("validRefreshToken")).thenReturn("refresh");
        when(jwtUtil.getUsername("validRefreshToken")).thenReturn("testUser");
        when(jwtUtil.getRole("validRefreshToken")).thenReturn("ROLE_USER");
        when(refreshTokenService.checkRefresh(request)).thenReturn("validRefreshToken");

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        when(jwtUtil.createJwt("access", "testUser", "ROLE_USER", 30 * 60 * 1000L)).thenReturn(newAccessToken);
        when(jwtUtil.createJwt("refresh", "testUser", "ROLE_USER", 60 * 60 * 24 * 1000L)).thenReturn(newRefreshToken);

        // when
        ResponseEntity<?> result = reissueService.reissue(request, response);

        // then
        System.out.println("Test Result: " + result.getStatusCode());
        System.out.println("New Access Token: " + newAccessToken);
        System.out.println("New Refresh Token: " + newRefreshToken);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(refreshTokenService, times(1)).deleteRefresh("validRefreshToken");
        verify(refreshTokenService, times(1)).saveRefresh("testUser", 60 * 60 * 24, newRefreshToken);
        verify(response, times(1)).setHeader("access", newAccessToken);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("쿠키에 refresh 토큰이 없을 때 실패 테스트")
    void reissue_fail_noRefreshToken() {
        // given
        when(request.getCookies()).thenReturn(null);

        // when
        ResponseEntity<?> result = reissueService.reissue(request, response);

        // then
        System.out.println("Test Result: " + result.getStatusCode());
        System.out.println("Error Message: " + result.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("refresh token is null", result.getBody());
    }

    @Test
    @DisplayName("만료된 refresh 토큰일 때 실패 테스트")
    void reissue_fail_expiredToken() {
        // given
        Cookie refreshCookie = new Cookie("refresh", "expiredRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isExpired("expiredRefreshToken")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // when
        ResponseEntity<?> result = reissueService.reissue(request, response);

        // then
        System.out.println("Test Result: " + result.getStatusCode());
        System.out.println("Error Message: " + result.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("refresh token expired", result.getBody());
    }

    @Test
    @DisplayName("Redis에 refresh 토큰이 없을 때 실패 테스트")
    void reissue_fail_noRedisToken() {
        // given
        Cookie refreshCookie = new Cookie("refresh", "invalidRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshCookie});
        when(jwtUtil.isExpired("invalidRefreshToken")).thenReturn(false);
        when(jwtUtil.getCategory("invalidRefreshToken")).thenReturn("refresh");
        when(refreshTokenService.checkRefresh(request)).thenReturn(null);

        // when
        ResponseEntity<?> result = reissueService.reissue(request, response);

        // then
        System.out.println("Test Result: " + result.getStatusCode());
        System.out.println("Error Message: " + result.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("invalid refresh token", result.getBody());
    }
}
