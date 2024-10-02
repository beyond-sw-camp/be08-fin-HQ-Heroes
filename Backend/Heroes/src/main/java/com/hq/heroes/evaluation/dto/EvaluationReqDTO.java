package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationReqDTO {

    private String employeeId;  // 피평가자 ID
    private String evaluatorId;  // 평가자 ID
    private Long evaluationTemplateId;  // 평가 기준 템플릿 ID
    private Double score;  // 평가 점수
    private String comments;  // 평가 코멘트
}
