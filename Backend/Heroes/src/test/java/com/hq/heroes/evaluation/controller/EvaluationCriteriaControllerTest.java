package com.hq.heroes.evaluation.controller;

import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.evaluation.service.EvaluationCriteriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EvaluationCriteriaController.class)
class EvaluationCriteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationCriteriaService evaluationCriteriaService;

    @MockBean
    private JWTUtil jwtUtil;

    private Department department1;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Mock 부서 데이터 생성 (영업부)
        department1 = Department.builder()
                .deptId(1L)
                .deptName("영업부")
                .build();
    }

    @Test
    @DisplayName("부서별 평가 기준 조회 테스트")
    void getEvaluationCriteriaByDepartment() throws Exception {
        // Mock 평가 기준 데이터 생성
        EvaluationCriteria mockCriteria = EvaluationCriteria.builder()
                .evaluationCriteriaId(1L)
                .criteriaTitle("팀워크")
                .criteriaContent("팀원 간 협력과 의사소통")
                .department(department1)  // 부서 정보 추가
                .build();

        // 서비스에서 부서 이름으로 평가 기준 목록을 반환하도록 Mock 설정
        Mockito.when(evaluationCriteriaService.getEvaluationCriterListByDeptName(anyString()))
                .thenReturn(Collections.singletonList(mockCriteria));

        // API 호출 및 응답 검증
        mockMvc.perform(get("/api/v1/evaluation-criteria/by-department")
                        .param("deptName", "영업부")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evaluationCriteriaId").value(1L))
                .andExpect(jsonPath("$[0].criteriaTitle").value("팀워크"))
                .andExpect(jsonPath("$[0].criteriaContent").value("팀원 간 협력과 의사소통"));
        // 부서 정보에 대한 검증은 제외
    }

    @Test
    @DisplayName("평가 기준 ID로 조회 테스트")
    void getEvaluationCriteriaById() throws Exception {
        // Mock 평가 기준 데이터 생성
        EvaluationCriteria mockCriteria = EvaluationCriteria.builder()
                .evaluationCriteriaId(1L)
                .criteriaTitle("팀워크")
                .criteriaContent("팀원 간 협력과 의사소통")
                .department(department1)  // 부서 정보 추가
                .build();

        // 서비스에서 평가 기준 ID로 평가 기준 반환하도록 Mock 설정
        Mockito.when(evaluationCriteriaService.getEvaluationCriteriaById(anyLong()))
                .thenReturn(mockCriteria);

        // API 호출 및 응답 검증
        mockMvc.perform(get("/api/v1/evaluation-criteria/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationCriteriaId").value(1L))
                .andExpect(jsonPath("$.criteriaTitle").value("팀워크"))
                .andExpect(jsonPath("$.criteriaContent").value("팀원 간 협력과 의사소통"));
        // 부서 정보에 대한 검증은 제외
    }

    @Test
    @DisplayName("평가 기준 등록 테스트")
    void createEvaluationCriteria() throws Exception {
        // Mock 평가 기준 데이터 생성
        EvaluationCriteria mockCriteria = EvaluationCriteria.builder()
                .evaluationCriteriaId(1L)
                .criteriaTitle("팀워크")
                .criteriaContent("팀원 간 협력과 의사소통")
                .department(department1)  // 부서 정보 추가
                .build();

        // 서비스에서 평가 기준 생성 시 Mock 데이터를 반환하도록 설정
        Mockito.when(evaluationCriteriaService.createEvaluationCriteria(any(EvaluationCriteriaReqDTO.class)))
                .thenReturn(mockCriteria);

        // API 호출 및 응답 검증
        mockMvc.perform(post("/api/v1/evaluation-criteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"criteriaTitle\":\"팀워크\",\"criteriaContent\":\"팀원 간 협력과 의사소통\",\"deptId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evaluationCriteriaId").value(1L))
                .andExpect(jsonPath("$.criteriaTitle").value("팀워크"))
                .andExpect(jsonPath("$.criteriaContent").value("팀원 간 협력과 의사소통"));
        // 부서 정보에 대한 검증은 제외
    }

    @Test
    @DisplayName("평가 기준 수정 테스트")
    void updateEvaluationCriteria() throws Exception {
        // Mock 평가 기준 데이터 생성 (수정된 기준)
        EvaluationCriteria mockCriteria = EvaluationCriteria.builder()
                .evaluationCriteriaId(1L)
                .criteriaTitle("리더십")
                .criteriaContent("팀을 이끄는 능력")
                .department(department1)  // 부서 정보 추가
                .build();

        // 서비스에서 평가 기준 수정 시 Mock 데이터를 반환하도록 설정
        Mockito.when(evaluationCriteriaService.updateEvaluationCriteria(anyLong(), any(EvaluationCriteriaReqDTO.class)))
                .thenReturn(mockCriteria);

        // API 호출 및 응답 검증
        mockMvc.perform(put("/api/v1/evaluation-criteria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"criteriaTitle\":\"리더십\",\"criteriaContent\":\"팀을 이끄는 능력\",\"deptId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationCriteriaId").value(1L))
                .andExpect(jsonPath("$.criteriaTitle").value("리더십"))
                .andExpect(jsonPath("$.criteriaContent").value("팀을 이끄는 능력"));
        // 부서 정보에 대한 검증은 제외
    }

    @Test
    @DisplayName("평가 기준 삭제 테스트")
    void deleteEvaluationCriteria() throws Exception {
        // 평가 기준 삭제 성공 시 Mock 설정
        Mockito.when(evaluationCriteriaService.deleteEvaluationCriteria(anyLong())).thenReturn(true);

        // API 호출 및 응답 검증
        mockMvc.perform(delete("/api/v1/evaluation-criteria/1"))
                .andExpect(status().isOk());
    }
}
