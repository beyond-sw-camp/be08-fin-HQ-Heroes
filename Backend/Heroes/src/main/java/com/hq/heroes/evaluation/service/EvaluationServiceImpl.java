package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
    public Evaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Evaluation createEvaluation(EvaluationReqDTO requestDTO) {
        // 피평가자 및 평가자 정보를 가져옴
        Optional<Employee> employeeOpt = employeeRepository.findById(requestDTO.getEmployeeId());
        Optional<Employee> evaluatorOpt = employeeRepository.findById(requestDTO.getEvaluatorId());

        // 유효성 검사: 피평가자 또는 평가자가 존재하지 않으면 예외 발생
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee ID: " + requestDTO.getEmployeeId());
        }
        if (evaluatorOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid evaluator ID: " + requestDTO.getEvaluatorId());
        }

        Employee employee = employeeOpt.get();
        Employee evaluator = evaluatorOpt.get();

        // 평가 생성 및 저장
        Evaluation evaluation = Evaluation.builder()
                .employee(employee)
                .evaluator(evaluator)
                .score(requestDTO.getScore())
                .comments(requestDTO.getComments())
                .build();

        // 데이터베이스에 평가 저장
        return evaluationRepository.save(evaluation);
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
