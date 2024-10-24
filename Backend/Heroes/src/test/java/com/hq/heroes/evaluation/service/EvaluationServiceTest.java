package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    private Employee kim;   // 사원
    private EvaluationReqDTO evaluationReqDTO;
    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 팀장 정보 (홍길동)
        Employee hong = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .build();

        // 사원 정보 (김철수)
        kim = Employee.builder()
                .employeeId("2024100002")
                .employeeName("김철수")
                .email("kim@naver.com")
                .joinDate(LocalDate.of(2024, 10, 22))
                .build();

        // 평가 생성 요청 DTO
        evaluationReqDTO = EvaluationReqDTO.builder()
                .employeeId(kim.getEmployeeId())
                .score(85.0)
                .comments("훌륭한 업무 성과")
                .build();

        // 평가 엔티티
        evaluation = Evaluation.builder()
                .evaluationId(1L)
                .employee(kim)
                .evaluator(hong)
                .score(85.0)
                .comments("훌륭한 업무 성과")
                .build();
    }

    @Test
    @DisplayName("평가 생성 성공 테스트 - 팀장이 사원을 평가하는 경우")
    void createEvaluation_success() {
        // given
        when(employeeRepository.findById(kim.getEmployeeId())).thenReturn(Optional.of(kim));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(evaluation);

        // when
        Evaluation result = evaluationService.createEvaluation(evaluationReqDTO);

        // then
        assertNotNull(result);
        assertEquals(evaluationReqDTO.getScore(), result.getScore());
        assertEquals(evaluationReqDTO.getComments(), result.getComments());

        System.out.println("Test Result: Evaluation created successfully.");
        System.out.println("Employee ID: " + result.getEmployee().getEmployeeId());
        System.out.println("Score: " + result.getScore());
        System.out.println("Comments: " + result.getComments());

        verify(employeeRepository, times(1)).findById(kim.getEmployeeId());
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("평가 생성 실패 테스트 - 존재하지 않는 직원 ID")
    void createEvaluation_fail_employeeNotFound() {
        // given
        when(employeeRepository.findById(kim.getEmployeeId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            evaluationService.createEvaluation(evaluationReqDTO);
        });

        System.out.println("Test Result: Evaluation creation failed - Employee not found.");

        verify(employeeRepository, times(1)).findById(kim.getEmployeeId());
        verify(evaluationRepository, times(0)).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("평가 조회 성공 테스트 - 특정 평가 ID로 조회")
    void getEvaluationById_success() {
        // given
        when(evaluationRepository.findById(anyLong())).thenReturn(Optional.of(evaluation));

        // when
        Evaluation result = evaluationService.getEvaluationById(1L);

        // then
        assertNotNull(result);
        System.out.println("Test Result: Evaluation retrieved successfully.");
        System.out.println("Employee ID: " + result.getEmployee().getEmployeeId());
        System.out.println("Score: " + result.getScore());
        System.out.println("Comments: " + result.getComments());

        verify(evaluationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("평가 조회 실패 테스트 - 존재하지 않는 평가 ID로 조회")
    void getEvaluationById_fail_notFound() {
        // given
        when(evaluationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        Evaluation result = evaluationService.getEvaluationById(999L);  // 존재하지 않는 ID

        assertNull(result);
        System.out.println("Test Result: Evaluation retrieval failed - Evaluation not found.");

        verify(evaluationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("평가 업데이트 성공 테스트")
    void updateEvaluation_success() {
        // given
        when(evaluationRepository.findById(anyLong())).thenReturn(Optional.of(evaluation));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(evaluation);

        EvaluationReqDTO updateDTO = EvaluationReqDTO.builder()
                .score(90.0)
                .comments("업데이트된 평가")
                .employeeId(kim.getEmployeeId())
                .build();

        // when
        Evaluation result = evaluationService.updateEvaluation(1L, updateDTO);

        // then
        assertNotNull(result);
        assertEquals(90, result.getScore());
        assertEquals("업데이트된 평가", result.getComments());

        System.out.println("Test Result: Evaluation updated successfully.");
        System.out.println("Updated Score: " + result.getScore());
        System.out.println("Updated Comments: " + result.getComments());

        verify(evaluationRepository, times(1)).findById(1L);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("평가 삭제 성공 테스트")
    void deleteEvaluation_success() {
        // given
        when(evaluationRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(evaluationRepository).deleteById(anyLong());

        // when
        boolean isDeleted = evaluationService.deleteEvaluation(1L);

        // then
        assertTrue(isDeleted);
        System.out.println("Test Result: Evaluation deleted successfully.");

        verify(evaluationRepository, times(1)).existsById(1L);
        verify(evaluationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("평가 삭제 실패 테스트 - 존재하지 않는 평가 ID")
    void deleteEvaluation_fail_notFound() {
        // given
        when(evaluationRepository.existsById(anyLong())).thenReturn(false);

        // when
        boolean isDeleted = evaluationService.deleteEvaluation(999L);

        // then
        assertFalse(isDeleted);
        System.out.println("Test Result: Evaluation deletion failed - Evaluation not found.");

        verify(evaluationRepository, times(1)).existsById(999L);
        verify(evaluationRepository, times(0)).deleteById(999L);
    }
}
