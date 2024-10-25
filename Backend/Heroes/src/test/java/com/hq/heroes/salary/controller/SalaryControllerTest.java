package com.hq.heroes.salary.controller;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.service.EvaluationService;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = SalaryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private SalaryService salaryService;

    @MockBean
    private EvaluationService evaluationService;

    private SalaryDTO salaryDTO;
    private List<Evaluation> evaluations;

    @BeforeEach
    void setup() {
        // 공통 테스트 데이터 설정
        salaryDTO = SalaryDTO.builder()
                .employeeId("EMP123")
                .performanceBonus(500.0)
                .performanceDate(LocalDateTime.now())
                .build();

        evaluations = List.of(new Evaluation());

        // Mock 동작 설정
        Mockito.when(evaluationService.getEvaluationsByEmployeeId("EMP123"))
                .thenReturn(evaluations);
    }

    @Test
    @DisplayName("시급 및 성과급 정보 생성 - 성공 테스트")
    void 시급및성과급생성() throws Exception {
        // Given
        SalaryDTO createdSalary = SalaryDTO.builder().employeeId("EMP123").build();

        // When
        Mockito.when(salaryService.createSalary(any(SalaryDTO.class), eq(evaluations)))
                .thenReturn(createdSalary);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/salary-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": \"EMP123\", \"performanceBonus\": 500.0}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("성과급 없는 급여 관련 정보 생성 - 성공 테스트")
    void 급여정보생성() throws Exception {
        // Given
        Mockito.when(evaluationService.getEvaluationsByEmployeeId("EMP123"))
                .thenReturn(Collections.emptyList());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/salary-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\": \"EMP123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("직책에 따른 시급 조회 - 성공 테스트")
    void 시급조회() throws Exception {
        // Given
        Mockito.when(salaryService.getBaseSalaryByPositionId("EMP123"))
                .thenReturn(Optional.of(3000.0));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/salary-service/base-salary/EMP123"))
                .andExpect(status().isOk())
                .andExpect(content().string("3000.0"))
                .andDo(print());
    }

    @Test
    @DisplayName("직책에 따른 시급 조회 - 실패 테스트")
    void 시급조회실패() throws Exception {
        // Given
        Mockito.when(salaryService.getBaseSalaryByPositionId("EMP123"))
                .thenReturn(Optional.empty());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/salary-service/base-salary/EMP123"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 사원의 급여 관련 정보 조회 - 성공 테스트")
    void 특정사원급여정보조회() throws Exception {
        // Given
        List<SalaryDTO> salaries = List.of(salaryDTO);

        Mockito.when(salaryService.getAllSalariesByEmployeeId("EMP123"))
                .thenReturn(salaries);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/salary-service/salaries/EMP123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("EMP123"))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 사원의 급여 관련 정보 조회 - 실패 테스트")
    void 특정사원급여정보조회실패() throws Exception {
        // Given
        Mockito.when(salaryService.getAllSalariesByEmployeeId("EMP123"))
                .thenReturn(Collections.emptyList());

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/salary-service/salaries/EMP123"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
