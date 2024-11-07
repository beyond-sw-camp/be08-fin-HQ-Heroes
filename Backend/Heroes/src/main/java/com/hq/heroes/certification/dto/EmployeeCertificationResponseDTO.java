package com.hq.heroes.certification.dto;

import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeCertificationResponseDTO {
    private long registrationId; // 자격증 번호
    private String certificationName; // 자격증 이름
    private String institution; // 발급기관
    private LocalDate acquisitionDate; // 취득일
    private String employeeId;
    private String employeeName;

    private EmployeeCertificationStatus employeeCertificationStatus;
}
