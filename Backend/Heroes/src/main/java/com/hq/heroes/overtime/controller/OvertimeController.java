package com.hq.heroes.overtime.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.overtime.dto.OvertimeDTO;
import com.hq.heroes.overtime.service.OvertimeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/submit")
    @Operation(summary = "연장 근로 신청")
    public ResponseEntity<String> submitOvertime(@RequestBody OvertimeDTO overtimeDTO) {
        try {
            overtimeService.submitOvertime(overtimeDTO);
            return ResponseEntity.ok("연장 근로 신청이 성공적으로 제출되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("연장 근로 신청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 연장 근로 승인
    @PostMapping("/approve/{overtimeId}")
    public ResponseEntity<String> approveOvertime(@PathVariable Long overtimeId) {
        overtimeService.approveOvertime(overtimeId);
        return ResponseEntity.ok("연장 근로가 승인되었습니다.");
    }

    // 연장 근로 반려
    @PostMapping("/reject/{overtimeId}")
    public ResponseEntity<String> rejectOvertime(@PathVariable Long overtimeId) {
        overtimeService.rejectOvertime(overtimeId);
        return ResponseEntity.ok("연장 근로가 반려되었습니다.");
    }

    @GetMapping("/list")
    @Operation(summary = "연장 근로 신청 목록 조회")
    public ResponseEntity<List<OvertimeDTO>> getOvertimeList() {
        List<OvertimeDTO> lists = overtimeService.getAllOvertimes();
        return ResponseEntity.ok(lists);
    }

    // 로그인된 사용자 정보 가져오기
    @GetMapping("/username")
    @Operation(summary = "로그인된 사용자 이름 조회")
    public ResponseEntity<String> getLoggedInUser() {
        // SecurityContextHolder를 사용하여 현재 로그인한 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            // UserDetails에서 사용자 ID (employeeId) 가져오기
            String employeeId = ((UserDetails) principal).getUsername(); // ID를 사용자 ID로 설정
            // employeeId를 통해 Employee 엔티티에서 사용자 이름 가져오기
            Employee employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            username = employee.getEmployeeName(); // 이름을 가져옴
        } else {
            username = principal.toString();
        }

        return ResponseEntity.ok(username);
    }

    // 로그인된 사용자의 승인된 연장 근로 목록을 반환하는 API - 테스트
    @GetMapping("/my-overtimes")
    @Operation(summary = "로그인된 사용자의 승인된 연장 근로 내역 조회")
    public ResponseEntity<List<OvertimeDTO>> getMyApprovedOvertimes() {
        // SecurityContextHolder에서 로그인된 사용자의 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String employeeId = "";

        if (principal instanceof UserDetails) {
            employeeId = ((UserDetails) principal).getUsername(); // employeeId 추출
        }

        // 해당 사용자(employeeId)의 승인된 연장 근로 목록 조회
        List<OvertimeDTO> approvedOvertimes = overtimeService.getApprovedOvertimesByEmployeeId(employeeId);

        return ResponseEntity.ok(approvedOvertimes);
    }

    //- 테스트
    @GetMapping("/total-overtime")
    @Operation(summary = "해당 월의 연장 근로 내역 조회")
    public ResponseEntity<Long> getTotalOvertimeForMonth(@RequestParam String employeeId, @RequestParam String yearMonth) {
        YearMonth month = YearMonth.parse(yearMonth);  // "yyyy-MM" 형식의 파라미터를 YearMonth로 변환
        long totalHours = overtimeService.getTotalOvertimeHoursForMonth(employeeId, month);
        return ResponseEntity.ok(totalHours);
    }
}
