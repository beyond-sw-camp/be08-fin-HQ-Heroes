package com.hq.heroes.certification.controller;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.service.CertificationService;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.service.EmployeeService;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certification-service")
@Tag(name = "Certification APIs", description = "자격증 관련 API 목록")
public class CertificationController {
    private final CertificationService certificationService;
    private final NotificationService notificationService;
    private final EmployeeService employeeService;

    // 자격증 목록 조회하기
    @GetMapping("/certification")
    @Operation(summary = "자격증 목록 조회", description = "전체 자격증의 목록을 조회한다.")
    public ResponseEntity<List<CertificationResponseDTO>> getCertifications() {
        List<CertificationResponseDTO> certificationResponseDTOS = certificationService.getCertifications();

        if (!certificationResponseDTOS.isEmpty()) {
            return new ResponseEntity<>(certificationResponseDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(certificationResponseDTOS, HttpStatus.NOT_FOUND);
        }
    }

    // 자격증 상세 조회 - 테스트
    @GetMapping("/certification/{certification-id}")
    @Operation(summary = "자격증 상세 조회", description = "자격증 ID로 해당 자격증의 정보를 조회한다.")
    public ResponseEntity<CertificationResponseDTO> getCertificationById(
            @Parameter(description = "자격증 ID", example = "1")
            @PathVariable("certification-id") Long certificationId) {
        CertificationResponseDTO certificationResponseDTO = certificationService.getCertificationById(certificationId);

        if (certificationResponseDTO != null) {
            return new ResponseEntity<>(certificationResponseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 부서 이름으로 자격증 목록 조회
    @GetMapping("/by-department")
    @Operation(summary = "부서별 자격증 목록 조회", description = "부서 이름으로 자격증 목록을 조회한다.")
    public ResponseEntity<List<CertificationResponseDTO>> getCertificationsByDepartment(
            @Parameter(description = "부서 이름", example = "IT기술본부")
            @RequestParam("deptName") String deptName) {

        List<CertificationResponseDTO> certificationResponseDTOS = certificationService.getCertificationListByDeptName(deptName);

        return new ResponseEntity<>(certificationResponseDTOS, HttpStatus.OK);
    }

    @PostMapping("/certification")
    @Operation(summary = "자격증 등록", description = "자격증 정보를 받아서 등록한다.")
    public ResponseEntity<CertificationResponseDTO> create(@RequestBody CertificationRequestDTO requestDTO) {
        CertificationResponseDTO certificationResponseDTO = certificationService.createCertification(requestDTO);

        if (certificationResponseDTO != null) { // 성공적으로 자격증 생성된 경우
            List<EmployeeDTO> employeeList = employeeService.getAllEmployeesByDepartment(certificationResponseDTO.getDeptId());

            for (EmployeeDTO employeeDTO : employeeList) {
                System.out.println("employeeDTO.getEmployeeName() = " + employeeDTO.getEmployeeName());
                notificationService.sendNotificationAsync(employeeDTO.getEmployeeId(), AutoNotificationType.COMPANY_CERTIFICATION_REGISTRATION, certificationResponseDTO);
            }

            return new ResponseEntity<>(certificationResponseDTO, HttpStatus.CREATED);
        } else {
            // 자격증 생성 실패 시, 적절한 오류 메시지와 상태 코드 반환
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 자격증 정보 수정 - 테스트
    @PutMapping("/certification/{certification-id}")
    @Operation(summary = "자격증 목록 수정", description = "자격증 정보를 받아 수정한다.")
    public ResponseEntity<CertificationResponseDTO> update(
            @Parameter(description = "자격증 ID", example = "1")
            @PathVariable("certification-id") Long certificationId,
            @RequestBody CertificationRequestDTO requestDTO) {

        CertificationResponseDTO certificationResponseDTO = certificationService.updateCertification(certificationId, requestDTO);

        if (certificationResponseDTO != null) {
            return new ResponseEntity<>(certificationResponseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 자격증 삭제 - 테스트
    @DeleteMapping("/certification/{certification-id}")
    @Operation(summary = "자격증 삭제", description = "자격증 ID로 해당 자격증을 삭제한다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "자격증 ID", example = "1")
            @PathVariable("certification-id") Long certificationId) {

        boolean isDeleted = certificationService.deleteCertification(certificationId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}