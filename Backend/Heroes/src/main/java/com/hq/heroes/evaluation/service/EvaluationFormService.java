package com.hq.heroes.evaluation.service;

import com.hq.heroes.evaluation.dto.EvaluationFormReqDTO;
import com.hq.heroes.evaluation.entity.EvaluationForm;

import java.util.List;

public interface EvaluationFormService {

    List<EvaluationForm> getEvaluationForms();

    EvaluationForm getEvaluationFormById(Long evaluationFormId);

    EvaluationForm createEvaluationForm(EvaluationFormReqDTO requestDTO);

    EvaluationForm updateEvaluationForm(Long evaluationFormId, EvaluationFormReqDTO requestDTO);

    boolean deleteEvaluationForm(Long evaluationFormId);
}
