package com.hq.heroes.certification.dto;

import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeCertificationResponseDTO {
    private long registrationId;
    private String certificationName;
    private String institution;
    private LocalDate acquisitionDate;
    private String employeeId;
    private String employeeName;

    private EmployeeCertificationStatus employeeCertificationStatus;
}
