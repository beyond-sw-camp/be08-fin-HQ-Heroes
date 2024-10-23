package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;

import java.util.List;

public interface EvaluationService {

    List<Evaluation> getEvaluations();

    //- 테스트
    List<Evaluation> getEvaluationsByEmployeeId(String employeeId);

    //- 테스트
    Evaluation getEvaluationById(Long evaluationId);

    //- 테스트
    Evaluation createEvaluation(EvaluationReqDTO requestDTO);

    //- 테스트
    Evaluation updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO);

    //- 테스트
    boolean deleteEvaluation(Long evaluationId);

}
