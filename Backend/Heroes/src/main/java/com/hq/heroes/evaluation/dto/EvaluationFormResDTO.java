package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationFormResDTO {

    private Long evaluationFormId;  // 평가 양식 ID
    private String employeeId;  // 피평가자 ID
    private String evaluationCriteriaTitle;  // 평가 기준 제목
    private String teamGoal;  // 팀 목표 점수
    private String personalGoal;  // 개인 목표 점수
}
