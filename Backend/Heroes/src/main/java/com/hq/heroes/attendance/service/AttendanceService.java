package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.auth.entity.Employee;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface AttendanceService {

    List<AttendanceDTO> getAllAttendances();

    // - 테스트
    Attendance checkIn(Employee employee);

    // - 테스트
    void checkOut(Employee employee);

    boolean isAlreadyCheckedIn(String employeeId);

    List<AttendanceDTO> getAttendancesByEmployeeId(String employeeId);

    AttendanceDTO getLatestAttendance(String employeeId);

    //- 테스트
    List<AttendanceDTO> findByEmployee_IdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    // - 테스트
    int calculateTotalWorkHours(String employeeId, YearMonth targetMonth);

    AttendanceDTO getLatestAttendanceRecord(String employeeId);
}
