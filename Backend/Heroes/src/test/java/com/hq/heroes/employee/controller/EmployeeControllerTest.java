package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.dto.PasswordUpdateDTO;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import com.hq.heroes.employee.service.EmployeeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class EmployeeControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private DepartmentRepository departmentRepository;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private PositionRepository positionRepository;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("특정사원조회 GET /api/v1/employee/employees/{employee-id} - 성공")
    void 특정사원조회() throws Exception {
        // given
        Employee employee = new Employee();
        employee.setEmployeeId("2024106824");
        employee.setEmployeeName("홍길동");

        // Employee 객체를 DTO로 변환
        EmployeeDTO employeeDTO = employee.toResponseDTO();

        // when
        Mockito.when(employeeService.getEmployeeById("2024106824")).thenReturn(employee);

        // then
        mockMvc.perform(get("/api/v1/employee/employees/2024106824")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.employeeId").value(employeeDTO.getEmployeeId()))
                .andExpect(jsonPath("$.employeeName").value(employeeDTO.getEmployeeName()));
    }

    @Test
    @DisplayName("사원정보수정_성공 PUT /api/v1/employee/update - 성공")
    void 사원정보수정_성공() throws Exception {
        // given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("2024106824");
        employeeDTO.setEmployeeName("홍길동");

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        // employeeData를 JSON 형식으로 설정
        MockMultipartFile employeeData = new MockMultipartFile(
                "employeeData",
                "",
                "application/json",
                "{\"employeeId\":\"2024106824\",\"employeeName\":\"홍길동\"}".getBytes()
        );

        // when
        Mockito.when(employeeService.updateEmployee(Mockito.any(EmployeeDTO.class))).thenReturn(new Employee());

        mockMvc.perform(multipart("/api/v1/employee/update")
                        .file(profileImage)
                        .file(employeeData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // HTTP 메서드 설정
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string("사원 정보가 성공적으로 업데이트되었습니다."));
    }

    @Test
    @DisplayName("사원정보수정_실패 - 이미지 형식이 아닐 경우")
    void 사원정보수정_실패_이미지형식이아닐경우() throws Exception {
        // given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("2024106824");
        employeeDTO.setEmployeeName("홍길동");

        // 잘못된 이미지 MIME 타입 설정 (예: "application/octet-stream")
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.txt",
                "application/octet-stream",
                "test content".getBytes()
        );

        // employeeData를 JSON 형식으로 설정
        MockMultipartFile employeeData = new MockMultipartFile(
                "employeeData",
                "",
                "application/json",
                "{\"employeeId\":\"2024106824\",\"employeeName\":\"홍길동\"}".getBytes()
        );

        // when
        Mockito.when(employeeService.updateEmployee(Mockito.any(EmployeeDTO.class))).thenReturn(new Employee());

        // then
        mockMvc.perform(multipart("/api/v1/employee/update")
                        .file(profileImage)
                        .file(employeeData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // HTTP 메서드 설정
                            return request;
                        }))
                .andExpect(status().isBadRequest()) // 상태 코드 400
                .andExpect(content().string("이미지 파일만 업로드할 수 있습니다.")); // 오류 메시지 확인
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공 POST /api/v1/employee/update-password")
    void 비밀번호변경_성공() throws Exception {
        // given
        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setCurrentPassword("oldPassword123");
        passwordUpdateDTO.setNewPassword("newPassword123");

        // Mock Authentication 설정
        Authentication authentication = mock(Authentication.class);
        CustomEmployeeDetails userDetails = mock(CustomEmployeeDetails.class);
        when(userDetails.getUsername()).thenReturn("2024106824");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(employeeService.updatePassword(eq("2024106824"), eq("oldPassword123"), eq("newPassword123")))
                .thenReturn(true); // Mocking 설정

        // then
        MvcResult result = mockMvc.perform(post("/api/v1/employee/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"oldPassword123\", \"newPassword\":\"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호가 성공적으로 변경되었습니다."))
                .andReturn(); // 결과를 저장

        // verify
        verify(employeeService).updatePassword("2024106824", "oldPassword123", "newPassword123");
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패 (현재 비밀번호가 틀린 경우)")
    void 비밀번호변경_실패() throws Exception {
        // given
        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO();
        passwordUpdateDTO.setCurrentPassword("wrongOldPassword");
        passwordUpdateDTO.setNewPassword("newPassword123");

        // Mock Authentication 설정
        Authentication authentication = mock(Authentication.class);
        CustomEmployeeDetails userDetails = mock(CustomEmployeeDetails.class);
        when(userDetails.getUsername()).thenReturn("2024106824");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(employeeService.updatePassword(eq("2024106824"), eq("wrongOldPassword"), eq("newPassword123")))
                .thenReturn(false); // Mocking 설정

        // then
        MvcResult result = mockMvc.perform(post("/api/v1/employee/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"wrongOldPassword\", \"newPassword\":\"newPassword123\"}"))
                .andExpect(status().isBadRequest()) // 비밀번호 변경 실패 시 적절한 HTTP 상태 코드 설정
                .andExpect(content().string("현재 비밀번호가 일치하지 않습니다.")) // 실패 메시지
                .andReturn(); // 결과를 저장

        // verify
        verify(employeeService).updatePassword("2024106824", "wrongOldPassword", "newPassword123");
    }

    @Test
    @DisplayName("유저 역할 및 직책 조회 GET /api/v1/employee/role-check")
    void 유저역할및직책조회_성공() throws Exception {
        // given
        String employeeId = "2024106824";
        String role = "ROLE_USER";
        Employee mockEmployee = new Employee();
        mockEmployee.setEmployeeId(employeeId);
        mockEmployee.setEmployeeName("John Doe");
        mockEmployee.setRole(Role.ROLE_USER);
        Position mockPosition = new Position();
        mockPosition.setPositionId(1L);
        mockEmployee.setPosition(mockPosition);

        // Mock Authentication 설정
        Authentication authentication = mock(Authentication.class);
        CustomEmployeeDetails userDetails = mock(CustomEmployeeDetails.class);
        when(userDetails.getUsername()).thenReturn(employeeId);
        when(userDetails.getRole()).thenReturn(role);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(employeeRepository.findByEmployeeId(eq(employeeId)))
                .thenReturn(Optional.of(mockEmployee)); // Mocking 설정

        // then
        MvcResult result = mockMvc.perform(get("/api/v1/employee/role-check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(content().json("{\"employeeId\":\"2024106824\",\"employeeName\":\"John Doe\",\"role\":\"ROLE_USER\",\"positionId\":1}")) // 성공 응답 확인
                .andReturn();

        // verify
        verify(employeeRepository).findByEmployeeId(employeeId);
    }

    @Test
    @DisplayName("유저 역할 및 직책 조회 - 실패")
    void 유저역할및직책조회_실패() throws Exception {
        // given
        String employeeId = "2024106824";

        // Mock Authentication 설정
        Authentication authentication = mock(Authentication.class);
        CustomEmployeeDetails userDetails = mock(CustomEmployeeDetails.class);
        when(userDetails.getUsername()).thenReturn(employeeId);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        when(employeeRepository.findByEmployeeId(eq(employeeId)))
                .thenReturn(Optional.empty()); // Mocking 설정

        // then
        MvcResult result = mockMvc.perform(get("/api/v1/employee/role-check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // HTTP 404 상태 코드 확인
                .andExpect(content().json("{\"error\":\"사용자를 찾을 수 없습니다.\"}")) // 실패 메시지 확인
                .andReturn();

        // verify
        verify(employeeRepository).findByEmployeeId(employeeId);
    }


}