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
    private String certificationName; // 자격증 이름
    private String institution; // 발급 기관
    private LocalDate acquisitionDate; // 취득일
    private String employeeId; // 사원 번호
}
