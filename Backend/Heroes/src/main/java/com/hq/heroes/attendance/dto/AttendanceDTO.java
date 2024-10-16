package com.hq.heroes.attendance.dto;

import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    private Long attendanceId;
    private String employeeId;
    private String employeeName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private AttendanceStatus status;

}
