package com.hq.heroes.event.controller;

import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/my-events")
    public List<EventDTO> getMyEvents(@RequestParam String employeeId) {
        return eventService.getEventsByEmployeeId(employeeId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        boolean updated = eventService.updateEvent(id, eventDTO.getStart(), eventDTO.getEnd());
        if (updated) {
            return ResponseEntity.ok("Event updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Event not found or update failed");
        }
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok("일정시 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the event");
        }
    }


}
