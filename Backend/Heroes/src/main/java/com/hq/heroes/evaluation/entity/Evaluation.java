package com.hq.heroes.evaluation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long evaluationId;

    // 피평가자 (사원)와의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 평가자와의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private Employee evaluator;

    // 평가기준과의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_template_id", nullable = false)
    private EvaluationCriteria evaluationTemplate;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // 엔티티를 응답 DTO로 변환하는 메서드
    public EvaluationResDTO toResponseDTO() {
        return EvaluationResDTO.builder()
                .evaluationId(this.evaluationId)
                .employeeName(this.employee.getEmployeeName())  // employee 엔티티에서 사원 이름 추출
                .evaluatorName(this.evaluator.getEmployeeName())  // evaluator 엔티티에서 평가자 이름 추출
                .evaluationTemplateTitle(this.evaluationTemplate.getCriteriaTitle())  // 평가 기준의 제목 추출
                .score(this.score)
                .comments(this.comments)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    // 요청 DTO를 엔티티로 변환하는 메서드
    public static Evaluation fromRequestDTO(EvaluationReqDTO requestDTO, Employee employee, Employee evaluator, EvaluationCriteria evaluationTemplate) {
        return Evaluation.builder()
                .employee(employee)
                .evaluator(evaluator)
                .evaluationTemplate(evaluationTemplate)
                .score(requestDTO.getScore())
                .comments(requestDTO.getComments())
                .build();
    }
}
