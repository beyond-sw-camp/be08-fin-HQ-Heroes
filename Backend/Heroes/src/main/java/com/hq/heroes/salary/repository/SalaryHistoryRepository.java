package com.hq.heroes.salary.repository;

import com.hq.heroes.salary.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {
    // 직원 ID로 급여 기록 조회
    @Query("SELECT sh FROM SalaryHistory sh " +
            "JOIN FETCH sh.employee e " +
            "WHERE e.employeeId = :employeeId")
    List<SalaryHistory> findSalaryHistoriesByEmployeeId(@Param("employeeId") String employeeId);

    // 직원 ID와 급여 월 범위로 급여 기록 조회
    @Query("SELECT sh FROM SalaryHistory sh " +
            "JOIN FETCH sh.employee e " +
            "WHERE e.employeeId = :employeeId AND sh.salaryMonth BETWEEN :startDate AND :endDate")
    List<SalaryHistory> findSalaryHistoriesByEmployeeIdAndSalaryMonthBetween(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

