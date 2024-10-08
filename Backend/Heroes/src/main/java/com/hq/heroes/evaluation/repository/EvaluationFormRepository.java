package com.hq.heroes.evaluation.repository;

import com.hq.heroes.evaluation.entity.EvaluationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationFormRepository extends JpaRepository<EvaluationForm, Long> {
}
