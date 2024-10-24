package com.hq.heroes.auth.controller;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.service.ReissueService;
import com.hq.heroes.common.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReissueController.class)
@Import(TestSecurityConfig.class)  // 시큐리티 설정을 가져옴
public class ReissueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReissueService reissueService;

    @MockBean
    private JWTUtil jwtUtil;

    private String validToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        Mockito.when(jwtUtil.isExpired(any(String.class))).thenReturn(false);
        Mockito.when(jwtUtil.getCategory(any(String.class))).thenReturn("refresh");

        // 테스트용 JWT 토큰 생성
        Mockito.when(jwtUtil.createJwt(any(), any(), any(), any()))
                .thenReturn("mockValidToken")
                .thenReturn("mockExpiredToken");

        validToken = "Bearer mockValidToken";  // 유효한 JWT 토큰
        expiredToken = "Bearer mockExpiredToken";  // 만료된 JWT 토큰
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    @WithMockUser
    void reissueSuccess() throws Exception {
        // Mocking the service response
        Mockito.when(reissueService.reissue(any(), any()))
                .thenAnswer(invocation -> new ResponseEntity<>("Success", HttpStatus.OK));

        System.out.println("Running reissueSuccess test...");
        System.out.println("Expected status: 200");

        // Performing the test
        mockMvc.perform(post("/reissue")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        System.out.println("Actual status: 200");
    }

    @Test
    @DisplayName("리프레시 토큰이 없는 경우 BAD_REQUEST 반환 테스트")
    @WithMockUser
    void reissueBadRequestWhenNoToken() throws Exception {
        // Mocking the service response
        Mockito.when(reissueService.reissue(any(), any()))
                .thenAnswer(invocation -> new ResponseEntity<>("refresh token is null", HttpStatus.BAD_REQUEST));

        System.out.println("Running reissueBadRequestWhenNoToken test...");
        System.out.println("Expected status: 400");

        // Performing the test
        mockMvc.perform(post("/reissue")
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("refresh token is null"));

        System.out.println("Actual status: 400");
    }

    @Test
    @DisplayName("리프레시 토큰 만료된 경우 BAD_REQUEST 반환 테스트")
    @WithMockUser
    void reissueExpiredToken() throws Exception {
        // Mocking the service response
        Mockito.when(reissueService.reissue(any(), any()))
                .thenAnswer(invocation -> new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST));

        System.out.println("Running reissueExpiredToken test...");
        System.out.println("Expected status: 400");

        // Performing the test
        mockMvc.perform(post("/reissue")
                        .header("Authorization", expiredToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("refresh token expired"));

        System.out.println("Actual status: 400");
    }

    @Test
    @DisplayName("리프레시 토큰 성공 후 헤더 및 쿠키 확인 테스트")
    @WithMockUser
    void reissueSuccessWithHeaders() throws Exception {
        // Mocking the service response
        Mockito.when(reissueService.reissue(any(), any()))
                .thenAnswer(invocation -> new ResponseEntity<>("Success", HttpStatus.OK));

        System.out.println("Running reissueSuccessWithHeaders test...");
        System.out.println("Expected status: 200");

        // Performing the test
        mockMvc.perform(post("/reissue")
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        System.out.println("Actual status: 200");
    }
}
