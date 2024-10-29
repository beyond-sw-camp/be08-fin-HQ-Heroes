package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.service.SalaryHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-service/histories")
@RequiredArgsConstructor
@Tag(name = "SalaryHistory APIs", description = "급여 기록 관리 API 목록")
public class SalaryHistoryController {

    private final SalaryHistoryService salaryHistoryService;

    @GetMapping("/{employeeId}")
    @Operation(summary = "특정 사원의 전체 급여 기록 조회", description = "특정 사원의 전체 급여 기록을 조회한다.")
    public List<SalaryHistoryDTO> getAllSalaryHistories(
            @PathVariable String employeeId) {  // 로그인한 사용자의 ID를 주입
        return salaryHistoryService.getAllSalaries(employeeId);
    }

    // 배치 생성
    @PostMapping
    @Operation(summary = "급여 생성", description = "급여를 생성한다.")
    public SalaryHistoryDTO createSalary(@RequestBody SalaryHistoryDTO SalaryHistoryDTO) {
        return salaryHistoryService.createSalary(SalaryHistoryDTO);
    }

    @GetMapping("/last-three-months/{employeeId}")
    @Operation(summary = "3개월간의 급여 합계 조회", description = "마지막 출퇴근 날짜로부터 3개월 동안의 급여 합계를 조회한다.")
    public ResponseEntity<Double> getLastThreeMonthsSalarySum(@PathVariable String employeeId) {
        Double totalSalary = salaryHistoryService.getLastThreeMonthsSalarySum(employeeId);
        return ResponseEntity.ok(totalSalary);
    }

}
