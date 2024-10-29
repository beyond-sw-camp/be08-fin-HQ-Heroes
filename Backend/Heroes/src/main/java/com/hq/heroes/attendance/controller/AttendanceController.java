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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
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

    // 특정 사원의 월별 총 근무 시간 조회
    @GetMapping("/total-work-hours")
    @Operation(summary = "로그인한 사용자의 월별 총 근무 시간 조회")
    public ResponseEntity<Integer> getTotalWorkHours(
            Authentication authentication,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
            String employeeId = userDetails.getUsername();

            // 입력된 연월에서 한 달 전의 YearMonth 생성
            YearMonth targetMonth = YearMonth.of(year, month).minusMonths(1);

            int totalWorkHours = attendanceService.calculateTotalWorkHours(employeeId, targetMonth);

            return ResponseEntity.ok(totalWorkHours);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/my-work-time")
    @Operation(summary = "로그인한 사용자의 근무 시간 조회")
    public ResponseEntity<Map<String, Double>> getMyWorkTime(Authentication authentication, @RequestParam YearMonth yearMonth) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomEmployeeDetails userDetails = (CustomEmployeeDetails) authentication.getPrincipal();
            String employeeId = userDetails.getUsername(); // employeeId 가져오기

            // YearMonth를 기반으로 해당 기간의 첫날과 마지막 날 계산
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            // 해당 기간 동안의 근태 기록 조회
            List<AttendanceDTO> records = attendanceService.findByEmployee_IdAndDateBetween(employeeId, startDate, endDate);

            // 총 근무 시간 계산
            double totalWorkHours = records.stream()
                    .mapToDouble(AttendanceDTO::getWorkHours)
                    .sum();

            return ResponseEntity.ok(Map.of("totalWorkHours", totalWorkHours));
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

    @GetMapping("/latest/{employeeId}")
    public ResponseEntity<AttendanceDTO> getLatestAttendance(@PathVariable String employeeId) {
        AttendanceDTO attendanceDTO = attendanceService.getLatestAttendance(employeeId);
        return ResponseEntity.ok(attendanceDTO);
    }

    @GetMapping("/latest-record/{employeeId}")
    public ResponseEntity<AttendanceDTO> getLatestAttendanceRecord(@PathVariable String employeeId) {
        // 사용자 ID를 사용하여 최신 출석 기록 가져오기
        AttendanceDTO latestAttendance = attendanceService.getLatestAttendanceRecord(employeeId);

        if (latestAttendance != null) {
            return ResponseEntity.ok(latestAttendance);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 데이터가 없는 경우 404 응답
        }
    }

}