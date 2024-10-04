package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "Salary APIs", description = "급여 관련 API 목록")
public class SalaryController {

    private final SalaryService salaryService;

    // 직원의 모든 급여 조회 (employeeId로 조회)
    @GetMapping("/salaries")
    @Operation(summary = "직원의 급여 조회", description = "특정 직원의 모든 급여 정보를 조회한다.")
    public ResponseEntity<List<SalaryDTO>> getSalariesByEmployeeId(@RequestParam String employeeId) {
        return ResponseEntity.ok(salaryService.getSalariesByEmployeeId(employeeId));
    }

    // 새로운 급여 생성
    @PostMapping("/salary")
    @Operation(summary = "급여 생성", description = "새로운 급여 정보를 생성한다.")
    public ResponseEntity<SalaryDTO> createSalary(@RequestBody SalaryDTO salaryDTO) {
        return new ResponseEntity<>(salaryService.createSalary(salaryDTO), HttpStatus.CREATED);
    }

    // 급여 수정
    @PatchMapping("/salary")
    @Operation(summary = "급여 수정", description = "특정 직원의 급여 정보를 수정한다.")
    public ResponseEntity<SalaryDTO> updateSalary(
            @RequestParam String employeeId,
            @RequestParam Long salaryId,
            @RequestBody SalaryDTO salaryDTO) {
        SalaryDTO updatedSalary = salaryService.updateSalaryByEmployeeAndSalaryId(employeeId, salaryId, salaryDTO);
        if (updatedSalary != null) {
            return ResponseEntity.ok(updatedSalary);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 일치하는 데이터가 없을 때
        }
    }

    // 급여 삭제
    @DeleteMapping("/salary")
    @Operation(summary = "급여 삭제", description = "특정 직원의 급여 정보를 삭제한다.")
    public ResponseEntity<Void> deleteSalary(
            @RequestParam String employeeId,
            @RequestParam Long salaryId) {
        boolean isDeleted = salaryService.deleteSalaryByEmployeeAndSalaryId(employeeId, salaryId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 삭제 성공
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 일치하는 데이터가 없을 때
        }
    }
}
