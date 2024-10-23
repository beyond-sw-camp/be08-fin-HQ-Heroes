package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;

import java.util.List;

public interface EvaluationCriteriaService {

    List<EvaluationCriteria> getEvaluationCriteriaList();

    List<EvaluationCriteria> getEvaluationCriterListByDeptName(String deptName);

    //- 테스트
    EvaluationCriteria getEvaluationCriteriaById(Long criteriaId);

    //- 테스트
    EvaluationCriteria createEvaluationCriteria(EvaluationCriteriaReqDTO requestDTO);

    //- 테스트
    EvaluationCriteria updateEvaluationCriteria(Long criteriaId, EvaluationCriteriaReqDTO requestDTO);

    //- 테스트
    boolean deleteEvaluationCriteria(Long criteriaId);

}
