package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;

import java.util.List;

public interface EvaluationCriteriaService {

    List<EvaluationCriteria> getEvaluationCriteriaList();

    List<EvaluationCriteria> getEvaluationCriterListByDeptName(String deptName);

    EvaluationCriteria getEvaluationCriteriaById(Long criteriaId);

    EvaluationCriteria createEvaluationCriteria(EvaluationCriteriaReqDTO requestDTO);

    EvaluationCriteria updateEvaluationCriteria(Long criteriaId, EvaluationCriteriaReqDTO requestDTO);

    boolean deleteEvaluationCriteria(Long criteriaId);

}
