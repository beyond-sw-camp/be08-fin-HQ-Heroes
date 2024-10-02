package com.hq.heroes.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResDTO {

    private Long evaluationId;  // 평가 ID
    private String employeeId;  // 피평가자 ID
    private String evaluatorId;  // 평가자 ID
    private String evaluationTemplateTitle;  // 평가 기준 제목
    private Double score;  // 평가 점수
    private String comments;  // 평가 코멘트
    private Date createdAt;  // 생성 일시
    private Date updatedAt;  // 수정 일시
}
