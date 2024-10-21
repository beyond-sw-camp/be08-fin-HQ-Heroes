package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;

import java.util.List;

public interface SalaryHistoryService {
    List<SalaryHistoryDTO> getAllSalaries(String employeeId); // 전체 급여 기록 조회 메서드

    SalaryHistoryDTO createSalary(SalaryHistoryDTO SalaryHistoryDTO);

    Double getLastThreeMonthsSalarySum(String employeeId);
}
