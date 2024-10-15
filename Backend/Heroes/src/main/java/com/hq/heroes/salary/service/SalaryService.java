package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;

import java.util.List;

public interface SalaryService {
    SalaryDTO getSalaryByPositionId(Long positionId); // 직책으로 급여 조회

    List<SalaryDTO> getSalariesByEmployeeIdAndYear(String employeeId, int year);
    List<SalaryDTO> getSalariesByEmployeeIdAndYearAndMonth(String employeeId, int year, int month);
}
