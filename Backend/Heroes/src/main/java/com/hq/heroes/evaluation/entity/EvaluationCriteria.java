package com.hq.heroes.evaluation.entity;

import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaResDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_evaluation_template")
public class EvaluationCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_template_id")
    public Long evaluationTemplateId;

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
                .evaluationTemplateId(this.evaluationTemplateId)
                .departmentName(this.department.getDeptName())  // 부서 엔티티에서 부서명 추출
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
