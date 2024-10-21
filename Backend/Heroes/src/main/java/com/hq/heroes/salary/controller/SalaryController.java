package com.hq.heroes.salary.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "기본 급여, 보너스 관리 API")
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping("/base-salary/employee/{employeeId}")
    @Operation(summary = "직원 ID로 기본 급여 조회", description = "직원의 직책에 해당하는 기본 급여 정보를 반환합니다.")
    public ResponseEntity<Double> getBaseSalaryByPositionId(@PathVariable String employeeId) {
        Optional<Double> baseSalary = salaryService.getBaseSalaryByPositionId(employeeId);
        return baseSalary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/salaries/employee/{employeeId}")
    @Operation(summary = "직원 ID로 급여 기록 조회", description = "주어진 직원 ID에 대한 급여 기록을 반환합니다.")
    public ResponseEntity<List<SalaryDTO>> getAllSalariesByEmployeeId(@PathVariable String employeeId) {
        List<SalaryDTO> salaries = salaryService.getAllSalariesByEmployeeId(employeeId);
        if (salaries.isEmpty()) {
            return ResponseEntity.notFound().build(); // 급여 기록이 없는 경우 404 응답
        }
        return ResponseEntity.ok(salaries); // 급여 기록 반환
    }
}
