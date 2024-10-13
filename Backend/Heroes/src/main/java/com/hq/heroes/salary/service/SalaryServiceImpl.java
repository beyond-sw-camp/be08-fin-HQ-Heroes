package com.hq.heroes.salary.service;

import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;

    @Override
    public List<SalaryDTO> getSalariesUpToCurrentMonth(String employeeId, int year) {
        List<Salary> salaries = salaryRepository.findByEmployee_employeeIdAndYear(employeeId, year);
        return salaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public SalaryDTO getSalaryByEmployeeIdAndMonth(String employeeId, int year, int month) {
        List<Salary> salaries = salaryRepository.findByEmployee_employeeIdAndYearAndMonth(employeeId, year, month);
        return salaries.isEmpty() ? null : convertToDTO(salaries.get(0));
    }

    @Override
    public SalaryDTO createSalary(SalaryDTO salaryDto) {
        Salary salary = Salary.builder()
                .salaryMonth(salaryDto.getSalaryMonth())
                .baseSalary(salaryDto.getBaseSalary())
                .bonus(salaryDto.getBonus())
                .totalSalary(salaryDto.getTotalSalary())
                .status(salaryDto.getStatus())
                .build();
        Salary savedSalary = salaryRepository.save(salary);
        return convertToDTO(savedSalary);
    }

    @Override
    public SalaryDTO updateSalary(Long salaryId, SalaryDTO salaryDto) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("급여를 찾을 수 없습니다."));

        salary.setBaseSalary(salaryDto.getBaseSalary());
        salary.setBonus(salaryDto.getBonus());
        salary.setTotalSalary(salaryDto.getTotalSalary());
        salary.setStatus(salaryDto.getStatus());

        Salary updatedSalary = salaryRepository.save(salary);
        return convertToDTO(updatedSalary);
    }

    @Override
    public void deleteSalary(Long salaryId) {
        salaryRepository.deleteById(salaryId);
    }

    // Salary 엔티티를 SalaryDTO로 변환하는 메서드
    private SalaryDTO convertToDTO(Salary salary) {
        return SalaryDTO.builder()
                .salaryId(salary.getSalaryId())
                .salaryMonth(salary.getSalaryMonth())
                .baseSalary(salary.getBaseSalary())
                .bonus(salary.getBonus())
                .totalSalary(salary.getTotalSalary())
                .status(salary.getStatus())
                .build();
    }
}
