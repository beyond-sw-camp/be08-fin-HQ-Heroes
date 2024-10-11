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
    private String institution; // 발급 기관
    private LocalDate applicationDate; // 신청 날짜
    private LocalDate examDate; // 시험 날짜
    private LocalDate acquisitionDate; // 취득일
    private Long employeeId; // 사원 ID
}
