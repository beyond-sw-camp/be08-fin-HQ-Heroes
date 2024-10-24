package com.hq.heroes.evaluation.service;

import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.evaluation.repository.EvaluationCriteriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationCriteriaServiceTest {

    @Mock
    private EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EvaluationCriteriaServiceImpl evaluationCriteriaService;

    private EvaluationCriteria evaluationCriteria;
    private Department department;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .deptId(1L)
                .deptName("영업부")
                .build();

        evaluationCriteria = EvaluationCriteria.builder()
                .evaluationCriteriaId(1L)
                .criteriaTitle("팀워크")
                .criteriaContent("팀워크 평가")
                .department(department)
                .build();
    }

    @Test
    @DisplayName("ID로 평가 기준 조회 테스트")
    void getEvaluationCriteriaById() {
        // given
        when(evaluationCriteriaRepository.findById(anyLong())).thenReturn(Optional.of(evaluationCriteria));

        // when
        EvaluationCriteria foundCriteria = evaluationCriteriaService.getEvaluationCriteriaById(1L);

        // then
        assertNotNull(foundCriteria);
        assertEquals("팀워크", foundCriteria.getCriteriaTitle());
        verify(evaluationCriteriaRepository, times(1)).findById(1L);
        System.out.println("Test Result: Found Criteria Title = " + foundCriteria.getCriteriaTitle());
    }

    @Test
    @DisplayName("ID로 평가 기준 조회 실패 테스트 - 존재하지 않는 ID")
    void getEvaluationCriteriaById_notFound() {
        // given
        when(evaluationCriteriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        EvaluationCriteria foundCriteria = evaluationCriteriaService.getEvaluationCriteriaById(1L);

        // then
        assertNull(foundCriteria);  // 조회된 결과가 없을 경우 null 반환
        verify(evaluationCriteriaRepository, times(1)).findById(1L);
        System.out.println("Test Result: Evaluation Criteria not found.");
    }

    @Test
    @DisplayName("평가 기준 생성 테스트")
    void createEvaluationCriteria() {
        // given
        EvaluationCriteriaReqDTO reqDTO = EvaluationCriteriaReqDTO.builder()
                .deptId(1L)
                .criteriaTitle("소통 능력")
                .criteriaContent("소통 능력 평가")
                .build();

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));

        // 새 EvaluationCriteria 객체를 생성하여 반환하도록 수정
        when(evaluationCriteriaRepository.save(any(EvaluationCriteria.class))).thenAnswer(invocation -> {
            EvaluationCriteria savedCriteria = invocation.getArgument(0);
            return savedCriteria;
        });

        // when
        EvaluationCriteria createdCriteria = evaluationCriteriaService.createEvaluationCriteria(reqDTO);

        // then
        assertNotNull(createdCriteria);
        assertEquals("소통 능력", createdCriteria.getCriteriaTitle());
        verify(evaluationCriteriaRepository, times(1)).save(any(EvaluationCriteria.class));
        verify(departmentRepository, times(1)).findById(reqDTO.getDeptId());

        System.out.println("Test Result: Created Criteria Title = " + createdCriteria.getCriteriaTitle());
    }

    @Test
    @DisplayName("평가 기준 생성 실패 테스트 - 잘못된 부서 ID")
    void createEvaluationCriteria_invalidDept() {
        // given
        EvaluationCriteriaReqDTO reqDTO = EvaluationCriteriaReqDTO.builder()
                .deptId(99L)  // 존재하지 않는 부서 ID
                .criteriaTitle("소통 능력")
                .criteriaContent("소통 능력 평가")
                .build();

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty()); // 부서 조회 실패

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            evaluationCriteriaService.createEvaluationCriteria(reqDTO);
        });

        verify(departmentRepository, times(1)).findById(reqDTO.getDeptId());
        verify(evaluationCriteriaRepository, times(0)).save(any(EvaluationCriteria.class));  // save 호출되지 않음

        System.out.println("Test Result: Failed to create Criteria due to invalid department ID.");
    }

    @Test
    @DisplayName("평가 기준 업데이트 테스트")
    void updateEvaluationCriteria() {
        // given
        EvaluationCriteriaReqDTO reqDTO = EvaluationCriteriaReqDTO.builder()
                .deptId(1L)
                .criteriaTitle("업데이트된 소통 능력")
                .criteriaContent("업데이트된 소통 능력 평가")
                .build();

        when(evaluationCriteriaRepository.findById(anyLong())).thenReturn(Optional.of(evaluationCriteria));
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        when(evaluationCriteriaRepository.save(any(EvaluationCriteria.class))).thenReturn(evaluationCriteria);

        // when
        EvaluationCriteria updatedCriteria = evaluationCriteriaService.updateEvaluationCriteria(1L, reqDTO);

        // then
        assertNotNull(updatedCriteria);
        assertEquals("업데이트된 소통 능력", updatedCriteria.getCriteriaTitle());
        verify(evaluationCriteriaRepository, times(1)).findById(1L);
        verify(evaluationCriteriaRepository, times(1)).save(any(EvaluationCriteria.class));

        System.out.println("Test Result: Updated Criteria Title = " + updatedCriteria.getCriteriaTitle());
    }

    @Test
    @DisplayName("평가 기준 업데이트 실패 테스트 - 존재하지 않는 평가 기준 ID")
    void updateEvaluationCriteria_notFound() {
        // given
        EvaluationCriteriaReqDTO reqDTO = EvaluationCriteriaReqDTO.builder()
                .deptId(1L)
                .criteriaTitle("업데이트된 소통 능력")
                .criteriaContent("업데이트된 소통 능력 평가")
                .build();

        when(evaluationCriteriaRepository.findById(anyLong())).thenReturn(Optional.empty());  // 존재하지 않는 ID

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            evaluationCriteriaService.updateEvaluationCriteria(1L, reqDTO);
        });

        verify(evaluationCriteriaRepository, times(1)).findById(1L);
        verify(evaluationCriteriaRepository, times(0)).save(any(EvaluationCriteria.class));  // save 호출되지 않음

        System.out.println("Test Result: Failed to update Criteria - Criteria not found.");
    }

    @Test
    @DisplayName("평가 기준 삭제 테스트")
    void deleteEvaluationCriteria() {
        // given
        when(evaluationCriteriaRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(evaluationCriteriaRepository).deleteById(anyLong());

        // when
        boolean isDeleted = evaluationCriteriaService.deleteEvaluationCriteria(1L);

        // then
        assertTrue(isDeleted);
        verify(evaluationCriteriaRepository, times(1)).existsById(1L);
        verify(evaluationCriteriaRepository, times(1)).deleteById(1L);

        System.out.println("Test Result: Evaluation Criteria deleted successfully.");
    }

    @Test
    @DisplayName("평가 기준 삭제 실패 테스트 - 존재하지 않는 ID")
    void deleteEvaluationCriteria_notFound() {
        // given
        when(evaluationCriteriaRepository.existsById(anyLong())).thenReturn(false);  // ID가 존재하지 않음

        // when
        boolean isDeleted = evaluationCriteriaService.deleteEvaluationCriteria(1L);

        // then
        assertFalse(isDeleted);
        verify(evaluationCriteriaRepository, times(1)).existsById(1L);
        verify(evaluationCriteriaRepository, times(0)).deleteById(1L);  // delete 호출되지 않음

        System.out.println("Test Result: Failed to delete Evaluation Criteria - Criteria not found.");
    }
}
