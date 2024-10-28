package com.hq.heroes.event.service;

import com.hq.heroes.event.dto.EventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    void createEvent(EventDTO eventDTO);

    boolean updateEvent(Long id, LocalDateTime start, LocalDateTime end);

    List<EventDTO> getEventsByEmployeeId(String employeeId); // 특정 사용자의 이벤트를 가져오는 메서드 추가
}
