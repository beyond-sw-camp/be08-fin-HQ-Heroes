package com.hq.heroes.overtime.controller;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.config.TestSecurityConfig;
import com.hq.heroes.overtime.dto.OvertimeDTO;
import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import com.hq.heroes.overtime.service.OvertimeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OvertimeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(TestSecurityConfig.class)
class OvertimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OvertimeServiceImpl overtimeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private JWTUtil jwtUtil;

    // SecurityContext 모킹 메소드 추가
    private void mockSecurityContext(String employeeId) {
        // Authentication 모킹
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(employeeId);

        // SecurityContext 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("연장 근로 제출 성공 테스트")
    void submitOvertime() throws Exception {
        mockSecurityContext("2024100006");  // 인증된 사용자 설정

        OvertimeDTO overtimeDTO = OvertimeDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))
                .overtimeStartTime(LocalTime.of(18, 0))
                .overtimeEndTime(LocalTime.of(21, 0))
                .overtimeStatus(OvertimeStatus.PENDING)
                .build();

        // when
        Mockito.doNothing().when(overtimeService).submitOvertime(any(OvertimeDTO.class));

        // 정확한 필드명과 데이터로 JSON 요청 본문 수정
        mockMvc.perform(post("/api/v1/overtime/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"2024100006\", \"approverName\":\"김채원\", \"overtimeStartDate\":\"2024-01-01\", \"overtimeEndDate\":\"2024-01-01\", \"overtimeStartTime\":\"18:00:00\", \"overtimeEndTime\":\"21:00:00\", \"overtimeStatus\":\"PENDING\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("연장 근로 신청이 성공적으로 제출되었습니다."));  // 메시지 일치 확인

        // verify
        verify(overtimeService, times(1)).submitOvertime(any(OvertimeDTO.class));
    }


    @Test
    @DisplayName("연장 근로 제출 실패 테스트 - 필수 정보 누락")
    void submitOvertimeFail_MissingRequiredFields() throws Exception {
        // 잘못된 요청(필수 정보 누락)
        String invalidRequest = "{\"employeeId\":\"2024100006\"}";  // 필수 정보인 날짜, 시간 누락

        mockMvc.perform(post("/api/v1/overtime/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())  // 400 Bad Request 확인
                .andExpect(content().string("필수 정보가 누락되었습니다."));  // 메시지 확인
    }

    @Test
    @DisplayName("연장 근로 승인 성공 테스트")
    void approveOvertime() throws Exception {
        // when
        doNothing().when(overtimeService).approveOvertime(anyLong());

        // then
        mockMvc.perform(post("/api/v1/overtime/approve/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("연장 근로가 승인되었습니다."));  // 메시지 일치 확인

        // verify
        verify(overtimeService, times(1)).approveOvertime(1L);
    }

    @Test
    @DisplayName("연장 근로 승인 실패 테스트 - ID 없음")
    void approveOvertimeFail_NoOvertimeFound() throws Exception {
        doThrow(new RuntimeException("해당 근무를 찾을 수 없습니다.")).when(overtimeService).approveOvertime(anyLong());

        mockMvc.perform(post("/api/v1/overtime/approve/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // 404 Not Found 확인
                .andExpect(content().string("해당 근무를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("연장 근로 거절 성공 테스트")
    void rejectOvertime() throws Exception {
        // when
        doNothing().when(overtimeService).rejectOvertime(anyLong());

        // then
        mockMvc.perform(post("/api/v1/overtime/reject/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("연장 근로가 반려되었습니다."));  // 메시지 일치 확인

        // verify
        verify(overtimeService, times(1)).rejectOvertime(1L);
    }

    @Test
    @DisplayName("연장 근로 거절 실패 테스트 - ID 없음")
    void rejectOvertimeFail_NoOvertimeFound() throws Exception {
        doThrow(new RuntimeException("해당 근무를 찾을 수 없습니다.")).when(overtimeService).rejectOvertime(anyLong());

        mockMvc.perform(post("/api/v1/overtime/reject/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // 404 Not Found 확인
                .andExpect(content().string("해당 근무를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("승인된 연장 근로 목록 조회 테스트")
    void getMyApprovedOvertimes() throws Exception {
        mockSecurityContext("2024100006");  // 인증된 사용자 설정

        OvertimeDTO overtimeDTO = OvertimeDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))
                .overtimeStartTime(LocalTime.of(18, 0))
                .overtimeEndTime(LocalTime.of(21, 0))
                .overtimeStatus(OvertimeStatus.APPROVED)
                .build();

        when(overtimeService.getApprovedOvertimesByEmployeeId(anyString())).thenReturn(Collections.singletonList(overtimeDTO));

        // 테스트 경로를 컨트롤러의 변경된 경로와 일치시키기
        mockMvc.perform(get("/api/v1/overtime/my-overtimes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("2024100006"))
                .andExpect(jsonPath("$[0].approverName").value("김채원"))
                .andExpect(jsonPath("$[0].overtimeStatus").value("APPROVED"));
    }

    @Test
    @DisplayName("승인된 연장 근로 목록 조회 실패 테스트 - 승인된 근무 없음")
    void getMyApprovedOvertimesFail_NoApprovedOvertimes() throws Exception {
        mockSecurityContext("2024100006");  // 인증된 사용자 설정

        when(overtimeService.getApprovedOvertimesByEmployeeId(anyString())).thenReturn(Collections.emptyList());  // 빈 리스트 반환

        mockMvc.perform(get("/api/v1/overtime/my-overtimes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK 상태 확인
                .andExpect(content().json("[]"));  // 빈 리스트 반환 확인
    }

    @Test
    @DisplayName("월별 총 시간외 근무 시간 조회 테스트")
    void getTotalOvertimeForMonth() throws Exception {
        mockSecurityContext("2024100006");  // 인증된 사용자 설정

        when(overtimeService.getTotalOvertimeHoursForMonth(anyString(), any(YearMonth.class))).thenReturn(30L);  // 30시간 모킹

        mockMvc.perform(get("/api/v1/overtime/total-overtime")
                        .param("employeeId", "2024100006")  // 컨트롤러에서 사용한 파라미터 이름으로 수정
                        .param("yearMonth", "2024-01")     // "yyyy-MM" 형식으로 수정
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("30"));  // 총 시간외 근무 시간 확인
    }

    @Test
    @DisplayName("월별 총 시간외 근무 시간 조회 실패 테스트 - 잘못된 날짜 형식")
    void getTotalOvertimeForMonthFail_InvalidDateFormat() throws Exception {
        mockSecurityContext("2024100006");  // 인증된 사용자 설정

        mockMvc.perform(get("/api/v1/overtime/total-overtime")
                        .param("employeeId", "2024100006")
                        .param("yearMonth", "2024-13")  // 잘못된 날짜 형식(13월은 존재하지 않음)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // 400 Bad Request 확인
                .andExpect(content().string("잘못된 날짜 형식입니다."));  // 적절한 에러 메시지 확인
    }
}
