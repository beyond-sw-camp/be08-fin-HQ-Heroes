package com.hq.heroes.vacation.dto;

import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationDTO {

    private Long vacationId;
    private String employeeId;
    private String approverId;
    private String applicantId;
    private String employeeName;
    private String approverName;
    private String applicantName;
    private VacationType vacationType;
    private LocalDate vacationStartDate;
    private LocalTime vacationStartTime;
    private LocalDate vacationEndDate;
    private LocalTime vacationEndTime;
    private String comment;
    private VacationStatus vacationStatus;

}
