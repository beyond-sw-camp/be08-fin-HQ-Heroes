package com.hq.heroes.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.service.AttendanceServiceImpl;
import com.hq.heroes.common.config.TestSecurityConfig;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AttendanceController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(TestSecurityConfig.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceServiceImpl attendanceService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;  // JSON 직렬화를 위한 ObjectMapper

    @MockBean
    private JWTUtil jwtUtil;

    // SecurityContext 모킹 메소드
    private void mockSecurityContext(Employee employee) {
        // CustomEmployeeDetails로 사용자 정보 모킹
        CustomEmployeeDetails userDetails = new CustomEmployeeDetails(employee);

        // Authentication 모킹
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);  // 인증된 상태 설정

        // SecurityContext 모킹
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // 테스트 시작 시 기존 컨텍스트를 지움
        SecurityContextHolder.clearContext();

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("출근 성공 테스트")
    void checkInSuccess() throws Exception {
        // Employee 객체 생성
        Employee employee = new Employee();
        employee.setEmployeeId("2024100006");
        employee.setPassword("password");  // 비밀번호 설정
        employee.setRole(Role.ROLE_USER);  // 역할 설정 (필요시 추가)

        // SecurityContext 모킹
        mockSecurityContext(employee);

        // 출근 성공 모킹
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(1L);  // 출근 후 반환되는 Attendance ID 설정

        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(attendanceService.checkIn(any(Employee.class))).thenReturn(attendance);  // 출근 처리 후 Attendance 반환

        mockMvc.perform(post("/api/v1/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())  // 출근 성공 시 201 Created 상태 확인
                .andExpect(jsonPath("$.attendanceId").value(1));  // 반환되는 Attendance ID 확인

        // 출근 서비스 호출 확인
        verify(attendanceService, times(1)).checkIn(any(Employee.class));
    }

    @Test
    @DisplayName("출근 실패 - 유효하지 않은 직원")
    void checkInFailure_InvalidEmployee() throws Exception {
        // 존재하지 않는 직원 ID 모킹
        String nonExistentEmployeeId = "invalidId";

        // SecurityContext 모킹
        Employee employee = new Employee();
        employee.setEmployeeId(nonExistentEmployeeId);  // 존재하지 않는 ID 설정
        mockSecurityContext(employee);

        // EmployeeRepository가 직원 정보를 찾을 수 없도록 설정
        when(employeeRepository.findByEmployeeId(nonExistentEmployeeId)).thenReturn(Optional.empty());

        // 출근 요청 수행
        mockMvc.perform(post("/api/v1/attendance/check-in")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());  // 직원이 없으므로 401 Unauthorized 응답 예상

        // 출근 서비스가 호출되지 않았는지 확인
        verify(attendanceService, never()).checkIn(any(Employee.class));
    }


    @Test
    @DisplayName("퇴근 성공 테스트")
    void checkOutSuccess() throws Exception {
        // Employee 객체 생성 및 필드 설정
        Employee employee = new Employee();
        employee.setEmployeeId("2024100006");
        employee.setPassword("password");  // 비밀번호 설정 (Security를 위해 필수)
        employee.setRole(Role.ROLE_USER);  // 역할 설정 (필요시 추가)

        // SecurityContext 모킹
        mockSecurityContext(employee);  // 인증된 사용자 설정

        // EmployeeRepository가 employeeId로 Employee 객체를 반환하도록 설정
        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));

        // AttendanceService의 checkOut 메서드가 정상 작동하도록 설정 (아무런 동작도 하지 않음)
        Mockito.doNothing().when(attendanceService).checkOut(any(Employee.class));

        // 실제 퇴근 요청을 모의
        mockMvc.perform(post("/api/v1/attendance/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"2024100006\", \"checkOutTime\":\"2024-10-24T18:00:00\"}"))
                .andExpect(status().isOk())  // 퇴근 성공 시 200 OK 상태 확인
                .andExpect(content().string(""));  // 응답 내용이 비어있는지 확인 (OK 상태)

        // AttendanceService의 checkOut 메서드가 한 번 호출되었는지 확인
        verify(attendanceService, times(1)).checkOut(any(Employee.class));
    }

    @Test
    @DisplayName("퇴근 실패 - 유효하지 않은 직원")
    void checkOutFailure_InvalidEmployee() throws Exception {
        // 유효하지 않은 직원 ID 모킹
        String nonExistentEmployeeId = "invalidId";

        // SecurityContext 모킹
        Employee employee = new Employee();
        employee.setEmployeeId(nonExistentEmployeeId);
        mockSecurityContext(employee);

        // EmployeeRepository가 직원 정보를 찾을 수 없도록 설정
        when(employeeRepository.findByEmployeeId(nonExistentEmployeeId)).thenReturn(Optional.empty());

        // 퇴근 요청 수행
        mockMvc.perform(post("/api/v1/attendance/check-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"invalidId\", \"checkOutTime\":\"2024-10-24T18:00:00\"}"))
                .andExpect(status().isUnauthorized());  // 직원이 없으므로 401 Unauthorized 응답 예상

        // 퇴근 서비스가 호출되지 않았는지 확인
        verify(attendanceService, never()).checkOut(any(Employee.class));
    }

}
