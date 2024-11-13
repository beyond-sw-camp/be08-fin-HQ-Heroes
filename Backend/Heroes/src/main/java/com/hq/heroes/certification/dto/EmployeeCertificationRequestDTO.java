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
    private String certificationName;
    private String institution;
    private LocalDate acquisitionDate;
    private String employeeId;

    private EmployeeCertificationStatus certificationStatus = EmployeeCertificationStatus.DENIED;
}
