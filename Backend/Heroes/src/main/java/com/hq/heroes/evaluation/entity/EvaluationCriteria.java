package com.hq.heroes.evaluation.entity;

import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaResDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_evaluation_criteria")
public class EvaluationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_criteria_id")
    public Long evaluationCriteriaId;

    // 부서와의 Many-to-One 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @Column(name = "criteria_title", nullable = false)
    private String criteriaTitle;

    @Column(name = "criteria_content", nullable = false)
    private String criteriaContent;

    // 엔티티를 응답 DTO로 변환하는 메서드
    public EvaluationCriteriaResDTO toResponseDTO() {
        return EvaluationCriteriaResDTO.builder()
                .evaluationCriteriaId(this.evaluationCriteriaId)
                .deptId(this.department.getDeptId())  // 부서 엔티티에서 부서명 추출
                .criteriaTitle(this.criteriaTitle)
                .criteriaContent(this.criteriaContent)
                .build();
    }

    // 요청 DTO를 엔티티로 변환하는 메서드
    public static EvaluationCriteria fromRequestDTO(EvaluationCriteriaReqDTO requestDTO, Department department) {
        return EvaluationCriteria.builder()
                .department(department)
                .criteriaTitle(requestDTO.getCriteriaTitle())
                .criteriaContent(requestDTO.getCriteriaContent())
                .build();
    }
}
