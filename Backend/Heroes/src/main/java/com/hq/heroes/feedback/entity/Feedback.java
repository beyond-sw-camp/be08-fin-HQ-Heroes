package com.hq.heroes.feedback.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.feedback.entity.enums.FeedbackStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    // 피평가자 (사원)와의 연관 관계
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 평가자와의 연관 관계
    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private Employee evaluator;

    @Column(name = "score", nullable = false, precision = 3, scale = 2)
    private BigDecimal score;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeedbackStatus status = FeedbackStatus.INCOMPLETE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
