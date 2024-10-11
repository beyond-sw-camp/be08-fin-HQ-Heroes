package com.hq.heroes.certification.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequestDTO {
    private String certificationName; // 자격증 이름
    private String category; // 자격증 카테고리
    private String benefit;
    private String institution; // 발급 기관
    private LocalDate applicationStartDate; // 신청 시작일
    private LocalDate applicationEndDate; // 신청 종료일
    private LocalDate examDate; // 시험 날짜
    private Long deptId; // 부서 ID
}
