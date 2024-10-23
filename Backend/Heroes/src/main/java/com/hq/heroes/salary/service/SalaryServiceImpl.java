package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    @Override
    @Transactional
    public SalaryDTO createSalary(SalaryDTO salaryDTO) {
        Employee employee = employeeRepository.findById(salaryDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Position position = positionRepository.findById(employee.getPosition().getPositionId())
                .orElseThrow(() -> new EntityNotFoundException("Position not found"));

        // Salary 엔티티로 변환
        Salary salary = Salary.builder()
                .employee(employee)
                .position(position)
                .performanceBonus(salaryDTO.getPerformanceBonus())
                .performanceDate(salaryDTO.getPerformanceDate())
                .build();

        // Salary 저장
        Salary savedSalary = salaryRepository.save(salary);

        // 저장된 Salary를 SalaryDTO로 변환하여 반환
        return SalaryDTO.builder()
                .salaryId(savedSalary.getSalaryId())
                .employeeId(savedSalary.getEmployee().getEmployeeId())
                .positionId(savedSalary.getPosition().getPositionId())
                .performanceBonus(savedSalary.getPerformanceBonus())
                .performanceDate(savedSalary.getPerformanceDate())
                .baseSalary(savedSalary.getPosition().getBaseSalary())
                .build();
    }

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
