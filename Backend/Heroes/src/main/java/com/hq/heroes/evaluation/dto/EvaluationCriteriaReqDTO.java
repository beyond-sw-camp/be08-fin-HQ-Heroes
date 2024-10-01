package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriteriaReqDTO {

    private Long departmentId;  // 부서 ID
    private String criteriaTitle;  // 평가 기준 제목
    private String criteriaContent;  // 평가 기준 내용
}
