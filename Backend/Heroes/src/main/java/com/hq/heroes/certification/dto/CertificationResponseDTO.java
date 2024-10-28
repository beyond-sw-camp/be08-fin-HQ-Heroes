package com.hq.heroes.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationResponseDTO {
    private long certificationId; // 자격증 번호
    private String certificationName; // 자격증 이름
    private String institution; // 발급 기관
    private String benefit; // 혜택
    private String deptName; // 부서 이름
    private Long deptId;
}
