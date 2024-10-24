package com.hq.heroes.common.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.config.TestSecurityConfig;
import com.hq.heroes.common.dto.EmailMessage;
import com.hq.heroes.common.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailController.class)
@Import(TestSecurityConfig.class)  // 시큐리티 설정을 가져옴
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmployeeRepository employeeRepository;  // EmployeeRepository 모킹

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("임시 비밀번호 이메일 전송 성공 테스트")
    @WithMockUser
    void sendPasswordMail_shouldSendEmail() throws Exception {
        // given: 직원 정보와 이메일 메시지 설정
        Employee employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("gjongwon8802@gmail.com")
                .build();

        Mockito.when(employeeRepository.findByEmployeeId("2024100001"))
                .thenReturn(Optional.of(employee));

        String requestBody = "{\"employeeId\": \"2024100001\", \"name\": \"홍길동\"}";

        // when: 이메일 전송 요청
        mockMvc.perform(post("/mails/password")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());  // 응답 상태 확인

        // then: 이메일 메시지가 전송되었는지 확인
        ArgumentCaptor<EmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, times(1)).sendMail(emailMessageCaptor.capture(), any());

        // 이메일 내용 검증
        EmailMessage sentEmailMessage = emailMessageCaptor.getValue();
        assert sentEmailMessage.getTo().equals("gjongwon8802@gmail.com");
        assert sentEmailMessage.getSubject().equals("[HQ-HeRoes] 임시 비밀번호 발급");
    }

    @Test
    @DisplayName("존재하지 않는 직원에 대한 비밀번호 이메일 요청 실패 테스트")
    @WithMockUser
    void sendPasswordMail_whenEmployeeNotFound() throws Exception {
        // given: 직원이 존재하지 않는 경우
        Mockito.when(employeeRepository.findByEmployeeId("2024100002"))
                .thenReturn(Optional.empty());

        String requestBody = "{\"employeeId\": \"2024100002\", \"name\": \"홍길동\"}";

        // when & then: 요청이 404 상태로 실패하는지 확인
        mockMvc.perform(post("/mails/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이름이 다를 때 이메일 요청 실패 테스트")
    @WithMockUser
    void sendPasswordMail_whenNameMismatch() throws Exception {
        // given: 직원은 존재하지만 이름이 일치하지 않는 경우
        Employee employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("이순신")
                .email("gjongwon8802@gmail.com")
                .build();

        Mockito.when(employeeRepository.findByEmployeeId("2024100001"))
                .thenReturn(Optional.of(employee));

        String requestBody = "{\"employeeId\": \"2024100001\", \"name\": \"홍길동\"}";

        // when & then: 요청이 400 상태로 실패하는지 확인
        mockMvc.perform(post("/mails/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송 성공 테스트")
    @WithMockUser
    void sendAuthCodeMail_shouldSendEmail() throws Exception {
        // given: 직원 정보 설정
        Employee employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("gjongwon8802@gmail.com")
                .build();

        Mockito.when(employeeRepository.findByEmployeeId("2024100001"))
                .thenReturn(Optional.of(employee));

        String requestBody = "{\"employeeId\": \"2024100001\"}";

        // when: 인증 코드 이메일 전송 요청
        mockMvc.perform(post("/mails/email")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // then: 이메일이 발송되었는지 확인
        ArgumentCaptor<EmailMessage> emailMessageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService, times(1)).sendMail(emailMessageCaptor.capture(), any());

        // 이메일 내용 검증
        EmailMessage sentEmailMessage = emailMessageCaptor.getValue();
        assert sentEmailMessage.getTo().equals("gjongwon8802@gmail.com");
        assert sentEmailMessage.getSubject().equals("[HQ-HeRoes] 이메일 인증을 위한 인증 코드 발송");
    }
}
