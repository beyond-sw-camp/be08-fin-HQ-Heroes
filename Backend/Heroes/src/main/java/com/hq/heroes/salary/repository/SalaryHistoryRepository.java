package com.hq.heroes.salary.repository;

import com.hq.heroes.salary.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    // 직원 ID와 해당 연도 급여 기록 조회
    @Query("SELECT s FROM SalaryHistory s " +
            "WHERE s.employee.employeeId = :employeeId " +
            "AND YEAR(s.salaryMonth) = :year")
    List<SalaryHistory> findByEmployee_employeeIdAndYear(String employeeId, int year);

    // 직원 ID, 연도, 월 기준 특정 급여 조회
    @Query("SELECT s FROM SalaryHistory s " +
            "WHERE s.employee.employeeId = :employeeId " +
            "AND YEAR(s.salaryMonth) = :year " +
            "AND MONTH(s.salaryMonth) = :month")
    List<SalaryHistory> findByEmployee_employeeIdAndYearAndMonth(String employeeId, int year, int month);
}
