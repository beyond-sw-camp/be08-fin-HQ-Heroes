package com.hq.heroes.certification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequestDTO {
    private String certificationName;
    private String benefit;
    private String institution;
    private Long deptId;
}
