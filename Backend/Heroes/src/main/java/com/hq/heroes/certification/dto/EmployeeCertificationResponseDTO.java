package com.hq.heroes.certification.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmployeeCertificationResponseDTO {
    private long registrationId; // 자격증 번호
    private String certificationName; // 자격증 이름
    private String institution; // 발급기관
    private LocalDate acquisitionDate; // 취득일
    private String employeeName;  // 사원 이름 필드 추가
}
