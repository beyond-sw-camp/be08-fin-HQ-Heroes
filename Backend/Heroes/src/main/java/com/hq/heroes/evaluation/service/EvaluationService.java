package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationCriteriaReqDTO;
import com.hq.heroes.evaluation.dto.EvaluationReqDTO;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;

import java.util.List;

public interface EvaluationService {

    List<Evaluation> getEvaluations();

    Evaluation getEvaluationById(Long evaluationId);

    Evaluation createEvaluation(EvaluationReqDTO requestDTO);

    Evaluation updateEvaluation(Long evaluationId, EvaluationReqDTO requestDTO);

    boolean deleteEvaluation(Long evaluationId);

    List<EvaluationCriteria> getEvaluationCriteriaList();

    EvaluationCriteria getEvaluationCriteriaById(Long criteriaId);

    EvaluationCriteria createEvaluationCriteria(EvaluationCriteriaReqDTO requestDTO);

    EvaluationCriteria updateEvaluationCriteria(Long criteriaId, EvaluationCriteriaReqDTO requestDTO);

    boolean deleteEvaluationCriteria(Long criteriaId);
}
