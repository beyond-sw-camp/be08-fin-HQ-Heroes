package com.hq.heroes.evaluation.repository;

import com.hq.heroes.evaluation.entity.Evaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT e FROM Evaluation e " +
            "JOIN FETCH e.employee " +
            "JOIN FETCH e.evaluator")
    Page<Evaluation> findAllWithEmployeeAndEvaluator(Pageable pageable);

    List<Evaluation> findByEmployee_EmployeeId(String employeeId);

    List<Evaluation> findByEvaluator_EmployeeId(String evaluatorId);

    // 상반기 또는 하반기 조건으로 특정 사원 및 평가자의 평가를 조회하는 쿼리
    @Query("SELECT e FROM Evaluation e WHERE e.employee.employeeId = :employeeId " +
            "AND e.evaluator.employeeId = :evaluatorId " +
            "AND ((:period = '상반기' AND FUNCTION('MONTH', e.createdAt) BETWEEN 1 AND 6) " +
            "OR (:period = '하반기' AND FUNCTION('MONTH', e.createdAt) BETWEEN 7 AND 12))")
    List<Evaluation> findEvaluationsByEmployeeEvaluatorAndPeriod(@Param("employeeId") String employeeId,
                                                                 @Param("evaluatorId") String evaluatorId,
                                                                 @Param("period") String period);
}
