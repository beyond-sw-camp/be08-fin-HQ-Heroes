package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.auth.repository.EmployeeRepository;
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
    public Evaluation getEvaluationById(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Evaluation createEvaluation(EvaluationReqDTO requestDTO) {

        // 평가자와 피평가자 정보 가져옴
        Optional<Employee> employeeOpt = employeeRepository.findById(String.valueOf(requestDTO.getEmployeeId()));
        Optional<Employee> evaluatorOpt = employeeRepository.findById(String.valueOf(requestDTO.getEvaluatorId()));

        if (employeeOpt.isEmpty() || evaluatorOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee or evaluator ID");
        }

        Employee employee = employeeOpt.get();
        Employee evaluator = evaluatorOpt.get();

        // 평가 생성
        Evaluation evaluation = Evaluation.builder()
                .employee(employee)
                .evaluator(evaluator)
                .score(requestDTO.getScore())
                .comments(requestDTO.getComments())
                .build();

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
