package com.hq.heroes.salary.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SalaryDTO {
    private Long salaryId;
    private String employeeId;
    private Double baseSalary;
    private Double bonus;
    private Date salaryMonth;

    public Double getTotalSalary() {
        return baseSalary + bonus;
    }
}
