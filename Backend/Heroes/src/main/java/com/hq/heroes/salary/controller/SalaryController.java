package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "기본 급여, 보너스 관리 API")
public class SalaryController {

    private final SalaryService salaryService;

    // 년도만 비교
    @GetMapping("/salaries/{employeeId}/{year}")
    @Operation(summary = "직원 ID와 년도로 급여 조회", description = "주어진 직원 ID와 년도에 해당하는 급여 정보를 반환합니다.")
    public ResponseEntity<List<SalaryDTO>> getSalariesByEmployeeIdAndYear(
            @PathVariable String employeeId,
            @PathVariable int year) {
        List<SalaryDTO> salaries = salaryService.getSalariesByEmployeeIdAndYear(employeeId, year);
        return ResponseEntity.ok(salaries);
    }

    // 년도 및 월 비교
    @GetMapping("/salaries/{employeeId}/year/{year}/month/{month}")
    @Operation(summary = "직원 ID와 년도 및 월로 급여 조회", description = "주어진 직원 ID, 년도 및 월에 해당하는 급여 정보를 반환합니다.")
    public ResponseEntity<List<SalaryDTO>> getSalariesByEmployeeIdAndYearAndMonth(
            @PathVariable String employeeId,
            @PathVariable int year,
            @PathVariable int month) {
        List<SalaryDTO> salaries = salaryService.getSalariesByEmployeeIdAndYearAndMonth(employeeId, year, month);
        return ResponseEntity.ok(salaries);
    }
}
