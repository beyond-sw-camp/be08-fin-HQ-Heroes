package com.hq.heroes.overtime.service;

import com.hq.heroes.overtime.dto.OvertimeDTO;
import com.hq.heroes.vacation.dto.VacationDTO;

import java.util.List;

public interface OvertimeService {

    void submitOvertime(OvertimeDTO overtimeDTO);

    List<OvertimeDTO> getAllOvertimes();

    void approveOvertime(Long overtimeId);

    void rejectOvertime(Long overtimeId);

    List<OvertimeDTO> getApprovedOvertimesByEmployeeId(String employeeId);
}
