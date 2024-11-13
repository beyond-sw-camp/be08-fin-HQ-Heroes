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
    private LocalDate salaryDate;
    private LocalDateTime salaryMonth;
    private Double preTaxTotal;
    private Double postTaxTotal;
    private Double bonus;
    private long workTime;
    private long overTime;
    private Double baseSalary;
    private Double totalSalary;
    private Double overSalary;

    // 공제 항목
    private Double nationalPension;
    private Double healthInsurance;
    private Double longTermCare;
    private Double employmentInsurance;
    private Double incomeTax;
    private Double localIncomeTax;
}
