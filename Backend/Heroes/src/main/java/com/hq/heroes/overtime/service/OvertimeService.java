package com.hq.heroes.overtime.service;

import com.hq.heroes.overtime.dto.OvertimeDTO;

import java.time.YearMonth;
import java.util.List;

public interface OvertimeService {

    void submitOvertime(OvertimeDTO overtimeDTO);

    List<OvertimeDTO> getAllOvertimes();

    void approveOvertime(Long overtimeId);

    void rejectOvertime(Long overtimeId);

    List<OvertimeDTO> getApprovedOvertimesByEmployeeId(String employeeId);

    long getTotalOvertimeHoursForMonth(String employeeId, YearMonth month);
}
