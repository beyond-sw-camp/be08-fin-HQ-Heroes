package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import com.hq.heroes.salary.service.SalaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Evaluation> getEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public List<Evaluation> getEvaluationsByEmployeeId(String employeeId) {
        return evaluationRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public List<Evaluation> getEvaluationsByEvaluatorId(String evaluatorId) {
        log.info("Fetching evaluations by evaluator ID: {}", evaluatorId);  // Log service layer access
        return evaluationRepository.findByEvaluator_EmployeeId(evaluatorId);
    }


    @Override
    public Evaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Evaluation createEvaluation(EvaluationReqDTO requestDTO) {
        // Employee
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Employee evaluator = employeeRepository.findById(requestDTO.getEvaluatorId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Evaluation 엔티티 생성
        Evaluation evaluation = Evaluation.fromRequestDTO(requestDTO, employee, evaluator); // evaluator는 본인으로 설정

        // Evaluation 저장
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        return savedEvaluation; // 저장된 평가 리턴
    }

    @Override
    @Transactional
    public Evaluation updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO) {
        // 기존 평가 조회
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 평가 ID : " + evaluationId));

        // 평가 정보 업데이트
        evaluation.setScore(requestDTO.getScore());
        evaluation.setComments(requestDTO.getComments());

        return evaluationRepository.save(evaluation);
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

    public List<Evaluation> findEvaluationsByEmployeeEvaluatorAndPeriod(String employeeId, String evaluatorId, String period) {
        log.info("조회 요청 - 사원 ID: {}, 평가자 ID: {}, 기간: {}", employeeId, evaluatorId, period);
        return evaluationRepository.findEvaluationsByEmployeeEvaluatorAndPeriod(employeeId, evaluatorId, period);
    }

}
