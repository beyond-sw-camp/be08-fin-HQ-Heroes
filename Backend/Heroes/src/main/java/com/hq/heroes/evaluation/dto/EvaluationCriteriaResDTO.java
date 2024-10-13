package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriteriaResDTO {

    private Long evaluationCriteriaId;  // 평가 템플릿 ID
    private Long deptId;  // 부서 이름
    private String criteriaTitle;  // 평가 기준 제목
    private String criteriaContent;  // 평가 기준 내용

}
