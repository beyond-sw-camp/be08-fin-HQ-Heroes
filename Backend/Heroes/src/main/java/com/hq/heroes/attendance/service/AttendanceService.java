package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.auth.entity.Employee;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface AttendanceService {

    List<AttendanceDTO> getAllAttendances();

    // 출근
    Attendance checkIn(Employee employee);

    // 퇴근
    void checkOut(Employee employee);

    // 출근한 사람인지 판별
    boolean isAlreadyCheckedIn(String employeeId);

    // 회원 번호로 근태 조회
    List<AttendanceDTO> getAttendancesByEmployeeId(String employeeId);

    // 가장 최근의 근태 조회 - 상태 기준
    AttendanceDTO getLatestAttendance(String employeeId);

    // 기간 조회
    List<AttendanceDTO> findByEmployee_IdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    // 총 근무시간 계산
    int calculateTotalWorkHours(String employeeId, YearMonth targetMonth);

    // 가장 최근의 근태 조회 - 상태와 상관없이 최신
    AttendanceDTO getLatestAttendanceRecord(String employeeId);
}
