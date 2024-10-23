package com.hq.heroes.evaluation.controller;

import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaResDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.evaluation.service.EvaluationCriteriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/evaluation-criteria")
@Tag(name = "Evaluation Criteria APIs", description = "평가 기준 관련 API 목록")
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService evaluationCriteriaService;

    // 평가 기준 목록 조회
    @GetMapping
    @Operation(summary = "평가 기준 목록 조회", description = "전체 평가 기준 목록을 조회한다.")
    public ResponseEntity<List<EvaluationCriteriaResDTO>> getEvaluationCriteriaList() {
        List<EvaluationCriteria> evaluationCriteriaList = evaluationCriteriaService.getEvaluationCriteriaList();
        List<EvaluationCriteriaResDTO> criteriaDTOs = evaluationCriteriaList.stream()
                .map(EvaluationCriteria::toResponseDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(criteriaDTOs, HttpStatus.OK);
    }

    // 부서 이름으로 평가 기준 목록 조회
    @GetMapping("by-department")
    @Operation(summary = "부서별 평가 기준 조회", description = "부서 이름을 기준으로 평가 기준 목록을 조회한다.")
    public ResponseEntity<List<EvaluationCriteriaResDTO>> getEvaluationCriteriaByDepartment(
            @Parameter(description = "부서 이름", example = "경영지원본부") @RequestParam("deptName") String deptName) {

        List<EvaluationCriteria> evaluationCriteriaList = evaluationCriteriaService.getEvaluationCriterListByDeptName(deptName);
        List<EvaluationCriteriaResDTO> criteriaDTOs = evaluationCriteriaList.stream()
                .map(EvaluationCriteria::toResponseDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(criteriaDTOs, HttpStatus.OK);
    }

    // 평가 기준 조회 - 테스트
    @GetMapping("/{criteria-id}")
    @Operation(summary = "평가 기준 상세 조회", description = "평가 기준 ID로 해당 평가 기준 정보를 조회한다.")
    public ResponseEntity<EvaluationCriteriaResDTO> getEvaluationCriteriaById(
            @Parameter(description = "평가 기준 ID", example = "1") @PathVariable("criteria-id") Long criteriaId) {
        EvaluationCriteria criteria = evaluationCriteriaService.getEvaluationCriteriaById(criteriaId);

        if (criteria != null) {
            return new ResponseEntity<>(criteria.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 기준 등록 - 테스트
    @PostMapping
    @Operation(summary = "평가 기준 등록", description = "평가 기준 정보를 받아서 등록한다.")
    public ResponseEntity<EvaluationCriteriaResDTO> createEvaluationCriteria(@RequestBody EvaluationCriteriaReqDTO requestDTO) {
        EvaluationCriteria criteria = evaluationCriteriaService.createEvaluationCriteria(requestDTO);
        return new ResponseEntity<>(criteria.toResponseDTO(), HttpStatus.CREATED);
    }

    // 평가 기준 수정 - 테스트
    @PutMapping("/{criteria-id}")
    @Operation(summary = "평가 기준 수정", description = "평가 기준 정보를 받아서 수정한다.")
    public ResponseEntity<EvaluationCriteriaResDTO> updateEvaluationCriteria(
            @Parameter(description = "평가 기준 ID", example = "1") @PathVariable("criteria-id") Long criteriaId,
            @RequestBody EvaluationCriteriaReqDTO requestDTO) {

        EvaluationCriteria criteria = evaluationCriteriaService.updateEvaluationCriteria(criteriaId, requestDTO);

        if (criteria != null) {
            return new ResponseEntity<>(criteria.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 기준 삭제 - 테스트
    @DeleteMapping("/{criteria-id}")
    @Operation(summary = "평가 기준 삭제", description = "평가 기준 ID로 해당 평가 기준을 삭제한다.")
    public ResponseEntity<Void> deleteEvaluationCriteria(
            @Parameter(description = "평가 기준 ID", example = "1") @PathVariable("criteria-id") Long criteriaId) {

        boolean isDeleted = evaluationCriteriaService.deleteEvaluationCriteria(criteriaId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
