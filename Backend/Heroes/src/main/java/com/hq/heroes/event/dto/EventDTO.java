package com.hq.heroes.event.dto;

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

    private Long eventId;           // 이벤트 ID
    private String eventTitle;      // 이벤트 제목
    private LocalDateTime eventStart; // 이벤트 시작 시간
    private LocalDateTime eventEnd;   // 이벤트 종료 시간
    private String eventType;       // 이벤트 타입 (휴가, 출근, 병가 등)
    private String description;     // 이벤트 설명
    private Long employeeId;        // 이벤트 생성자의 직원 ID
    private Long vacationId;        // 휴가 ID (연관된 휴가가 있을 경우)
}
