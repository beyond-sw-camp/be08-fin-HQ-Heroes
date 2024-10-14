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
public class EmployeeCertificationRequestDTO {
    private String certificationName;
    private long educationId;
    private long registrationId;
    private String institution;
    private LocalDate acquisitionDate;
    private String employeeId;
}
