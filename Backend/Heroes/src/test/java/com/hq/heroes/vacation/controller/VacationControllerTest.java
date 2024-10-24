    package com.hq.heroes.vacation.controller;

    import com.hq.heroes.auth.jwt.JWTUtil;
    import com.hq.heroes.auth.repository.EmployeeRepository;
    import com.hq.heroes.common.config.TestSecurityConfig;
    import com.hq.heroes.vacation.dto.VacationDTO;
    import com.hq.heroes.vacation.entity.enums.VacationStatus;
    import com.hq.heroes.vacation.service.VacationServiceImpl;
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
    import java.util.Collections;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @WebMvcTest(controllers = VacationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
    @Import(TestSecurityConfig.class)
    class VacationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private VacationServiceImpl vacationService;

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
        @DisplayName("휴가 제출 성공 테스트")
        void submitVacation() throws Exception {
            mockSecurityContext("2024100006");  // 인증된 사용자 설정

            VacationDTO vacationDTO = VacationDTO.builder()
                    .employeeId("2024100006")
                    .approverName("김채원")
                    .vacationStartDate(LocalDate.of(2024, 1, 1))
                    .vacationStartTime(LocalTime.of(9, 0))
                    .vacationEndDate(LocalDate.of(2024, 1, 5))
                    .vacationEndTime(LocalTime.of(18, 0))
                    .vacationStatus(VacationStatus.PENDING)
                    .build();

            // when
            Mockito.doNothing().when(vacationService).submitVacation(any(VacationDTO.class));

            // then
            mockMvc.perform(post("/api/v1/vacation/submit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"employeeId\":\"2024100006\", \"approverName\":\"김채원\", \"vacationStartDate\":\"2024-01-01\", \"vacationEndDate\":\"2024-01-05\", \"vacationStartTime\":\"09:00:00\", \"vacationEndTime\":\"18:00:00\", \"vacationStatus\":\"PENDING\"}"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("휴가 신청이 성공적으로 제출되었습니다."));  // 메시지 일치 확인

            // verify
            verify(vacationService, times(1)).submitVacation(any(VacationDTO.class));
        }

        @Test
        @DisplayName("휴가 제출 실패 테스트 - 필수 정보 누락")
        void submitVacationFail_MissingRequiredFields() throws Exception {
            // 필수 정보 누락된 잘못된 요청
            String invalidRequest = "{\"employeeId\":\"2024100006\", \"vacationStartDate\":\"2024-01-01\"}";

            mockMvc.perform(post("/api/v1/vacation/submit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())  // 400 에러 확인
                    .andExpect(content().string("필수 정보가 누락되었습니다."));  // 메시지 일치 확인
        }

        @Test
        @DisplayName("휴가 제출 실패 테스트 - 중복 휴가 요청")
        void submitVacationFail_DuplicateVacation() throws Exception {
            VacationDTO vacationDTO = VacationDTO.builder()
                    .employeeId("2024100006")
                    .approverName("김채원")
                    .vacationStartDate(LocalDate.of(2024, 1, 1))
                    .vacationStartTime(LocalTime.of(9, 0))
                    .vacationEndDate(LocalDate.of(2024, 1, 5))
                    .vacationEndTime(LocalTime.of(18, 0))
                    .vacationStatus(VacationStatus.PENDING)
                    .build();

            // when: 중복된 휴가 요청일 때
            doThrow(new IllegalArgumentException("이미 동일한 날짜에 휴가가 등록되어 있습니다.")).when(vacationService).submitVacation(any(VacationDTO.class));

            // then
            mockMvc.perform(post("/api/v1/vacation/submit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"employeeId\":\"2024100006\", \"approverName\":\"김채원\", \"vacationStartDate\":\"2024-01-01\", \"vacationEndDate\":\"2024-01-05\", \"vacationStartTime\":\"09:00:00\", \"vacationEndTime\":\"18:00:00\", \"vacationStatus\":\"PENDING\"}"))
                    .andExpect(status().isConflict())  // 중복된 요청에 대한 상태 코드 409
                    .andExpect(content().string("이미 동일한 날짜에 휴가가 등록되어 있습니다."));
        }

        @Test
        @DisplayName("휴가 승인 성공 테스트")
        void approveVacation() throws Exception {
            // when
            doNothing().when(vacationService).approveVacation(anyLong());

            // then
            mockMvc.perform(post("/api/v1/vacation/approve/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("휴가가 성공적으로 승인되었습니다."));  // 메시지 일치 확인

            // verify
            verify(vacationService, times(1)).approveVacation(1L);
        }

        @Test
        @DisplayName("휴가 승인 실패 테스트 - 휴가 ID 없음")
        void approveVacationFail_NoVacationFound() throws Exception {
            // when: 휴가 ID가 없을 때 발생하는 상황 모킹
            doThrow(new RuntimeException("해당 휴가를 찾을 수 없습니다.")).when(vacationService).approveVacation(anyLong());

            // then
            mockMvc.perform(post("/api/v1/vacation/approve/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())  // 404 상태 코드 확인
                    .andExpect(content().string("해당 휴가를 찾을 수 없습니다."));  // 메시지 일치 확인

            // verify
            verify(vacationService, times(1)).approveVacation(1L);
        }

        @Test
        @DisplayName("휴가 거절 성공 테스트")
        void rejectVacation() throws Exception {
            // when
            doNothing().when(vacationService).rejectVacation(anyLong());

            // then
            mockMvc.perform(post("/api/v1/vacation/reject/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("휴가가 성공적으로 거절되었습니다."));  // 메시지 일치 확인

            // verify
            verify(vacationService, times(1)).rejectVacation(1L);
        }

        @Test
        @DisplayName("휴가 거절 실패 테스트 - 휴가 ID 없음")
        void rejectVacationFail_NoVacationFound() throws Exception {
            // when: 휴가 ID가 없을 때 발생하는 상황 모킹
            doThrow(new RuntimeException("해당 휴가를 찾을 수 없습니다.")).when(vacationService).rejectVacation(anyLong());

            // then
            mockMvc.perform(post("/api/v1/vacation/reject/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())  // 404 상태 코드 확인
                    .andExpect(content().string("해당 휴가를 찾을 수 없습니다."));  // 메시지 일치 확인

            // verify
            verify(vacationService, times(1)).rejectVacation(1L);
        }

        @Test
        @DisplayName("승인된 내 휴가 목록 조회 테스트")
        void getMyApprovedVacations() throws Exception {
            mockSecurityContext("2024100006");  // 인증된 사용자 설정

            VacationDTO vacationDTO = VacationDTO.builder()
                    .employeeId("2024100006")
                    .approverName("김채원")
                    .vacationStartDate(LocalDate.of(2024, 1, 1))
                    .vacationEndDate(LocalDate.of(2024, 1, 5))
                    .vacationStatus(VacationStatus.APPROVED)
                    .build();

            when(vacationService.getApprovedVacationsByEmployeeId(anyString())).thenReturn(Collections.singletonList(vacationDTO));

            // 테스트 경로를 컨트롤러의 변경된 경로와 일치시키기
            mockMvc.perform(get("/api/v1/vacation/my-vacations")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].employeeId").value("2024100006"))
                    .andExpect(jsonPath("$[0].approverName").value("김채원"))
                    .andExpect(jsonPath("$[0].vacationStatus").value("APPROVED"));
        }

        @Test
        @DisplayName("승인된 내 휴가 목록 조회 실패 테스트 - 승인된 휴가 없음")
        void getMyApprovedVacationsFail_NoVacations() throws Exception {
            mockSecurityContext("2024100006");  // 인증된 사용자 설정

            when(vacationService.getApprovedVacationsByEmployeeId(anyString()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/vacation/my-vacations")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())  // 200 상태 코드 확인
                    .andExpect(content().json("[]"));  // 빈 리스트 반환 확인

            // verify
            verify(vacationService, times(1)).getApprovedVacationsByEmployeeId(anyString());
        }
    }
