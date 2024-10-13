package com.hq.heroes.evaluation.service;

import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import com.hq.heroes.evaluation.repository.EvaluationCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationCriteriaServiceImpl implements EvaluationCriteriaService {

    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<EvaluationCriteria> getEvaluationCriteriaList() {
        return evaluationCriteriaRepository.findAll();
    }

    @Override
    public List<EvaluationCriteria> getEvaluationCriterListByDeptName(String deptName) {
        Department department = departmentRepository.findByDeptName(deptName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department name: " + deptName));

        return evaluationCriteriaRepository.findByDepartment(department);
    }


    @Override
    public EvaluationCriteria getEvaluationCriteriaById(Long criteriaId) {
        return evaluationCriteriaRepository.findById(criteriaId)
                .orElse(null);
    }

    @Override
    @Transactional
    public EvaluationCriteria createEvaluationCriteria(EvaluationCriteriaReqDTO requestDTO) {
        Department department = departmentRepository.findById(requestDTO.getDeptId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

        EvaluationCriteria evaluationCriteria = EvaluationCriteria.fromRequestDTO(requestDTO, department);

        return evaluationCriteriaRepository.save(evaluationCriteria);
    }

    @Override
    @Transactional
    public EvaluationCriteria updateEvaluationCriteria(Long criteriaId, EvaluationCriteriaReqDTO requestDTO) {
        EvaluationCriteria criteria = evaluationCriteriaRepository.findById(criteriaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 평가 기준 ID : " + criteriaId));

        Department department = departmentRepository.findById(requestDTO.getDeptId())
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
