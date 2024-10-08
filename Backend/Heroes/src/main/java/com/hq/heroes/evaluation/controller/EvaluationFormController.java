package com.hq.heroes.evaluation.controller;

import com.hq.heroes.evaluation.dto.EvaluationFormReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationFormResDTO;
import com.hq.heroes.evaluation.entity.EvaluationForm;
import com.hq.heroes.evaluation.service.EvaluationFormService;
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
@RequestMapping("/api/v1/evaluation-form")
@Tag(name = "Evaluation Form APIs", description = "평가 양식 관련 API 목록")
public class EvaluationFormController {

    private final EvaluationFormService evaluationFormService;

    // 평가 양식 목록 조회
    @GetMapping
    @Operation(summary = "평가 양식 목록 조회", description = "전체 평가 양식 목록을 조회한다.")
    public ResponseEntity<List<EvaluationFormResDTO>> getEvaluationForms() {
        List<EvaluationForm> evaluationForms = evaluationFormService.getEvaluationForms();
        List<EvaluationFormResDTO> evaluationFormDTOs = evaluationForms.stream()
                .map(EvaluationForm::toResponseDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(evaluationFormDTOs, HttpStatus.OK);
    }

    // 특정 평가 양식 조회
    @GetMapping("/{evaluation-form-id}")
    @Operation(summary = "평가 양식 상세 조회", description = "평가 양식 ID로 해당 평가 양식을 조회한다.")
    public ResponseEntity<EvaluationFormResDTO> getEvaluationFormById(
            @PathVariable("evaluation-form-id") Long evaluationFormId) {
        EvaluationForm evaluationForm = evaluationFormService.getEvaluationFormById(evaluationFormId);

        if (evaluationForm != null) {
            return new ResponseEntity<>(evaluationForm.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 양식 등록
    @PostMapping
    @Operation(summary = "평가 양식 등록", description = "평가 양식을 등록한다.")
    public ResponseEntity<EvaluationFormResDTO> createEvaluationForm(@RequestBody EvaluationFormReqDTO requestDTO) {
        EvaluationForm evaluationForm = evaluationFormService.createEvaluationForm(requestDTO);
        return new ResponseEntity<>(evaluationForm.toResponseDTO(), HttpStatus.CREATED);
    }

    // 평가 양식 수정
    @PutMapping("/{evaluation-form-id}")
    @Operation(summary = "평가 양식 수정", description = "평가 양식 정보를 받아 수정한다.")
    public ResponseEntity<EvaluationFormResDTO> updateEvaluationForm(
            @PathVariable("evaluation-form-id") Long evaluationFormId,
            @RequestBody EvaluationFormReqDTO requestDTO) {

        EvaluationForm evaluationForm = evaluationFormService.updateEvaluationForm(evaluationFormId, requestDTO);

        if (evaluationForm != null) {
            return new ResponseEntity<>(evaluationForm.toResponseDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 평가 양식 삭제
    @DeleteMapping("/{evaluation-form-id}")
    @Operation(summary = "평가 양식 삭제", description = "평가 양식 ID로 해당 평가 양식을 삭제한다.")
    public ResponseEntity<Void> deleteEvaluationForm(@PathVariable("evaluation-form-id") Long evaluationFormId) {
        boolean isDeleted = evaluationFormService.deleteEvaluationForm(evaluationFormId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
