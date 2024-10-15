package com.hq.heroes.salary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SalaryDTO {
    private Long salaryId;
    private Long positionId;
    private String employeeId;
    private Double performanceBonus;
    private LocalDateTime performanceDate;
    private Double baseSalary;
}
