package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "Salary APIs", description = "급여 관리 API 목록")
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping("/salaries")
    @Operation(summary = "특정 직원의 연도별 급여 조회", description = "특정 직원의 특정 연도 급여 정보를 조회합니다.")
    public List<SalaryDTO> getSalariesByEmployeeIdAndYear(@RequestParam String employeeId, @RequestParam int year) {
        return salaryService.getSalariesUpToCurrentMonth(employeeId, year);
    }

    @GetMapping("/salaries/month")
    @Operation(summary = "특정 월의 급여 조회", description = "특정 직원의 특정 월의 급여 정보를 조회합니다.")
    public SalaryDTO getSalaryByEmployeeIdAndMonth(@RequestParam String employeeId, @RequestParam int year, @RequestParam int month) {
        return salaryService.getSalaryByEmployeeIdAndMonth(employeeId, year, month);
    }

    @PostMapping
    @Operation(summary = "급여 생성", description = "새로운 급여 정보를 생성합니다.")
    public SalaryDTO createSalary(@RequestBody SalaryDTO salaryDto) {
        return salaryService.createSalary(salaryDto);
    }

    @PutMapping("/{salaryId}")
    @Operation(summary = "급여 수정", description = "급여 ID를 기반으로 급여 정보를 수정합니다.")
    public SalaryDTO updateSalary(@PathVariable Long salaryId, @RequestBody SalaryDTO salaryDto) {
        return salaryService.updateSalary(salaryId, salaryDto);
    }

    @DeleteMapping("/{salaryId}")
    @Operation(summary = "급여 삭제", description = "급여 ID를 기반으로 급여 정보를 삭제합니다.")
    public void deleteSalary(@PathVariable Long salaryId) {
        salaryService.deleteSalary(salaryId);
    }
}
