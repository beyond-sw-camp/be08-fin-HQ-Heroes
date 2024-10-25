package com.hq.heroes.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.event.dto.EventDTO;
import com.hq.heroes.event.service.EventServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventServiceImpl eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("이벤트 생성 성공 테스트")
    void createEventSuccess() throws Exception {
        // given
        EventDTO eventDTO = EventDTO.builder()
                .title("Spring Boot Workshop")
                .description("A workshop on Spring Boot and Testing")
                .start(LocalDateTime.of(2024, 10, 30, 9, 0))
                .end(LocalDateTime.of(2024, 10, 30, 17, 0))
                .build();

        Mockito.doNothing().when(eventService).createEvent(any(EventDTO.class));
        String eventJson = objectMapper.writeValueAsString(eventDTO);

        // when
        mockMvc.perform(post("/api/v1/event/create")
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))

                // then
                .andExpect(status().isCreated())
                .andExpect(content().string("이벤트가 성공적으로 생성되었습니다."));

        verify(eventService, times(1)).createEvent(any(EventDTO.class));
    }

    @Test
    @DisplayName("이벤트 생성 실패 테스트")
    void createEventFailure() throws Exception {
        // given
        EventDTO eventDTO = EventDTO.builder()
                .title("Spring Boot Workshop")
                .description("A workshop on Spring Boot and Testing")
                .start(LocalDateTime.of(2024, 10, 30, 9, 0))
                .end(LocalDateTime.of(2024, 10, 30, 17, 0))
                .build();

        doThrow(new RuntimeException("이벤트 생성 중 오류")).when(eventService).createEvent(any(EventDTO.class));
        String eventJson = objectMapper.writeValueAsString(eventDTO);

        // when
        mockMvc.perform(post("/api/v1/event/create")
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("이벤트 생성 중 오류가 발생했습니다."));

        // then
        verify(eventService, times(1)).createEvent(any(EventDTO.class));
    }
}
