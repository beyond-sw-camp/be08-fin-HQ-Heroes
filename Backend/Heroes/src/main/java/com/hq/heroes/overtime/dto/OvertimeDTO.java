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

    private Long overtimeId; // 휴가 ID
    private String employeeId; // 신청인 ID
    private String approverId; // 결재자 ID
    private String employeeName; // 신청인 이름
    private String approverName; // 결재자 이름
    private LocalDate overtimeStartDate; // 시작 일시
    private LocalTime overtimeStartTime;
    private LocalDate overtimeEndDate;
    private LocalTime overtimeEndTime; // 종료 일시
    private String comment; // 사유 (댓글)
    private OvertimeStatus overtimeStatus; // 휴가 상태

}
