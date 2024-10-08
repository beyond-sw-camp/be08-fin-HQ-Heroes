package com.hq.heroes.vacation.dto;

import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationDTO {

    private Long vacationId; // 휴가 ID
    private String employeeId; // 신청인 ID
    private String approverId; // 결재자 ID
    private VacationType vacationType; // 휴가 종류
    private LocalDateTime vacationStart; // 시작 일시
    private LocalDateTime vacationEnd; // 종료 일시
    private String comment; // 사유 (댓글)
    private VacationStatus vacationStatus; // 휴가 상태
    private LocalDateTime createdAt; // 생성 일시
    private LocalDateTime updatedAt; // 수정 일시

}
