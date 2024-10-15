package com.hq.heroes.salary.service;

import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final PositionRepository positionRepository;

    @Override
    public SalaryDTO getSalaryByPositionId(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID"));

        Salary salary = salaryRepository.findByPosition(position)
                .orElseThrow(() -> new IllegalArgumentException("No salary record found for this position"));

        return SalaryDTO.builder()
                .salaryId(salary.getSalaryId())
                .positionId(positionId)
                .employeeId(salary.getEmployee().getEmployeeId())
                .performanceBonus(salary.getPerformanceBonus())
                .baseSalary(position.getBaseSalary())
                .build();
    }

    @Override
    public List<SalaryDTO> getSalariesByEmployeeIdAndYear(String employeeId, int year) {
        List<Salary> salaries = salaryRepository.findByEmployeeIdAndYear(employeeId, year);
        return salaries.stream().map(this::convertToDTO).toList(); // 엔티티를 DTO로 변환
    }

    @Override
    public List<SalaryDTO> getSalariesByEmployeeIdAndYearAndMonth(String employeeId, int year, int month) {
        List<Salary> salaries = salaryRepository.findByEmployeeIdAndYearAndMonth(employeeId, year, month);
        return salaries.stream().map(this::convertToDTO).toList(); // 엔티티를 DTO로 변환
    }

    private SalaryDTO convertToDTO(Salary salary) {
        return SalaryDTO.builder()
                .salaryId(salary.getSalaryId())
                .positionId(salary.getPosition().getPositionId())
                .employeeId(salary.getEmployee().getEmployeeId())
                .performanceBonus(salary.getPerformanceBonus())
                .baseSalary(salary.getPosition().getBaseSalary())
                .build();
    }
}
