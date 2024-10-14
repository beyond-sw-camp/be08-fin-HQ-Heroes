package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.DeductDTO;
import com.hq.heroes.salary.service.DeductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deduct-service/deductions")
@RequiredArgsConstructor
@Tag(name = "Deduction APIs", description = "공제 관련 API 목록")
public class DeductController {

    private final DeductService deductService;

    @GetMapping
    @Operation(summary = "공제 항목 조회", description = "모든 공제 항목을 조회한다.")
    public ResponseEntity<List<DeductDTO>> getAllDeductions() {
        List<DeductDTO> deductions = deductService.getAllDeducts();
        return ResponseEntity.ok(deductions);
    }
}