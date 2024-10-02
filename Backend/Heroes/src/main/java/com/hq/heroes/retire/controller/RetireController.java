package com.hq.heroes.retire.controller;

import com.hq.heroes.retire.dto.RetireResponseDTO;
import com.hq.heroes.retire.service.RetireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/retire-service")
@Tag(name = "Retire APIs", description = "퇴직금 관련 API 목록")
public class RetireController {

    private final RetireService retireService;

    // 퇴직금 정보 조회
    @GetMapping("/retire")
    @Operation(summary = "퇴직금 정보 조회", description = "payroll_avg, period, annual_bonus를 조회한다.")
    public ResponseEntity<RetireResponseDTO> getRetirementPay(@RequestParam String employeeId) {
        RetireResponseDTO retireDTO = retireService.getRetirementPayByEmployeeId(employeeId);

        if (retireDTO != null) {
            return new ResponseEntity<>(retireDTO, HttpStatus.OK); // 정상 조회시 OK 반환
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 데이터 없을시 NOT_FOUND 반환
        }
    }

    // 퇴직금 수정
    @PutMapping("/retire")
    @Operation(summary = "퇴직금 수정", description = "퇴직금 정보를 수정한다.")
    public ResponseEntity<RetireResponseDTO> updateRetirementPay(@RequestParam String employeeId, @RequestBody RetireResponseDTO retireResponseDTO) {
        RetireResponseDTO updatedRetireDTO = retireService.updateRetirementPay(employeeId, retireResponseDTO);
        if (updatedRetireDTO != null) {
            return new ResponseEntity<>(updatedRetireDTO, HttpStatus.OK); // 정상 수정시 OK 반환
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 데이터 없을시 NOT_FOUND 반환
        }
    }

    // 퇴직금 삭제
    @DeleteMapping("/retire")
    @Operation(summary = "퇴직금 삭제", description = "퇴직금 정보를 삭제한다.")
    public ResponseEntity<Void> deleteRetirementPay(@RequestParam String employeeId) {
        boolean isDeleted = retireService.deleteRetirementPay(employeeId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 삭제 성공시 NO_CONTENT 반환
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 데이터 없을시 NOT_FOUND 반환
        }
    }
}

