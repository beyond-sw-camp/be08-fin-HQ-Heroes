package com.hq.heroes.common.controller;

import com.hq.heroes.auth.jwt.JWTFilter;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.common.dto.EmailMessage;
import com.hq.heroes.common.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTFilter.class)
})
class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @WithMockUser  // 인증된 사용자로 모의 실행
    void sendPasswordMail_shouldSendEmail() throws Exception {
        // given
        String requestBody = "{\"email\": \"gjongwon8802@gmail.com\"}";

        // when
        mockMvc.perform(post("/mails/password")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // then
        ArgumentCaptor<EmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, times(1)).sendMail(emailMessageCaptor.capture(), any());

        EmailMessage sentEmailMessage = emailMessageCaptor.getValue();
        assert sentEmailMessage.getTo().equals("gjongwon8802@gmail.com");
        assert sentEmailMessage.getSubject().equals("[HQ-HeRoes] 임시 비밀번호 발급");
    }

    @Test
    @WithMockUser  // 인증된 사용자로 모의 실행
    void sendAuthCodeMail_shouldSendEmail() throws Exception {
        // given
        String requestBody = "{\"email\": \"gjongwon8802@gmail.com\"}";

        // when
        mockMvc.perform(post("/mails/email")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // then
        ArgumentCaptor<EmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, times(1)).sendMail(emailMessageCaptor.capture(), any());

        EmailMessage sentEmailMessage = emailMessageCaptor.getValue();
        assert sentEmailMessage.getTo().equals("gjongwon8802@gmail.com");
        assert sentEmailMessage.getSubject().equals("[HQ-HeRoes]이메일 인증을 위한 인증 코드 발송");
    }
}
