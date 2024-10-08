package com.hq.heroes.evaluation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.evaluation.dto.EvaluationFormReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationFormResDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_evaluation_form")
public class EvaluationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_form_id")
    private Long evaluationFormId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_criteria_id", nullable = false)
    private EvaluationCriteria evaluationCriteria;

    @Column(name = "team_goal", nullable = false)
    private int teamGoal;

    @Column(name = "personal_goal", nullable = false)
    private int personalGoal;

    // 요청 DTO를 엔티티로 변환하는 메서드
    public static EvaluationForm fromRequestDTO(EvaluationFormReqDTO requestDTO, Employee employee, EvaluationCriteria evaluationCriteria) {
        return EvaluationForm.builder()
                .employee(employee)
                .evaluationCriteria(evaluationCriteria)
                .teamGoal(requestDTO.getTeamGoal())
                .personalGoal(requestDTO.getPersonalGoal())
                .build();
    }

    // 엔티티를 응답 DTO로 변환하는 메서드
    public EvaluationFormResDTO toResponseDTO() {
        return EvaluationFormResDTO.builder()
                .evaluationFormId(this.evaluationFormId)
                .employeeId(this.employee.getEmployeeId())  // employee 엔티티에서 피평가자 ID 추출
                .evaluationCriteriaTitle(this.evaluationCriteria.getCriteriaTitle())  // 평가 기준 템플릿의 제목 추출
                .teamGoal(this.teamGoal)
                .personalGoal(this.personalGoal)
                .build();
    }
}
