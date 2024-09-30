package com.hq.heroes.vacation.controller;

import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.service.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    @PostMapping("/submit")
    @Operation(summary = "휴가 신청")
    public ResponseEntity<String> submitVacation(@RequestBody VacationDTO vacationDTO) {
        try {
            vacationService.submitVacation(vacationDTO);
            return ResponseEntity.ok("휴가 신청이 성공적으로 제출되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("휴가 신청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "휴가 신청 목록 조회")
    public ResponseEntity<List<VacationDTO>> getVacationList() {
        List<VacationDTO> lists = vacationService.getAllVacations();
        return ResponseEntity.ok(lists);
    }
}
