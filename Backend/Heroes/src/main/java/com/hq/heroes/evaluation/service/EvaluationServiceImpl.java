package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;

    // 엔티티를 응답 DTO로 변환하는 메서드
    public EvaluationResDTO convertToDTO(Evaluation evaluation) {
        return EvaluationResDTO.builder()
                .evaluationId(evaluation.getEvaluationId())
                .employeeId(evaluation.getEmployee().getEmployeeId())  // employee 엔티티에서 사원 이름 추출
                .evaluatorId(evaluation.getEvaluator().getEmployeeId())  // evaluator 엔티티에서 평가자 ID 추출
                .evaluatorName(evaluation.getEvaluator().getEmployeeName()) // evaluator 엔티티에서 평가자 이름 추출
                .score(evaluation.getScore())
                .comments(evaluation.getComments())
                .createdAt(evaluation.getCreatedAt().toString())
                .updatedAt(evaluation.getUpdatedAt().toString())
                .build();
    }

    @Override
    public List<EvaluationResDTO> getEvaluations() {
        // 모든 평가 데이터를 조회하여 DTO로 변환 후 반환
        List<Evaluation> evaluations = evaluationRepository.findAll();
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluationResDTO> getEvaluationsByEmployeeId(String employeeId) {
        // 특정 피평가자의 평가 데이터를 조회하여 DTO로 변환 후 반환
        List<Evaluation> evaluations = evaluationRepository.findByEmployee_EmployeeId(employeeId);
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluationResDTO> getEvaluationsByEvaluatorId(String evaluatorId) {
        log.info("Fetching evaluations by evaluator ID: {}", evaluatorId);
        // 특정 평가자의 평가 데이터를 조회하여 DTO로 변환 후 반환
        List<Evaluation> evaluations = evaluationRepository.findByEvaluator_EmployeeId(evaluatorId);
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public EvaluationResDTO getEvaluationById(Long evaluationId) {
        // ID로 특정 평가 데이터를 조회하고, DTO로 변환하여 반환
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found with ID: " + evaluationId));
        return convertToDTO(evaluation);
    }

    @Override
    @Transactional
    public EvaluationResDTO createEvaluation(EvaluationReqDTO requestDTO) {
        // 피평가자와 평가자 정보를 조회
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + requestDTO.getEmployeeId()));
        Employee evaluator = employeeRepository.findById(requestDTO.getEvaluatorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + requestDTO.getEvaluatorId()));

        // Evaluation 엔티티 생성
        Evaluation evaluation = Evaluation.fromRequestDTO(requestDTO, employee, evaluator);

        // 평가 정보를 저장하고, DTO로 변환하여 반환
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return convertToDTO(savedEvaluation);
    }

    @Override
    @Transactional
    public EvaluationResDTO updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO) {
        // 기존 평가 조회
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 평가 ID : " + evaluationId));

        // 평가 정보 업데이트
        evaluation.setScore(requestDTO.getScore());
        evaluation.setComments(requestDTO.getComments());

        // 업데이트된 평가 정보를 저장하고 DTO로 변환하여 반환
        Evaluation updatedEvaluation = evaluationRepository.save(evaluation);
        return convertToDTO(updatedEvaluation);
    }

    @Override
    @Transactional
    public boolean deleteEvaluation(Long evaluationId) {
        // 평가가 존재하는지 확인
        if (evaluationRepository.existsById(evaluationId)) {
            evaluationRepository.deleteById(evaluationId);
            return true;
        }
        return false;
    }

    // EvaluationServiceImpl.java

    public List<EvaluationResDTO> findEvaluationsByEmployeeEvaluatorAndPeriod(String employeeId, String evaluatorId, String period) {
        log.info("조회 요청 - 사원 ID: {}, 평가자 ID: {}, 기간: {}", employeeId, evaluatorId, period);
        // 특정 사원과 평가자 및 기간에 해당하는 평가를 조회하고 DTO로 변환하여 반환
        List<Evaluation> evaluations = evaluationRepository.findEvaluationsByEmployeeEvaluatorAndPeriod(employeeId, evaluatorId, period);
        return evaluations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
