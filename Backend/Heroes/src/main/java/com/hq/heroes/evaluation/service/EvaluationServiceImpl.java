package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.evaluation.repository.EvaluationCriteriaRepository;
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
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final DepartmentRepository departmentRepository;

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

        System.out.println(requestDTO.toString());

        // 평가자와 피평가자 정보 가져옴
        Optional<Employee> employeeOpt = employeeRepository.findById(requestDTO.getEmployeeId());
        Optional<Employee> evaluatorOpt = employeeRepository.findById(requestDTO.getEvaluatorId());
        Optional<EvaluationCriteria> criteriaOpt = evaluationCriteriaRepository.findById(requestDTO.getEvaluationTemplateId());

        if (employeeOpt.isEmpty() || evaluatorOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee or evaluator ID");
        }

        Employee employee = employeeOpt.get();
        Employee evaluator = evaluatorOpt.get();
        EvaluationCriteria criteria = criteriaOpt.get();

        // 평가 생성
        Evaluation evaluation = Evaluation.builder()
                .employee(employee)
                .evaluator(evaluator)
                .evaluationTemplate(criteria)
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

    @Override
    public List<EvaluationCriteria> getEvaluationCriteriaList() {
        return evaluationCriteriaRepository.findAll();
    }

    @Override
    public EvaluationCriteria getEvaluationCriteriaById(Long criteriaId) {
        return evaluationCriteriaRepository.findById(criteriaId)
                .orElse(null);
    }

    @Override
    @Transactional
    public EvaluationCriteria createEvaluationCriteria(EvaluationCriteriaReqDTO requestDTO) {
        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

        EvaluationCriteria evaluationCriteria = EvaluationCriteria.fromRequestDTO(requestDTO, department);

        return evaluationCriteriaRepository.save(evaluationCriteria);
    }

    @Override
    @Transactional
    public EvaluationCriteria updateEvaluationCriteria(Long criteriaId, EvaluationCriteriaReqDTO requestDTO) {
        EvaluationCriteria criteria = evaluationCriteriaRepository.findById(criteriaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 평가 기준 ID : " + criteriaId));

        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

        // 평가 기준 정보 업데이트
        criteria.setDepartment(department);
        criteria.setCriteriaTitle(requestDTO.getCriteriaTitle());
        criteria.setCriteriaContent(requestDTO.getCriteriaContent());

        return evaluationCriteriaRepository.save(criteria);
    }

    @Override
    @Transactional
    public boolean deleteEvaluationCriteria(Long criteriaId) {
        if (evaluationCriteriaRepository.existsById(criteriaId)) {
            evaluationCriteriaRepository.deleteById(criteriaId);
            return true;
        }
        return false;
    }
}
