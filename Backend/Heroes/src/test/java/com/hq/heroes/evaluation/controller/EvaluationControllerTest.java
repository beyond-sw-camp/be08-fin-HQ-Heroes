package com.hq.heroes.evaluation.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.service.EvaluationService;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EvaluationController.class)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationService evaluationService;

    @MockBean
    private JWTUtil jwtUtil;

    private Employee hong;
    private Employee kim;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 평가자 (Evaluator)
        hong = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .phoneNumber("01012345678")
                .roadAddress("서울 동작구 보라매로 87")
                .lotAddress("서울 동작구 신대방동 344-4")
                .detailedAddress("5층")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();

        // 피평가자 (Evaluatee)
        kim = Employee.builder()
                .employeeId("2024100002")
                .employeeName("김철수")
                .email("silverwater@naver.com")
                .password("1234")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 22))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1992, 11, 5))
                .phoneNumber("01098765432")
                .roadAddress("경기 고양시 일산서구 가좌로 2")
                .lotAddress("경기 고양시 일산서구 가좌동 158-1")
                .detailedAddress("101호")
                .profileImageUrl("http://example.com/profile2.jpg")
                .build();
    }

    @Test
    @DisplayName("사원 ID로 평가 목록 조회 테스트")
    void getEvaluationsByEmployeeId() throws Exception {
        // Mock Evaluation 생성 (홍길동이 김철수를 평가)
        Evaluation mockEvaluation = Evaluation.builder()
                .evaluationId(1L)
                .score(5.0)
                .comments("Excellent work!")
                .employee(kim) // 피평가자: 김철수
                .evaluator(hong) // 평가자: 홍길동
                .build();

        // evaluationService에서 사원 ID로 평가 목록을 반환하도록 Mock 설정
        Mockito.when(evaluationService.getEvaluationsByEmployeeId(anyString()))
                .thenReturn(Collections.singletonList(mockEvaluation));

        // API 요청을 시뮬레이션하고 테스트 수행
        mockMvc.perform(get("/api/v1/evaluation-service/evaluations/by-employeeId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evaluationId").value(1L))
                .andExpect(jsonPath("$[0].score").value(5))
                .andExpect(jsonPath("$[0].comments").value("Excellent work!"));
    }

    @Test
    @DisplayName("평가 ID로 평가 조회 테스트")
    void getEvaluationById() throws Exception {
        // Mock Evaluation 생성 (홍길동이 김철수를 평가)
        Evaluation mockEvaluation = Evaluation.builder()
                .evaluationId(1L)
                .score(5.0)
                .comments("Great job!")
                .employee(kim) // 피평가자: 김철수
                .evaluator(hong) // 평가자: 홍길동
                .build();

        // evaluationService에서 평가 ID로 평가를 반환하도록 Mock 설정
        Mockito.when(evaluationService.getEvaluationById(anyLong()))
                .thenReturn(mockEvaluation);

        // API 요청을 시뮬레이션하고 테스트 수행
        mockMvc.perform(get("/api/v1/evaluation-service/evaluation/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value(1L))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.comments").value("Great job!"));
    }

    @Test
    @DisplayName("평가 등록 테스트")
    void create() throws Exception {
        // Mock Evaluation 생성 (홍길동이 김철수를 평가)
        Evaluation mockEvaluation = Evaluation.builder()
                .evaluationId(1L)
                .score(5.0)
                .comments("Good performance!")
                .employee(kim) // 피평가자: 김철수
                .evaluator(hong) // 평가자: 홍길동
                .build();

        // evaluationService에서 평가 생성 시 Mock 데이터를 반환하도록 설정
        Mockito.when(evaluationService.createEvaluation(any(EvaluationReqDTO.class)))
                .thenReturn(mockEvaluation);

        // API 요청을 시뮬레이션하고 테스트 수행
        mockMvc.perform(post("/api/v1/evaluation-service/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"2024100002\",\"score\":5,\"comments\":\"Good performance!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evaluationId").value(1L))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.comments").value("Good performance!"));
    }

    @Test
    @DisplayName("평가 수정 테스트")
    void update() throws Exception {
        // Mock Evaluation 생성 (홍길동이 김철수를 평가하고 수정)
        Evaluation mockEvaluation = Evaluation.builder()
                .evaluationId(1L)
                .score(4.0)
                .comments("Updated performance review")
                .employee(kim) // 피평가자: 김철수
                .evaluator(hong) // 평가자: 홍길동
                .build();

        // evaluationService에서 평가 수정 시 Mock 데이터를 반환하도록 설정
        Mockito.when(evaluationService.updateEvaluation(anyLong(), any(EvaluationReqDTO.class)))
                .thenReturn(mockEvaluation);

        // API 요청을 시뮬레이션하고 테스트 수행
        mockMvc.perform(put("/api/v1/evaluation-service/evaluation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":4,\"comments\":\"Updated performance review\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value(1L))
                .andExpect(jsonPath("$.score").value(4))
                .andExpect(jsonPath("$.comments").value("Updated performance review"));
    }

    @Test
    @DisplayName("평가 삭제 테스트")
    void delete() throws Exception {
        // 평가가 성공적으로 삭제될 때 Mock 설정
        Mockito.when(evaluationService.deleteEvaluation(anyLong())).thenReturn(true);
    }
}
