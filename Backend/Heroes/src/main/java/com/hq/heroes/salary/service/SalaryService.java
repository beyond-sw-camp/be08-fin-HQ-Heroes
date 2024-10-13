package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;

import java.util.List;

public interface SalaryService {
    List<SalaryDTO> getSalariesUpToCurrentMonth(String employeeId, int year);
    SalaryDTO getSalaryByEmployeeIdAndMonth(String employeeId, int year, int month);
    SalaryDTO createSalary(SalaryDTO salaryDto);
    SalaryDTO updateSalary(Long salaryId, SalaryDTO salaryDto);
    void deleteSalary(Long salaryId);
}
