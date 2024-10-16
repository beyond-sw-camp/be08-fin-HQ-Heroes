package com.hq.heroes.event.service;

import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.entity.Event;
import com.hq.heroes.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public void createEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .eventTitle(eventDTO.getTitle())
                .eventStart(eventDTO.getStart())
                .eventEnd(eventDTO.getEnd())
                .description(eventDTO.getDescription())
                .employee(eventDTO.getEmployee()) // 사원 정보 설정
                .build();

        eventRepository.save(event);
    }
}
