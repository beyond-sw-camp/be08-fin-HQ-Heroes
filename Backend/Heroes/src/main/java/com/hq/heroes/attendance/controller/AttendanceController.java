package com.hq.heroes.attendance.controller;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/my-attendance")
    @Operation(summary = "로그인한 사용자의 근태 기록 조회")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendanceRecords(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
            String employeeId = userDetails.getUsername();  // employeeId 가져오기

            List<AttendanceDTO> records = attendanceService.getAttendancesByEmployeeId(employeeId);
            return ResponseEntity.ok(records);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // 출근 요청
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn() {
        // 인증된 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                String employeeId = userDetails.getUsername();

                // employeeId를 이용해 출근 처리
                Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);
                if (employeeOptional.isPresent()) {
                    Attendance attendance = attendanceService.checkIn(employeeOptional.get());  // 출근 처리 후 Attendance 객체 반환
                    // 출근 후 생성된 attendanceId를 클라이언트로 반환
                    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("attendanceId", attendance.getAttendanceId()));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



    // 퇴근 요청
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                String employeeId = userDetails.getUsername();

                Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);
                if (employeeOptional.isPresent()) {
                    attendanceService.checkOut(employeeOptional.get());
                    return ResponseEntity.ok().build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 출근 상태 확인 요청
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
            String employeeId = userDetails.getUsername();

            boolean isCheckedIn = attendanceService.isAlreadyCheckedIn(employeeId);
            return ResponseEntity.ok(Map.of("isCheckedIn", isCheckedIn));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}