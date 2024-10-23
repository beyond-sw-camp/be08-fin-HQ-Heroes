package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/employee")
@Slf4j
public class AdminEmployeeController {

    private final EmployeeService employeeService;

    // 테스트 코드 필요
    @PutMapping("/update")
    public ResponseEntity<String> adminUpdateEmployeeInfo(
            @RequestPart("employeeData") @Validated EmployeeDTO employeeDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            // 요청 파라미터 로그 출력
            log.debug("Profile Image: {}", (profileImage != null ? profileImage.getOriginalFilename() : "No file uploaded"));

            // 프로필 이미지 파일 검증 및 처리
            if (profileImage != null && !profileImage.isEmpty()) {
                // 이미지 파일 MIME 타입 검증
                if (!profileImage.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest().body("이미지 파일만 업로드할 수 있습니다.");
                }

                // DTO에 파일 설정
                employeeDTO.setProfileImage(profileImage);
            } else {
                // 파일이 없을 경우 null 설정
                employeeDTO.setProfileImage(null);
            }

            // 사원 정보 업데이트
            Employee updatedEmployee = employeeService.adminUpdateEmployee(employeeDTO);

            return ResponseEntity.ok("사원 정보가 성공적으로 업데이트되었습니다.");
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("날짜 형식이 잘못되었습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사원 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
