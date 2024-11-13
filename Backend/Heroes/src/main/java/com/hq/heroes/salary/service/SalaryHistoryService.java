package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;

import java.util.List;

public interface SalaryHistoryService {
    // 전체 급여 기록 조회
    List<SalaryHistoryDTO> getAllSalaries(String employeeId);

    // 급여 생성
    SalaryHistoryDTO createSalary(SalaryHistoryDTO SalaryHistoryDTO);

    // 3달간의 급여 합계 조회
    Double getLastThreeMonthsSalarySum(String employeeId);
}
