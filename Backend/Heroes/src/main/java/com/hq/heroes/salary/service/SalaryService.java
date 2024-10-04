package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;

import java.util.List;

public interface SalaryService {
    List<SalaryDTO> getSalariesByEmployeeId(String employeeId);
    SalaryDTO createSalary(SalaryDTO salaryDTO);
    SalaryDTO updateSalaryByEmployeeAndSalaryId(String employeeId, Long salaryId, SalaryDTO salaryDTO);
    boolean deleteSalaryByEmployeeAndSalaryId(String employeeId, Long salaryId);
}
