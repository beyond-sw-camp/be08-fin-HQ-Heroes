package com.hq.heroes.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequestDTO {
    // 자격증 이름
    private String certificationName;

    // 자격증 카테고리
    private String category;

    // 발급 기관
    private String institution;

    // 신청 날짜
    private Date applicationDate;

    // 시험 날짜
    private Date examDate;

    // 취득일
    private Date acquisitionDate;

    // 사원 ID
    private Long employeeId;
}
