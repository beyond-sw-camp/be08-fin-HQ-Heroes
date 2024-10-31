package com.hq.heroes.certification.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.service.EmployeeCertificationService;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/employee-certification")
@Tag(name = "EmployeeCertification APIs", description = "사원 자격증 관련 API 목록")
@Slf4j
public class EmployeeCertificationController {

    private final EmployeeCertificationService employeeCertificationService;
    private final NotificationService notificationService;

    // 사원 ID로 자격증 조회하기 - 테스트
    @GetMapping("/my-certification/by-employeeId")
    @Operation(summary = "사원 ID로 사원 자격증 목록 조회", description = "해당 사원의 자격증 목록을 조회한다.")
    public ResponseEntity<List<EmployeeCertificationResponseDTO>> getMyCertificationByEmployeeId() {
        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;

                employeeId = userDetails.getUsername();
            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authentication user found.");
        }

        List<EmployeeCertificationResponseDTO> employeeCertificationResponseDTOs = employeeCertificationService.getMyCertificationByEmployeeId(employeeId);


        return new ResponseEntity<>(employeeCertificationResponseDTOs, HttpStatus.OK);
    }

    //- 테스트
    @PostMapping("/my-certification")
    @Operation(summary = "사원 자격증 등록", description = "사원 자격증 정보를 받아서 등록한다.")
    public ResponseEntity<EmployeeCertificationResponseDTO> create(@RequestBody EmployeeCertificationRequestDTO requestDTO) {
        EmployeeCertificationResponseDTO employeeCertificationResponseDTO = employeeCertificationService.createEmployeeCertification(requestDTO);

        Map<String, Object> params = new HashMap<>();
        String receiverId = employeeCertificationResponseDTO.getEmployeeId();
        params.put("receiverId", receiverId);
        notificationService.sendAutomaticNotification(AutoNotificationType.EMPLOYEE_CERTIFICATION_REGISTRATION, params, employeeCertificationResponseDTO);

        return new ResponseEntity<>(employeeCertificationResponseDTO, HttpStatus.CREATED);
    }

    // 자격증 등록 완료
    @PostMapping("/complete/{registrationId}")
    @Operation(summary = "사원 자격증 승인하기", description = "등록 ID로 사원이 신청한 자격증이 등록되도록 한다.")
    public ResponseEntity<String> completeCertification(@PathVariable Long registrationId) {
        EmployeeCertificationResponseDTO employeeCertificationResponseDTO = employeeCertificationService.completeCertification(registrationId);

        Map<String, Object> params = new HashMap<>();
        String receiverId = employeeCertificationResponseDTO.getEmployeeId();
        params.put("receiverId", receiverId);
        notificationService.sendAutomaticNotification(AutoNotificationType.EMPLOYEE_CERTIFICATION_APPROVAL, params, employeeCertificationResponseDTO);

        return ResponseEntity.ok("사원 자격증 등록이 완료되었습니다.");
    }

    // 사원 자격증 목록
    @GetMapping("/certification-list")
    @Operation(summary = "등록된 사원 자격증 목록 조회")
    public ResponseEntity<List<EmployeeCertificationResponseDTO>> getCertificationList() {
        List<EmployeeCertificationResponseDTO> lists = employeeCertificationService.getAllCertification();
        return ResponseEntity.ok(lists);
    }
}

