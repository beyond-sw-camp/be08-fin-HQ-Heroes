package com.hq.heroes.salary.repository;

import com.hq.heroes.salary.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {
    List<SalaryHistory> findByEmployee_EmployeeId(String employeeId); // 직원 ID로 급여 기록 조회

    List<SalaryHistory> findByEmployee_EmployeeIdAndSalaryMonthBetween(String employeeId, LocalDateTime startDate, LocalDateTime endDate);
}
