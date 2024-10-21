package com.hq.heroes.salary.controller;

import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.service.SalaryHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/salary-service/histories")
@RequiredArgsConstructor
@Tag(name = "SalaryHistory APIs", description = "급여 기록 관리 API 목록")
public class SalaryHistoryController {

    private final SalaryHistoryService salaryHistoryService;
    private final AttendanceService attendanceService;

    @GetMapping("/{employeeId}")
    @Operation(summary = "로그인한 사용자의 전체 급여 기록 조회")
    public List<SalaryHistoryDTO> getAllSalaryHistories(
            @PathVariable String employeeId) {  // 로그인한 사용자의 ID를 주입
        return salaryHistoryService.getAllSalaries(employeeId);
    }

    @PostMapping
    @Operation(summary = "급여 생성")
    public SalaryHistoryDTO createSalary(@RequestBody SalaryHistoryDTO SalaryHistoryDTO) {
        return salaryHistoryService.createSalary(SalaryHistoryDTO);
    }

    @GetMapping("/last-three-months/sum/{employeeId}")
    @Operation(summary = "마지막 출퇴근 날짜로부터 3개월 동안의 급여 합계 조회")
    public ResponseEntity<Double> getLastThreeMonthsSalarySum(@PathVariable String employeeId) {
        Double totalSalary = salaryHistoryService.getLastThreeMonthsSalarySum(employeeId);
        return ResponseEntity.ok(totalSalary);
    }

}
