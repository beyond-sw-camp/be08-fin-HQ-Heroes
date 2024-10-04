package com.hq.heroes.salary.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployee(Employee employee);

    Optional<Salary> findByEmployeeAndSalaryId(Employee employee, Long salaryId);
}
