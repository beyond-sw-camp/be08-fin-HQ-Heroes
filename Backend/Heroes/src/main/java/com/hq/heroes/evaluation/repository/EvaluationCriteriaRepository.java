package com.hq.heroes.evaluation.repository;

import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.evaluation.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    List<EvaluationCriteria> findByDepartment(Department department);
}
