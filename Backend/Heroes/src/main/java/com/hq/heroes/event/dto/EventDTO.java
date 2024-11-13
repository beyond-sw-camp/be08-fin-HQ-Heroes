package com.hq.heroes.event.dto;

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
    private String employeeId;
    private String employeeName;
}
