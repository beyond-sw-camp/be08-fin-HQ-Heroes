package com.hq.heroes.attendance.repository;

import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import com.hq.heroes.auth.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndStatus(Employee employee, AttendanceStatus attendanceStatus);

    // 직원의 가장 최근 출근 기록 조회
    Optional<Attendance> findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(Employee employee, AttendanceStatus status);

    List<Attendance> findByEmployeeEmployeeId(String employeeId);
}
