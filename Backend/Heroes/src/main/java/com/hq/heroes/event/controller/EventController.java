package com.hq.heroes.event.controller;

import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {

    private final EventService eventService;

    // 이벤트 생성 메서드 - 테스트
    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            // 이벤트 생성 로직 호출
            eventService.createEvent(eventDTO);
            return ResponseEntity.ok("이벤트가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("이벤트 생성 중 오류가 발생했습니다.");
        }
    }
}
