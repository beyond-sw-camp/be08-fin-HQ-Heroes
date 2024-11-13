package com.hq.heroes.overtime.dto;

import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeDTO {

    private Long overtimeId;
    private String employeeId;
    private String approverId;
    private String employeeName;
    private String approverName;
    private LocalDate overtimeStartDate;
    private LocalTime overtimeStartTime;
    private LocalDate overtimeEndDate;
    private LocalTime overtimeEndTime;
    private String comment;
    private OvertimeStatus overtimeStatus;

}
