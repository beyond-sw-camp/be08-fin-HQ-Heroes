package com.hq.heroes.employee.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private String employeeId;
    private String employeeName;
    private String teamName;
    private String deptName;
    private String jobName;
    private String positionName;
    private LocalDate joinDate;

    private LocalDate birthDate;
    private String phoneNumber;
    private String roadAddress;
    private String lotAddress;
    private String detailedAddress;
    private String profileImageUrl;

}

