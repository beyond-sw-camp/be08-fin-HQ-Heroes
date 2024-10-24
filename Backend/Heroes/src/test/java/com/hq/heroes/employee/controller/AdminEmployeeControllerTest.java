package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.employee.dto.EmployeeDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminEmployeeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdminEmployeeControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("관리자 사원 정보 수정 - 성공 PUT /api/v1/admin/employee/update")
    void 관리자_사원정보수정_성공() throws Exception {
        // given
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        MockMultipartFile employeeData = new MockMultipartFile(
                "employeeData",
                "",
                "application/json",
                "{\"employeeId\":\"2024106824\",\"employeeName\":\"홍길동\"}".getBytes()
        );

        Employee mockEmployee = new Employee();
        mockEmployee.setEmployeeId("2024106824");
        mockEmployee.setEmployeeName("홍길동");

        // when
        Mockito.when(employeeService.adminUpdateEmployee(any(EmployeeDTO.class)))
                .thenReturn(mockEmployee);

        // then
        MvcResult result = mockMvc.perform(multipart("/api/v1/admin/employee/update")
                        .file(profileImage)
                        .file(employeeData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // PUT 메소드로 변경
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().string("사원 정보가 성공적으로 업데이트되었습니다."))
                .andReturn();

        // verify
        Mockito.verify(employeeService).adminUpdateEmployee(any(EmployeeDTO.class));
    }

    @Test
    @DisplayName("사원 정보 수정 - 실패 (이미지 파일이 아님)")
    void 관리자_사원정보수정_실패() throws Exception {
        // given
        MockMultipartFile invalidFile = new MockMultipartFile(
                "profileImage",
                "test.txt",
                "text/plain",
                "this is not an image".getBytes()
        );

        MockMultipartFile employeeData = new MockMultipartFile(
                "employeeData",
                "",
                "application/json",
                "{\"employeeId\":\"2024106824\",\"employeeName\":\"홍길동\"}".getBytes()
        );

        // when & then
        MvcResult result = mockMvc.perform(multipart("/api/v1/admin/employee/update")
                        .file(invalidFile)
                        .file(employeeData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // PUT 메소드로 변경
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미지 파일만 업로드할 수 있습니다."))
                .andReturn();
    }

}