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
    private long certificationId;
    private String certificationName;
    private String institution;
    private String benefit;
    private String deptName;
    private Long deptId;
}
