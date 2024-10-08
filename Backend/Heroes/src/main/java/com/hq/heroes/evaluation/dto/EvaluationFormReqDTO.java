package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationFormReqDTO {

    private String employeeId;  // 피평가자 ID
    private Long evaluationCriteriaId;  // 평가 기준 ID
    private int teamGoal;  // 팀 목표 점수
    private int personalGoal;  // 개인 목표 점수
}
