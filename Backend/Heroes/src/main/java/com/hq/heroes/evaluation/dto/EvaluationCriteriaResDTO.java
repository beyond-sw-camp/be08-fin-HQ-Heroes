package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriteriaResDTO {

    private Long evaluationTemplateId;  // 평가 템플릿 ID
    private String departmentName;  // 부서 이름
    private String criteriaTitle;  // 평가 기준 제목
    private String criteriaContent;  // 평가 기준 내용
}