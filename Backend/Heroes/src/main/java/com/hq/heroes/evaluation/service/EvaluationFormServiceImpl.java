package com.hq.heroes.evaluation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.evaluation.dto.EvaluationFormReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.evaluation.entity.EvaluationForm;
import com.hq.heroes.evaluation.repository.EvaluationCriteriaRepository;
import com.hq.heroes.evaluation.repository.EvaluationFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationFormServiceImpl implements EvaluationFormService {

    private final EvaluationFormRepository evaluationFormRepository;
    private final EmployeeRepository employeeRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;

    @Override
    public List<EvaluationForm> getEvaluationForms() {
        return evaluationFormRepository.findAll();
    }

    @Override
    public EvaluationForm getEvaluationFormById(Long evaluationFormId) {
        return evaluationFormRepository.findById(evaluationFormId)
                .orElse(null);
    }

    @Override
    @Transactional
    public EvaluationForm createEvaluationForm(EvaluationFormReqDTO requestDTO) {

        // 피평가자 정보 가져오기
        Optional<Employee> employeeOpt = employeeRepository.findById(requestDTO.getEmployeeId());
        Optional<EvaluationCriteria> criteriaOpt = evaluationCriteriaRepository.findById(requestDTO.getEvaluationCriteriaId());

        if (employeeOpt.isEmpty() || criteriaOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee or evaluation criteria ID");
        }

        Employee employee = employeeOpt.get();
        EvaluationCriteria evaluationCriteria = criteriaOpt.get();

        // 평가 양식 생성
        EvaluationForm evaluationForm = EvaluationForm.fromRequestDTO(requestDTO, employee, evaluationCriteria);

        return evaluationFormRepository.save(evaluationForm);
    }

    @Override
    @Transactional
    public EvaluationForm updateEvaluationForm(Long evaluationFormId, EvaluationFormReqDTO requestDTO) {
        EvaluationForm evaluationForm = evaluationFormRepository.findById(evaluationFormId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 평가 양식 ID : " + evaluationFormId));

        Optional<Employee> employeeOpt = employeeRepository.findById(requestDTO.getEmployeeId());
        Optional<EvaluationCriteria> criteriaOpt = evaluationCriteriaRepository.findById(requestDTO.getEvaluationCriteriaId());

        if (employeeOpt.isEmpty() || criteriaOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee or evaluation criteria ID");
        }

        Employee employee = employeeOpt.get();
        EvaluationCriteria evaluationCriteria = criteriaOpt.get();

        // 평가 양식 정보 업데이트
        evaluationForm.setEmployee(employee);
        evaluationForm.setEvaluationCriteria(evaluationCriteria);
        evaluationForm.setTeamGoal(requestDTO.getTeamGoal());
        evaluationForm.setPersonalGoal(requestDTO.getPersonalGoal());

        return evaluationFormRepository.save(evaluationForm);
    }

    @Override
    @Transactional
    public boolean deleteEvaluationForm(Long evaluationFormId) {
        if (evaluationFormRepository.existsById(evaluationFormId)) {
            evaluationFormRepository.deleteById(evaluationFormId);
            return true;
        }
        return false;
    }
}
