package com.hq.heroes.evaluation.controller;

import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import com.hq.heroes.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/evaluation-service")
@Tag(name = "Evaluation APIs", description = "평가 관련 API 목록")
public class EvaluationController {

    private final EvaluationService evaluationService;

    // 전체 평가 목록 조회 API
    @GetMapping("/evaluations")
    @Operation(summary = "전체 평가 목록 조회", description = "모든 평가 정보를 가져옵니다.")
    public ResponseEntity<List<EvaluationResDTO>> getAllEvaluations() {
        List<EvaluationResDTO> evaluations = evaluationService.getEvaluations();
        return ResponseEntity.ok(evaluations);
    }

    // 특정 평가 조회 API
    @GetMapping("/evaluations/{evaluationId}")
    @Operation(summary = "특정 평가 조회", description = "특정 평가 ID로 평가 정보를 가져옵니다.")
    public ResponseEntity<EvaluationResDTO> getEvaluationById(@PathVariable Long evaluationId) {
        EvaluationResDTO evaluation = evaluationService.getEvaluationById(evaluationId);
        if (evaluation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evaluation);
    }

    // 평가 생성 API
    @PostMapping("/evaluations")
    @Operation(summary = "평가 생성", description = "새로운 평가를 생성합니다.")
    public ResponseEntity<EvaluationResDTO> createEvaluation(@RequestBody EvaluationReqDTO requestDTO) {
        EvaluationResDTO createdEvaluation = evaluationService.createEvaluation(requestDTO);
        return ResponseEntity.ok(createdEvaluation);
    }

    // 평가 수정 API
    @PutMapping("/evaluations/{evaluationId}")
    @Operation(summary = "평가 수정", description = "기존 평가를 수정합니다.")
    public ResponseEntity<EvaluationResDTO> updateEvaluation(
            @PathVariable Long evaluationId, @RequestBody EvaluationReqDTO requestDTO) {
        EvaluationResDTO updatedEvaluation = evaluationService.updateEvaluation(evaluationId, requestDTO);
        return ResponseEntity.ok(updatedEvaluation);
    }

    // 평가 삭제 API
    @DeleteMapping("/evaluations/{evaluationId}")
    @Operation(summary = "평가 삭제", description = "평가 ID로 평가를 삭제합니다.")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long evaluationId) {
        boolean deleted = evaluationService.deleteEvaluation(evaluationId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
