package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.dto.PasswordUpdateDTO;
import com.hq.heroes.employee.dto.TeamDTO;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRoleRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import com.hq.heroes.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final JobRoleRepository jobRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;

    // 특정 사원 조회를 위한 API
    @GetMapping("/employees/{employee-id}")
    public ResponseEntity<EmployeeDTO> getAllEmployee(
            @Parameter(description = "사원 ID", example = "2024106824") @PathVariable("employee-id") String employeeId
    ) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);

        if (employeeDTO != null) {
            return ResponseEntity.ok(employeeDTO);
        } else {
            return ResponseEntity.notFound().build(); // 수정된 부분
        }
    }

    // 모든 사원 조회를 위한 API
    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        // 사원이 없을 경우 메시지와 함께 200 OK 반환
        if (employees.isEmpty()) {
            return ResponseEntity.ok("사원이 없습니다.");
        }

        return ResponseEntity.ok(employees);
    }

    // status 가 'ACTIVE' 인 모든 사원 조회를 위한 API
    @GetMapping("/active/employees")
    public ResponseEntity<?> getAllEmployeesWithActive() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees()
                .stream().filter(employee -> employee.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());

        // 사원이 없을 경우 메시지와 함께 200 OK 반환
        if (employees.isEmpty()) {
            return ResponseEntity.ok("사원이 없습니다.");
        }

        return ResponseEntity.ok(employees);
    }

    // 부서 조회를 위한 API
    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAllDepartments());
    }

    // 팀 조회를 위한 API
    @GetMapping("/teams")
    public ResponseEntity<?> getTeamsByDepartment(@RequestParam(required = false) Long deptId) {

        List<TeamDTO> teams;

        if (deptId != null) {
            teams = teamRepository.findByDepartment_DeptId(deptId); // 부서 ID로 팀을 조회
        } else {
            teams = teamRepository.findAllTeams(); // 모든 팀 조회
        }

        return ResponseEntity.ok(teams);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getJobRoles() {
        return ResponseEntity.ok((jobRepository.findAllJobRoles()));
    }

    @GetMapping("/positions")
    public ResponseEntity<?> getPositions() {
        return ResponseEntity.ok((positionRepository.findAllPositions()));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateEmployeeInfo(
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
            Employee updatedEmployee = employeeService.updateEmployee(employeeDTO);
            return ResponseEntity.ok("사원 정보가 성공적으로 업데이트되었습니다.");
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("날짜 형식이 잘못되었습니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사원 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 비밀번호 변경을 위한 API
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {

        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();
            } else {
                log.debug("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            log.debug("No authenticated user found.");
        }

        try {
            boolean isUpdated = employeeService.updatePassword(
                    employeeId,
                    passwordUpdateDTO.getCurrentPassword(),
                    passwordUpdateDTO.getNewPassword()
            );

            if (isUpdated) {
                return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 변경되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 현재 로그인한 사용자의 역할과 positionId를 확인하는 API
    @GetMapping("/role-check")
    public ResponseEntity<Map<String, Object>> checkUserRoleAndPosition() {
        String employeeId = "";
        String employeeName = "";  // 로그인한 사용자 이름 추가
        String role = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();
                employeeName = userDetails.getUsername();
                role = userDetails.getRole();
                log.debug("User is authenticated: {}, Role: {}", employeeId, role);
            } else {
                log.debug("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            log.debug("No authenticated user found.");
        }

        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);
        log.debug("Employee found: {}", employeeOptional.isPresent()); // 조회 결과 확인

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            Map<String, Object> response = Map.of(
                    "employeeId", employee.getEmployeeId(),  // employeeId 추가 반환
                    "employeeName", employee.getEmployeeName(),
                    "role", employee.getRole().toString(),  // 역할: ROLE_ADMIN, ROLE_USER 등
                    "positionId", employee.getPosition().getPositionId() // positionId
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "사용자를 찾을 수 없습니다."));
        }
    }



}