package com.hq.heroes.salary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SalaryHistoryDTO {
    private Long salaryId;
    private String employeeId;
    private LocalDate salaryDate; // 데이터 생성용 지급 일자
    private LocalDateTime salaryMonth; // 지급 일자
    private Double preTaxTotal; // 세전 총액
    private Double postTaxTotal; // 세후 총액
    private Double bonus; // 성과급
    private long workTime; // 근무 시간
    private long overTime; // 연장 근로 시간
    private Double baseSalary; // 시급
    private Double totalSalary; // 기본 급여
    private Double overSalary; // 연장 근로 수당

    // 공제 항목
    private Double nationalPension; // 국민연금
    private Double healthInsurance; // 건강보험
    private Double longTermCare; // 장기요양
    private Double employmentInsurance; // 고용보험
    private Double incomeTax; // 소득세
    private Double localIncomeTax; // 지방소득세
}
