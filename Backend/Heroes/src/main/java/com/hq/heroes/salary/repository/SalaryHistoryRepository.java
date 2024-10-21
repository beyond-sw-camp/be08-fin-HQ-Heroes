package com.hq.heroes.salary.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {
    List<SalaryHistory> findByEmployee_EmployeeId(String employeeId); // 직원 ID로 급여 기록 조회

    @Query("SELECT sh FROM SalaryHistory sh " +
            "WHERE sh.employee.employeeId = :employeeId " +
            "AND sh.salaryMonth BETWEEN :startDate AND :endDate")
    List<SalaryHistory> findLastThreeMonthsSalaries(
            String employeeId,LocalDateTime startDate, LocalDateTime endDate);
}
