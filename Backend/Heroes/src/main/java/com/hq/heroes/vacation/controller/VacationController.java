package com.hq.heroes.vacation.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.repository.VacationRepository;
import com.hq.heroes.vacation.service.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;  // NotificationService 주입
    private final VacationRepository vacationRepository;

    // 휴가 신청
    @PostMapping("/submit")
    public ResponseEntity<String> submitVacation(@RequestBody VacationDTO vacationDTO) {
        try {
            if (vacationDTO.getEmployeeId() == null || vacationDTO.getApproverName() == null ||
                    vacationDTO.getVacationStartDate() == null || vacationDTO.getVacationEndDate() == null ||
                    vacationDTO.getVacationStartTime() == null || vacationDTO.getVacationEndTime() == null) {
                return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
            }

            // 휴가 신청 처리
            vacationService.submitVacation(vacationDTO);

            // 자동 알림 발송
            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacationDTO.getApproverId()); // 승인자 ID
            Optional<Vacation> vacation = vacationRepository.findById(vacationDTO.getVacationId());
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_APPLICATION, params, vacation.get());

            return ResponseEntity.ok("휴가 신청이 성공적으로 제출되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("휴가 신청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 휴가 승인
    @PostMapping("/approve/{vacationId}")
    public ResponseEntity<String> approveVacation(@PathVariable Long vacationId) {
        try {
            vacationService.approveVacation(vacationId);

            // 자동 알림 발송
            Optional<Vacation> vacation = vacationRepository.findById(vacationId);
            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.get().getApplicant().getEmployeeId()); // 신청자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_APPROVAL, params, vacation.get());

            return ResponseEntity.ok("휴가가 성공적으로 승인되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("해당 휴가를 찾을 수 없습니다.");
        }
    }

    // 휴가 반려
    @PostMapping("/reject/{vacationId}")
    public ResponseEntity<String> rejectVacation(@PathVariable Long vacationId) {
        try {
            vacationService.rejectVacation(vacationId);

            // 자동 알림 발송
            Optional<Vacation> vacation = vacationRepository.findById(vacationId);
            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.get().getApplicant().getEmployeeId()); // 신청자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_REJECTION, params, vacation.get());

            return ResponseEntity.ok("휴가가 성공적으로 거절되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("해당 휴가를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "휴가 신청 목록 조회")
    public ResponseEntity<List<VacationDTO>> getVacationList() {
        List<VacationDTO> lists = vacationService.getAllVacations();
        return ResponseEntity.ok(lists);
    }

    // 로그인된 사용자 정보 가져오기
    @GetMapping("/username")
    @Operation(summary = "로그인된 사용자 이름 조회")
    public ResponseEntity<String> getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            String employeeId = ((UserDetails) principal).getUsername();
            Employee employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            username = employee.getEmployeeName();
        } else {
            username = principal.toString();
        }

        return ResponseEntity.ok(username);
    }

    // 로그인된 사용자의 승인된 휴가 목록을 반환하는 API
    @GetMapping("/my-vacations")
    @Operation(summary = "로그인된 사용자의 승인된 휴가 내역 조회")
    public ResponseEntity<List<VacationDTO>> getMyApprovedVacations() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String employeeId = "";

        if (principal instanceof UserDetails) {
            employeeId = ((UserDetails) principal).getUsername();
        }

        List<VacationDTO> approvedVacations = vacationService.getApprovedVacationsByEmployeeId(employeeId);
        return ResponseEntity.ok(approvedVacations);
    }
}
