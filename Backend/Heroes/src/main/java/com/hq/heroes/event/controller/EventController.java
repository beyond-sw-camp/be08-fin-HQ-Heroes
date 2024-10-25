package com.hq.heroes.event.controller;

import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // 이벤트 생성 메서드
    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            // 이벤트 생성 로직 호출
            eventService.createEvent(eventDTO);
            return new ResponseEntity<>("이벤트가 성공적으로 생성되었습니다.", HttpStatus.CREATED);  // 201 Created 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이벤트 생성 중 오류가 발생했습니다.");
        }
    }
}
