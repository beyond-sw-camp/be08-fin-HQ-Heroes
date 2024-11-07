package com.hq.heroes.event.dto;

import com.hq.heroes.auth.entity.Employee;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String category;
    private String description;
    private String employeeId; // 필요한 정보만 포함
    private String employeeName;
}
