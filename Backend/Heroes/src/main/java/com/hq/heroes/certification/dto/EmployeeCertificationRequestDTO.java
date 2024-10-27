package com.hq.heroes.certification.dto;

import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertificationRequestDTO {
    private String certificationName; // 자격증 이름
    private String institution; // 발급 기관
    private LocalDate acquisitionDate; // 취득일
    private String employeeId; // 사원 번호

    private EmployeeCertificationStatus certificationStatus = EmployeeCertificationStatus.DENIED;
}
