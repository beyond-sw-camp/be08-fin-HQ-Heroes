package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<Double> getBaseSalaryByPositionId(String employeeId) {
        // 1. 직원 조회 (employeeId로 직원의 positionId 찾기)
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 직원이 존재하지 않습니다."));

        // 2. 직책 정보 조회 (positionId로 baseSalary 찾기)
        return Optional.ofNullable(employee.getPosition().getBaseSalary());
    }

    @Override
    public List<SalaryDTO> getAllSalariesByEmployeeId(String employeeId) {
        List<Salary> salaries = salaryRepository.findByEmployeeId(employeeId);

        return salaries.stream()
                .map(s -> SalaryDTO.builder()
                        .salaryId(s.getSalaryId())
                        .positionId(s.getPosition().getPositionId())
                        .employeeId(s.getEmployee().getEmployeeId())
                        .performanceBonus(s.getPerformanceBonus())
                        .performanceDate(s.getPerformanceDate())
                        .baseSalary(s.getPosition().getBaseSalary())
                        .build())
                .toList();
    }
}
