package com.hq.heroes.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationRepository;
import com.hq.heroes.education.service.CourseServiceImpl;
import com.hq.heroes.education.service.EducationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CourseContoller.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Slf4j
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseServiceImpl courseService;

    @MockBean
    private EducationServiceImpl educationService;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private JWTUtil jwtUtil;

    // SecurityContext 모킹 메소드 추가
    private void mockSecurityContext(String employeeId) {
        // Authentication 모킹
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(employeeId);

        // SecurityContext 모킹
        org.springframework.security.core.context.SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // SecurityContextHolder에 모킹된 SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
    }

//    @Test
//    @DisplayName("사원 ID로 신청한 교육 목록 조회 테스트 성공 GET /api/v1/course-service/my-courses - 성공")
//    public void getCourseByEmployeeId_Success() throws Exception {
//        // SecurityContext 모킹
//        mockSecurityContext("2024100001");
//
//        CourseResponseDTO courseDTO = CourseResponseDTO.builder()
//                .courseId(1L)
//                .categoryName("어학")
//                .educationName("영어가 어려운 사람들을 위한 교육!")
//                .employeeName("홍길동")
//                .startDate(LocalDate.of(2024, 10, 20))
//                .endDate(LocalDate.of(2024, 12, 21))
//                .status(CourseStatus.FAIL)
//                .build();
//
//        // 서비스가 CourseResponseDTO 객체의 리스트를 반환하도록 모킹
//        Mockito.doReturn(courseDTO).when(courseService).getCourseByEmployeeId(anyString());
//
//        // 테스트 실행 및 응답 확인
//        mockMvc.perform(get("/api/v1/course-service/my-courses"))
//                .andDo(print()) // 응답을 콘솔에 출력
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].educationName").value("영어가 어려운 사람들을 위한 교육!"))
//                .andExpect(jsonPath("$[0].status").value("FAIL")); // 상태를 FAIL로 수정
//    }
//
//
//
//    @Test
//    @DisplayName("권한이 없는 사용자가 교육 목록 조회 테스트 - 실패")
//    public void getCourseByEmployeeId_Unauthorized() throws Exception {
//        // 인증 없이 요청 실행
//        mockMvc.perform(get("/api/v1/course-service/my-courses"))
//                .andExpect(status().isUnauthorized()); // 상태 코드 401 확인
//    }


    @Test
    @DisplayName("교육 이수 성공 테스트")
    public void completeCourse_Success() throws Exception {
        // 교육 이수를 완료할 courseId 설정
        Long courseId = 1L;

        // 서비스 메서드가 호출될 때 예외가 발생하지 않도록 모킹
        doNothing().when(courseService).completeCourse(courseId);

        // POST 요청 실행 및 검증
        mockMvc.perform(post("/api/v1/course-service/complete/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("교육이 이수 되었습니다."));
    }

    @Test
    @DisplayName("교육 취소 성공 테스트")
    public void cancelEducation_Success() throws Exception {
        Long courseId = 1L; // 존재하는 courseId
        ObjectMapper objectMapper = new ObjectMapper();

        // 교육 취소가 성공적으로 이루어질 것이라고 가정
        when(educationService.cancelEducation(courseId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/course-service/cancel/{courseId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // 200 OK 응답 예상

        verify(educationService, times(1)).cancelEducation(1L); // 서비스 메서드 호출 확인
    }

    @Test
    @DisplayName("교육 취소 실패 테스트")
    public void cancelEducation_NotFound() throws Exception {
        Long courseId = 999L; // 존재하지 않는 courseId
        ObjectMapper objectMapper = new ObjectMapper();

        // 교육 취소가 실패할 것이라고 가정
        when(educationService.cancelEducation(courseId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/course-service/cancel/{courseId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404 NOT FOUND 응답 예상

        verify(educationService, times(1)).cancelEducation(1L); // 서비스 메서드 호출 확인
    }

}