package com.hq.heroes.event.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.entity.Event;
import com.hq.heroes.event.repository.EventRepository;
import com.hq.heroes.vacation.entity.Vacation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EmployeeRepository employeeRepository;


    @Override
    public void createEvent(EventDTO eventDTO) {

        // 유효성 검사 추가: 시작 시간이 종료 시간보다 늦으면 예외 발생
        if (eventDTO.getStart().isAfter(eventDTO.getEnd())) {
            throw new IllegalArgumentException("이벤트 시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

        // employeeId로 Employee를 조회하여 설정
        Employee employee = employeeRepository.findById(eventDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사원 ID입니다."));

        Event event = Event.builder()
                .eventTitle(eventDTO.getTitle())
                .eventStart(eventDTO.getStart())
                .eventEnd(eventDTO.getEnd())
                .description(eventDTO.getDescription())
                .employee(employee) // 조회된 Employee 설정
                .build();

        eventRepository.save(event);
    }

    @Override
    public List<EventDTO> getEventsByEmployeeId(String employeeId) {
        return eventRepository.findByEmployee_EmployeeId(employeeId).stream()
                .map(event -> EventDTO.builder()
                        .id(event.getEventId())
                        .title(event.getEventTitle())
                        .start(event.getEventStart())
                        .end(event.getEventEnd())
                        .description(event.getDescription())
                        .employeeId(event.getEmployee().getEmployeeId())
                        .employeeName(event.getEmployee().getEmployeeName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateEvent(Long id, LocalDateTime start, LocalDateTime end) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setEventStart(start);
            event.setEventEnd(end);
            eventRepository.save(event);
            return true;
        }
        return false;
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 휴가 요청을 찾을 수 없습니다."));

        eventRepository.delete(event);
    }


}
