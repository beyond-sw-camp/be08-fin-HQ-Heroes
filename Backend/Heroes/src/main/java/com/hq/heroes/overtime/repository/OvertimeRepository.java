package com.hq.heroes.overtime.repository;

import com.hq.heroes.overtime.entity.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<Overtime, Long> {

    // 사원 번호로 승인된 연장근로 조회
    @Query("SELECT o FROM Overtime o WHERE o.employee.employeeId = :employeeId AND o.overtimeStatus = 'APPROVED'")
    List<Overtime> findApprovedOvertimesByEmployeeId(@Param("employeeId") String employeeId);

    // 특정일을 기준으로 해당 직원의 연장근로 기록 조회
    List<Overtime> findByEmployee_EmployeeIdAndOvertimeStartDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);
}
