package com.hq.heroes.certification.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequestDTO {
    private String certificationName; // 자격증 이름
    private String benefit; // 혜택
    private String institution; // 발급 기관
    private Long deptId; // 부서 ID
}
