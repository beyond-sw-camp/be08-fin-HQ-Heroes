package com.hq.heroes.certification.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.service.EmployeeCertificationService;
import com.hq.heroes.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee-certification")
@Tag(name = "EmployeeCertification APIs", description = "사원 자격증 관련 API 목록")
public class EmployeeCertificationController {
    private final EmployeeCertificationService employeeCertificationService;
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    // 사원 ID로 자격증 조회하기
    @GetMapping("/my-certification")
    @Operation(summary = "사원 ID로 자격증 목록 조회", description = "해당 사원의 자격증 목록을 조호한다.")
    public ResponseEntity<List<EmployeeCertificationResponseDTO>> getEmployeeCertificationByEmployeeId()    {
        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails userDetails) {
                employeeId = userDetails.getUsername();
            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authenticated user found.");
        }
        List<EmployeeCertification> employeeCertification = employeeCertificationService.getEmployeeCertificationByEmployeeId(employeeId);
        List<EmployeeCertificationResponseDTO> employeeCertificationDTOs = employeeCertification.stream().map(EmployeeCertification::toECResponseDTO).collect(Collectors.toList());

        return new ResponseEntity<>(employeeCertificationDTOs, HttpStatus.OK);
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

    @PostMapping("/my-certification")
    @Operation(summary = "사원 자격증 등록", description = "사원 자격증 정보를 받아서 등록한다.")
    public ResponseEntity<EmployeeCertificationResponseDTO> create(@RequestBody EmployeeCertificationRequestDTO requestDTO) {
        EmployeeCertification employeeCertification = employeeCertificationService.createEmployeeCertification(requestDTO);
        return new ResponseEntity<>(employeeCertification.toECResponseDTO(), HttpStatus.CREATED);
    }
}

