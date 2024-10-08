package com.hq.heroes.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationResponseDTO {
    // 자격증 번호
    private long certificationId;

    // 자격증 이름
    private String certificationName;

    // 발급 기관
    private String institution;

    // 혜택
    private String benefit;

    // 부서 번호
    private long deptId;

}
