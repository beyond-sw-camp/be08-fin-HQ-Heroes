package com.hq.heroes.salary.dto;

import com.hq.heroes.salary.entity.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDTO {
    private Long salaryId;
    private String employeeId;
    private LocalDateTime salaryMonth;
    private Double baseSalary;
    private Double bonus;
    private Double totalSalary;
    private Status status;
    private LocalDateTime createdAt;
}
