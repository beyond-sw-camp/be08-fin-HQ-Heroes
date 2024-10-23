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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryService salaryService;

    @Override
    public List<Evaluation> getEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public List<Evaluation> getEvaluationsByEmployeeId(String employeeId) {
        return evaluationRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public Evaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Evaluation createEvaluation(EvaluationReqDTO requestDTO) {
        // Employee와 Position 가져오기
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Evaluation 엔티티 생성
        Evaluation evaluation = Evaluation.fromRequestDTO(requestDTO, employee, employee); // evaluator는 본인으로 설정

        // Evaluation 저장
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        // 성과급 비율 설정
        double performanceBonus = calculateBonusRate(requestDTO.getScore());

        // SalaryDTO 생성
        SalaryDTO salaryDTO = SalaryDTO.builder()
                .employeeId(employee.getEmployeeId())
                .performanceDate(LocalDateTime.now()) // 현재 날짜로 설정
                .performanceBonus(performanceBonus)
                .build();

        // Salary 엔티티 생성
        salaryService.createSalary(salaryDTO);

        return savedEvaluation; // 저장된 평가 리턴
    }

    // 점수에 따른 성과급 비율 계산 메서드
    private double calculateBonusRate(double score) {
        if (score < 80) {
            return 0.035; // 80점 이하 -> 35%
        } else if (score <= 89) {
            return 0.05; // 80~89점 -> 50%
        } else {
            return 0.065; // 90~100점 -> 65%
        }
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
}
