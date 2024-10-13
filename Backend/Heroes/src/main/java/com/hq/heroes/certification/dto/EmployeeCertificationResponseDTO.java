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
public class EmployeeCertificationResponseDTO {
    private long registrationId;
    private String certificationName;
    private String institution;
    private LocalDate acquisitionDate;
    private String employeeName;  // 사원 이름 필드 추가
}
