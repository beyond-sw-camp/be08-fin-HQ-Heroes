package com.hq.heroes.evaluation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
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
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", nullable = false)
    private Employee employee;

    // 평가자와의 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", referencedColumnName = "employee_id", nullable = false)
    private Employee evaluator;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "notification_sent", nullable = false)
    private boolean notificationSent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 요청 DTO를 엔티티로 변환하는 메서드
    public static Evaluation fromRequestDTO(EvaluationReqDTO requestDTO, Employee employee, Employee evaluator) {
        return Evaluation.builder()
                .employee(employee)
                .evaluator(evaluator)
                .score(requestDTO.getScore())
                .comments(requestDTO.getComments())
                .build();
    }
}
