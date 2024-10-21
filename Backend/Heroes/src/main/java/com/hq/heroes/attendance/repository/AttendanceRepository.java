package com.hq.heroes.attendance.repository;

import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import com.hq.heroes.auth.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndStatus(Employee employee, AttendanceStatus attendanceStatus);

    // 직원의 가장 최근 출근 기록 조회
    Optional<Attendance> findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(Employee employee, AttendanceStatus status);

    List<Attendance> findByEmployeeEmployeeId(String employeeId);

    List<Attendance> findByEmployee_EmployeeIdAndCheckInBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    // 특정 사원의 월별 총 근무 시간 조회
    @Query("SELECT SUM(TIMESTAMPDIFF(HOUR, a.checkIn, a.checkOut)) " +
            "FROM Attendance a " +
            "WHERE a.employee.employeeId = :employeeId " +
            "AND MONTH(a.checkIn) = :month " +
            "AND YEAR(a.checkIn) = :year")
    Integer findTotalWorkHours(@Param("employeeId") String employeeId,
                               @Param("year") int year,
                               @Param("month") int month);
    Optional<Attendance> findTopByEmployeeOrderByCheckInDesc(Employee employee);
}
