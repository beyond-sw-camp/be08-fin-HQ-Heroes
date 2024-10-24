package com.hq.heroes.event.service;

import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.entity.Event;
import com.hq.heroes.event.repository.EventRepository;
import com.hq.heroes.auth.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventDTO eventDTO;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("안유진")
                .build();

        eventDTO = EventDTO.builder()
                .title("Team Meeting")
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 1, 12, 0))
                .description("Discuss Q1 goals")
                .employee(employee)  // 사원 정보 추가
                .build();
    }

    @Test
    @DisplayName("이벤트 생성 성공 테스트")
    void createEventSuccess() {
        // when: 이벤트 생성 메서드 호출
        eventService.createEvent(eventDTO);

        // then: 이벤트 저장 여부 확인
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("이벤트 생성 실패 테스트 - 시작 시간이 종료 시간보다 늦음")
    void createEventFailInvalidTime() {
        // given: 잘못된 시간 설정
        eventDTO.setStart(LocalDateTime.of(2024, 1, 1, 14, 0));
        eventDTO.setEnd(LocalDateTime.of(2024, 1, 1, 12, 0));

        // when & then: 예외 발생 확인
        assertThatThrownBy(() -> eventService.createEvent(eventDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 시작 시간이 종료 시간보다 늦을 수 없습니다.");

        // 이벤트 저장이 호출되지 않았는지 확인
        verify(eventRepository, never()).save(any(Event.class));
    }
}
