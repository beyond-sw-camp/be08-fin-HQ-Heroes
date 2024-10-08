package com.hq.heroes.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private String employeeId;
    private String employeeName;
    private String teamName;
    private String deptName;
    private String email;
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

