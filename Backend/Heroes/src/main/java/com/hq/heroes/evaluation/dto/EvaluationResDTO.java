package com.hq.heroes.evaluation.dto;

import com.hq.heroes.evaluation.entity.EvaluationForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResDTO {

    private Long evaluationId;  // 평가 ID
    private String employeeId;  // 피평가자 ID
    private String evaluatorId;  // 평가자 ID
    private Double score;  // 평가 점수
    private String comments;  // 평가 코멘트
    private Date createdAt;  // 생성 일시
    private Date updatedAt;  // 수정 일시

}
