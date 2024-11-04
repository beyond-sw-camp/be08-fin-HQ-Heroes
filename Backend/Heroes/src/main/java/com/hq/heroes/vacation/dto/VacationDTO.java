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

    private Long vacationId; // 휴가 ID
    private String employeeId; // 대리인 ID
    private String approverId; // 결재자 ID
    private String applicantId; // 신청인 ID
    private String employeeName; // 대리인 이름
    private String approverName; // 결재자 이름
    private String applicantName; // 신청인 이름
    private VacationType vacationType; // 휴가 종류
    private LocalDate vacationStartDate; // 시작 일시
    private LocalTime vacationStartTime;
    private LocalDate vacationEndDate;
    private LocalTime vacationEndTime; // 종료 일시
    private String comment; // 사유 (댓글)
    private VacationStatus vacationStatus; // 휴가 상태

}
