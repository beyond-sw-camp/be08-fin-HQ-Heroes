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

        // 유효성 검사 추가: 시작 시간이 종료 시간보다 늦으면 예외 발생
        if (eventDTO.getStart().isAfter(eventDTO.getEnd())) {
            throw new IllegalArgumentException("이벤트 시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

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
