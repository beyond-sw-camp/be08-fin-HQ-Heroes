package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationResDTO;
import com.hq.heroes.evaluation.entity.Evaluation;

import java.util.List;

public interface EvaluationService {

    List<EvaluationResDTO> getEvaluations();

    List<EvaluationResDTO> getEvaluationsByEmployeeId(String employeeId);

    List<EvaluationResDTO> getEvaluationsByEvaluatorId(String employeeId);

    EvaluationResDTO getEvaluationById(Long evaluationId);

    EvaluationResDTO createEvaluation(EvaluationReqDTO requestDTO);

    EvaluationResDTO updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO);

    boolean deleteEvaluation(Long evaluationId);

    List<EvaluationResDTO> findEvaluationsByEmployeeEvaluatorAndPeriod(String employeeId, String evaluatorId, String period);
}
