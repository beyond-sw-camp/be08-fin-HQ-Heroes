package com.hq.heroes.salary.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDTO {

    private String employeeId;
    private Double baseSalary;
    private Double bonus;
    private LocalDateTime salaryMonth;

    public Double getTotalSalary() {
        return baseSalary + bonus;
    }
}
