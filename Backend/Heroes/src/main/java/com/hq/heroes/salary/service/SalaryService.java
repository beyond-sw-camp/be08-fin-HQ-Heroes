package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    Optional<Double> getBaseSalaryByPositionId(String employeeId);

    List<SalaryDTO> getAllSalariesByEmployeeId(String employeeId);
}
