package com.hq.heroes.evaluation.entity;

import com.hq.heroes.employee.entity.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_evaluation_template")
public class EvaluationTemplate {

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
}
