package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    // 시급, 성과급 비율 생성
    SalaryDTO createSalary(SalaryDTO salaryDTO);

    // 직책에 따른 시급 조회
    Optional<Double> getBaseSalaryByPositionId(String employeeId);

    // 사원번호를 통한 모든 시급, 성과급 정보 조회
    List<SalaryDTO> getAllSalariesByEmployeeId(String employeeId);
}
