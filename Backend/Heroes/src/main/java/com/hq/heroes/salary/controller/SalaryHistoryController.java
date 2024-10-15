package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.service.SalaryHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "SalaryHistory APIs", description = "급여 기록 관리 API 목록")
public class SalaryHistoryController {

    private final SalaryHistoryService salaryHistoryService;

    @GetMapping("/histories")
    @Operation(summary = "특정 직원의 연도별 급여 기록 조회")
    public List<SalaryHistoryDTO> getSalariesByEmployeeIdAndYear(
            @RequestParam String employeeId, @RequestParam int year) {
        return salaryHistoryService.getSalariesUpToCurrentMonth(employeeId, year);
    }

    @GetMapping("/histories/month")
    @Operation(summary = "해당 년도의 해당 월 급여 기록 조회")
    public SalaryHistoryDTO getSalaryByEmployeeIdAndMonth(
            @RequestParam String employeeId, @RequestParam int year, @RequestParam int month) {
        return salaryHistoryService.getSalaryByEmployeeIdAndMonth(employeeId, year, month);
    }

    @PostMapping
    @Operation(summary = "급여 생성")
    public SalaryHistoryDTO createSalary(@RequestBody SalaryHistoryDTO SalaryHistoryDTO) {
        return salaryHistoryService.createSalary(SalaryHistoryDTO);
    }
}
