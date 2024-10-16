package com.hq.heroes.event.dto;

import com.hq.heroes.auth.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String category;
    private String description;
    private Employee employee; // 이벤트를 생성한 사원 정보
}
