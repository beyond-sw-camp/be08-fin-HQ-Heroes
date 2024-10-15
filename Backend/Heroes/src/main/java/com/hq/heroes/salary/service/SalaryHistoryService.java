package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;

import java.util.List;

public interface SalaryHistoryService {
    List<SalaryHistoryDTO> getSalariesUpToCurrentMonth(String employeeId, int year);
    SalaryHistoryDTO getSalaryByEmployeeIdAndMonth(String employeeId, int year, int month);
    SalaryHistoryDTO createSalary(SalaryHistoryDTO SalaryHistoryDTO);
}
