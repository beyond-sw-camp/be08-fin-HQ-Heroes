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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/pending-quarters/{employeeId}")
    public ResponseEntity<Long> getPendingVacationQuarters(@PathVariable String employeeId) {
        Long pendingQuarters = vacationService.getPendingVacationQuarters(employeeId);
        return ResponseEntity.ok(pendingQuarters);
    }

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
            Vacation vacation = vacationService.submitVacation(vacationDTO);

            // 자동 알림 발송
            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.getApprover().getEmployeeId()); // 승인자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_APPLICATION, params, vacation);

            return ResponseEntity.ok("휴가 신청이 성공적으로 제출되었습니다.");
        } catch (ResponseStatusException e) {
            // ResponseStatusException을 별도로 처리하여 409 상태 코드 전달
            System.out.println("Conflict error: " + e.getReason());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getReason());
        } catch (Exception e) {
            System.out.println("Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body("휴가 신청 중 오류가 발생했습니다.");
        }
    }

    // 휴가 취소 신청
    @PostMapping("/cancel")
    public ResponseEntity<String> requestVacationCancel(@RequestBody VacationDTO vacationDTO) {
        try {
            Vacation vacation = vacationService.cancelVacation(vacationDTO);

            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.getApprover().getEmployeeId()); // 결재자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_CANCEL_REQUEST, params, vacation);

            return ResponseEntity.ok("휴가 취소 요청이 성공적으로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴가 취소 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 휴가 취소 승인
    @PostMapping("/approveCancel/{vacationId}")
    public ResponseEntity<String> approveCancelVacation(@PathVariable Long vacationId) {
        try {
            vacationService.approveCancelVacation(vacationId);

            // 자동 알림 발송
            Vacation vacation = vacationRepository.findById(vacationId)
                    .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.getApplicant().getEmployeeId()); // 신청자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_CANCELLATION_APPROVAL, params, vacation);

            return ResponseEntity.ok("휴가 취소가 성공적으로 승인되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("해당 휴가를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴가 취소 승인 중 오류가 발생했습니다.");
        }
    }

    // 휴가 취소 반려
    @PostMapping("/rejectCancel/{vacationId}")
    public ResponseEntity<String> rejectCancelVacation(@PathVariable Long vacationId) {
        try {
            vacationService.rejectCancelVacation(vacationId);

            // 자동 알림 발송
            Vacation vacation = vacationRepository.findById(vacationId)
                    .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

            Map<String, Object> params = new HashMap<>();
            params.put("receiverId", vacation.getApplicant().getEmployeeId()); // 신청자 ID
            notificationService.sendAutomaticNotification(AutoNotificationType.VACATION_CANCELLATION_REJECTION, params, vacation);

            return ResponseEntity.ok("휴가 취소가 성공적으로 반려되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("해당 휴가를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴가 취소 반려 중 오류가 발생했습니다.");
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

    // 팀원의 휴가 정보를 조회하는 엔드포인트
    @GetMapping("/team-vacations")
    public ResponseEntity<List<VacationDTO>> getTeamVacations(@RequestParam String employeeId) {
        List<VacationDTO> teamVacations = vacationService.getTeamVacations(employeeId);
        return ResponseEntity.ok(teamVacations);
    }

    // 대기 중인 휴가 요청 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteVacation(@RequestParam Long vacationId) {
        try {
            vacationService.deleteVacation(vacationId);
            return ResponseEntity.ok("대기 중인 휴가 요청이 성공적으로 삭제되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("삭제할 수 없는 상태입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("휴가 삭제 중 오류가 발생했습니다.");
        }
    }
}
