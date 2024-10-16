package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.auth.entity.Employee;

import java.util.List;

public interface AttendanceService {

    List<AttendanceDTO> getAllAttendances();

    Attendance checkIn(Employee employee);

    void checkOut(Employee employee);

    boolean isAlreadyCheckedIn(String employeeId);

    List<AttendanceDTO> getAttendancesByEmployeeId(String employeeId);

    AttendanceDTO getLatestAttendance(String employeeId);
}
