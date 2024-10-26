package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;

import java.util.List;

public interface EvaluationService {

    List<Evaluation> getEvaluations();

    List<Evaluation> getEvaluationsByEmployeeId(String employeeId);

    List<Evaluation> getEvaluationsByEvaluatorId(String employeeId);

    Evaluation getEvaluationById(Long evaluationId);

    Evaluation createEvaluation(EvaluationReqDTO requestDTO);

    Evaluation updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO);

    boolean deleteEvaluation(Long evaluationId);

    List<Evaluation> findEvaluationsByEmployeeEvaluatorAndPeriod(String employeeId, String evaluatorId, String period);
}
