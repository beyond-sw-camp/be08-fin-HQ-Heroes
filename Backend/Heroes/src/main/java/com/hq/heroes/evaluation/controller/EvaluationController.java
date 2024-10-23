package com.hq.heroes.evaluation.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/evaluation-service")
@Tag(name = "Evaluation APIs", description = "평가 관련 API 목록")
public class EvaluationController {

    private final EvaluationService evaluationService;

    // 평가 목록 조회
    @GetMapping("/evaluations")
    @Operation(summary = "평가 목록 조회", description = "전체 평가 목록을 조회한다.")
    public ResponseEntity<List<EvaluationResDTO>> getEvaluations() {
        List<Evaluation> evaluations = evaluationService.getEvaluations();
        List<EvaluationResDTO> evaluationDTOs = evaluations.stream().map(Evaluation::toResponseDTO).collect(Collectors.toList());

        return new ResponseEntity<>(evaluationDTOs, HttpStatus.OK);
    }

    //- 테스트
    @GetMapping("/evaluations/by-employeeId")
    @Operation(summary = "사원 ID로 평가 목록 조회", description = "해당 사원의 평가 목록을 조회한다.")
    public ResponseEntity<List<EvaluationResDTO>> getEvaluationsByEmployeeId() {

        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();
            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authenticated user found.");
        }

        List<Evaluation> evaluations = evaluationService.getEvaluationsByEmployeeId(employeeId);
        List<EvaluationResDTO> evaluationDTOs = evaluations.stream().map(Evaluation::toResponseDTO).collect(Collectors.toList());

        return new ResponseEntity<>(evaluationDTOs, HttpStatus.OK);
    }


    // 평가 조회 - 테스트
    @GetMapping("/evaluation/{evaluation-id}")
    @Operation(summary = "평가 상세 조회", description = "평가 ID로 해당 평가 정보를 조회한다.")
    public ResponseEntity<EvaluationResDTO> getEvaluationById(
            @Parameter(description = "평가 ID", example = "1") @PathVariable("evaluation-id") Long evaluationId) {
        Evaluation evaluation = evaluationService.getEvaluationById(evaluationId);

        if (evaluation != null) {
            return new ResponseEntity<>(evaluation.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 등록 - 테스트
    @PostMapping("/evaluation")
    @Operation(summary = "평가 등록", description = "평가 정보를 받아서 등록한다.")
    public ResponseEntity<EvaluationResDTO> create(@RequestBody EvaluationReqDTO requestDTO) {
        Evaluation evaluation = evaluationService.createEvaluation(requestDTO);
        return new ResponseEntity<>(evaluation.toResponseDTO(), HttpStatus.CREATED);
    }

    // 평가 수정 - 테스트
    @PutMapping("/evaluation/{evaluation-id}")
    @Operation(summary = "평가 수정", description = "평가 정보를 받아 수정한다.")
    public ResponseEntity<EvaluationResDTO> update(
            @Parameter(description = "평가 ID", example = "1") @PathVariable("evaluation-id") Long evaluationId,
            @RequestBody EvaluationReqDTO requestDTO) {

        Evaluation evaluation = evaluationService.updateEvaluation(evaluationId, requestDTO);

        if (evaluation != null) {
            return new ResponseEntity<>(evaluation.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 삭제 - 테스트
    @DeleteMapping("/evaluation/{evaluation-id}")
    @Operation(summary = "평가 삭제", description = "평가 ID로 해당 평가를 삭제한다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "평가 ID", example = "1") @PathVariable("evaluation-id") Long evaluationId) {

        boolean isDeleted = evaluationService.deleteEvaluation(evaluationId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
