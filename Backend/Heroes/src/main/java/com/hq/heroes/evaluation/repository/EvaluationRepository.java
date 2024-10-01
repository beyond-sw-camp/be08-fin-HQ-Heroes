package com.hq.heroes.evaluation.repository;

import com.hq.heroes.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

}
