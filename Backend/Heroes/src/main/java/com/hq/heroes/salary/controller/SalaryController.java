package com.hq.heroes.salary.controller;

import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.service.EvaluationService;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/salary-service")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "기본 급여, 보너스 관리 API")
public class SalaryController {

    private final SalaryService salaryService;
    private final EvaluationService evaluationService;

    @PostMapping
    @Operation(summary = "기본 급여 등록", description = "급여 정보를 받아서 기본 급여를 등록한다.")
    public ResponseEntity<SalaryDTO> createSalary(@RequestBody SalaryDTO salaryDTO) {
        SalaryDTO createdSalary;

        // 현재 월
        int currentMonth = LocalDateTime.now().getMonthValue();

        // 1, 7월일 경우 평가 점수 조회 및 전달
        if(currentMonth == 1 || currentMonth == 7) {
            // 최근 평가 데이터 가져오기
            List<Evaluation> Evaluations = evaluationService.getEvaluationsByEmployeeId(salaryDTO.getEmployeeId());

            // 평가 기록이 존재하는지 확인
            if (!Evaluations.isEmpty()) {
                createdSalary = salaryService.createSalary(salaryDTO, Evaluations);
            }
            else {
                return ResponseEntity.badRequest().body(null); // 평가가 없을 경우 에러 응답
            }
        }
        else {
            createdSalary = salaryService.createSalary(salaryDTO);
        }

        return new ResponseEntity<>(createdSalary, HttpStatus.CREATED);
    }

    @GetMapping("/base-salary/{employeeId}")
    @Operation(summary = "특정 직책의 기본 급여 조회", description = "직원의 직책에 해당하는 기본 급여 정보를 반환한다.")
    public ResponseEntity<Double> getBaseSalaryByPositionId(@PathVariable String employeeId) {
        Optional<Double> baseSalary = salaryService.getBaseSalaryByPositionId(employeeId);
        return baseSalary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/salaries/{employeeId}")
    @Operation(summary = "특정 사원의 기본 급여 조회", description = "특정 사원의 기본 급여 정보를 반환한다.")
    public ResponseEntity<List<SalaryDTO>> getAllSalariesByEmployeeId(@PathVariable String employeeId) {
        List<SalaryDTO> salaries = salaryService.getAllSalariesByEmployeeId(employeeId);
        if (salaries.isEmpty()) {
            return ResponseEntity.notFound().build(); // 급여 기록이 없는 경우 404 응답
        }
        return ResponseEntity.ok(salaries); // 급여 기록 반환
    }
}
