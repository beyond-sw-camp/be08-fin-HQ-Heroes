package com.hq.heroes.salary.repository;

import com.hq.heroes.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    @Query("SELECT s FROM Salary s WHERE s.employee.employeeId = :employeeId AND FUNCTION('YEAR', s.salaryMonth) = :year")
    List<Salary> findByEmployee_employeeIdAndYear(String employeeId, int year);

    @Query("SELECT s FROM Salary s WHERE s.employee.employeeId = :employeeId AND FUNCTION('YEAR', s.salaryMonth) = :year AND FUNCTION('MONTH', s.salaryMonth) = :month")
    List<Salary> findByEmployee_employeeIdAndYearAndMonth(String employeeId, int year, int month);}
