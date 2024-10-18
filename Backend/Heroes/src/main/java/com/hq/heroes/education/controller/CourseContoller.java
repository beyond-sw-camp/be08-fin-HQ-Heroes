package com.hq.heroes.education.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.service.CourseService;
import com.hq.heroes.education.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-service")
@Tag(name = "Course APIs", description = "수강 관련 API 목록")
public class CourseContoller {
    private final CourseService courseService;
    private final EducationService educationService;

    // 사원 ID로 신청한 교육 목록 조회하기
    @GetMapping("/my-courses")
    @Operation(summary = "사원 ID로 신청한 교육 목록 조회", description = "해당 사원이 신청한 교육 목록을 조회한다.")
    public ResponseEntity<List<CourseResponseDTO>> getCourseByEmployeeId() {
        String employeeId = "";

        // Spring Security를 통해 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("authentication = " + authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();

                System.out.println("employeeId = " + employeeId);

            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authenticated user found.");
        }

        // 해당 사원의 신청한 교육 목록을 서비스에서 조회
        List<Course> courses = courseService.getCourseByEmployeeId(employeeId);

        // Course 엔티티를 CourseResponseDTO로 변환
        List<CourseResponseDTO> courseDTOs = courses.stream()
                .map(Course::toResponseDTO)
                .collect(Collectors.toList());

        // 응답 반환
        return new ResponseEntity<>(courseDTOs, HttpStatus.OK);
    }
}