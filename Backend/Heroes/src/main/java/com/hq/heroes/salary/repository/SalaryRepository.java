package com.hq.heroes.salary.repository;

import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    // 직책(Position)으로 Salary를 조회하는 메서드
    Optional<Salary> findByPosition(Position position);

    // 년도만 비교
    @Query("SELECT s FROM Salary s WHERE s.employee.employeeId = :employeeId AND FUNCTION('YEAR', s.performanceDate) = :year")
    List<Salary> findByEmployeeIdAndYear(@Param("employeeId") String employeeId, @Param("year") int year);

    // 년도 및 월 비교
    @Query("SELECT s FROM Salary s WHERE s.employee.employeeId = :employeeId AND FUNCTION('YEAR', s.performanceDate) = :year AND FUNCTION('MONTH', s.performanceDate) = :month")
    List<Salary> findByEmployeeIdAndYearAndMonth(@Param("employeeId") String employeeId, @Param("year") int year, @Param("month") int month);
}
