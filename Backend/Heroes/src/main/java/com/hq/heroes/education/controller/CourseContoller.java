package com.hq.heroes.education.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.service.CourseService;
import com.hq.heroes.education.service.EducationService;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-service")
@Tag(name = "Course APIs", description = "수강 관련 API 목록")
public class CourseContoller {
    private final CourseService courseService;
    private final EducationService educationService;
    private final NotificationService notificationService;

    // 사원 ID로 신청한 교육 목록 조회하기 - 테스트
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
        List<CourseResponseDTO> courseResponseDTOS = courseService.getCourseByEmployeeId(employeeId);

        // 응답 반환
        return new ResponseEntity<>(courseResponseDTOS, HttpStatus.OK);
    }

    // 교육 취소하기 - 테스트
    @DeleteMapping("/cancel/{courseId}")
    @Operation(summary = "교육 취소하기", description = "수강 ID로 교육 신청을 취소한다.")
    public ResponseEntity<Void> cancelEducation(@PathVariable Long courseId) {
        CourseResponseDTO courseResponseDTO = courseService.getCourseById(courseId);

        Map<String, Object> params = new HashMap<>();
        String receiverId = courseResponseDTO.getEmployeeId();
        params.put("receiverId", receiverId);
        notificationService.sendAutomaticNotification(AutoNotificationType.EDUCATION_CANCEL, params, courseResponseDTO);

        boolean isCancelled = educationService.cancelEducation(courseId); // 메서드 이름을 적절하게 변경하세요.
        if (isCancelled) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 교육 이수 -테스트
    @PostMapping("/complete/{courseId}")
    @Operation(summary = "교육 이수하기", description = "수강 ID로 미이수된 교육을 이수로 바꾼다.")
    public ResponseEntity<String> completeCourse(@PathVariable Long courseId) {
        CourseResponseDTO courseResponseDTO = courseService.completeCourse(courseId);

        Map<String, Object> params = new HashMap<>();
        String receiverId = courseResponseDTO.getEmployeeId();
        params.put("receiverId", receiverId);
        notificationService.sendAutomaticNotification(AutoNotificationType.EDUCATION_COMPLETION, params, courseResponseDTO);

        return ResponseEntity.ok("교육이 이수 되었습니다.");
    }

    // 수강 목록
    @GetMapping("/list")
    @Operation(summary = "수강 신청 목록 조회")
    public ResponseEntity<List<CourseResponseDTO>> getCourses() {
        List<CourseResponseDTO> lists = courseService.getAllCourses();
        return ResponseEntity.ok(lists);
    }
}