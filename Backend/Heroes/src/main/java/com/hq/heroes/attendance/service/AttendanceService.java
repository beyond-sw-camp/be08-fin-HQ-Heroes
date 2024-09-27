package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;

import java.util.List;

public interface AttendanceService {

    List<AttendanceDTO> getAllAttendances();

}
