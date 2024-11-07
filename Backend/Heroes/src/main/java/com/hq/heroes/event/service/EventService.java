package com.hq.heroes.event.service;

import com.hq.heroes.event.dto.EventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // 일정 생성
    void createEvent(EventDTO eventDTO);

    // 일정 수정
    boolean updateEvent(Long id, LocalDateTime start, LocalDateTime end);

    // 일정 삭제
    void deleteEvent(Long eventId);

    // 회원 번호로 일정 조회
    List<EventDTO> getEventsByEmployeeId(String employeeId);
}
