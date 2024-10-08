package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<SalaryDTO> getSalariesByEmployeeId(String employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            List<Salary> salaries = salaryRepository.findByEmployee(employee.get());
            return salaries.stream().map(this::mapToDTO).collect(Collectors.toList());
        } else {
            throw new NoSuchElementException("Employee not found with ID: " + employeeId);
        }
    }

    @Override
    public SalaryDTO createSalary(SalaryDTO salaryDTO) {
        Salary salary = mapToEntity(salaryDTO);
        Salary savedSalary = salaryRepository.save(salary);
        return mapToDTO(savedSalary);
    }

    @Override
    public SalaryDTO updateSalaryByEmployeeAndSalaryId(String employeeId, Long salaryId, SalaryDTO salaryDTO) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            Salary salary = salaryRepository.findByEmployeeAndSalaryId(employee.get(), salaryId)
                    .orElse(null);
            if (salary != null) {
                salary.setBaseSalary(salaryDTO.getBaseSalary());
                salary.setBonus(salaryDTO.getBonus());
                salary.setSalaryMonth(LocalDateTime.now());
                Salary updatedSalary = salaryRepository.save(salary);
                return mapToDTO(updatedSalary);
            }
        } else {
            throw new NoSuchElementException("Employee not found with ID: " + employeeId);
        }
        return null;
    }

    @Override
    public boolean deleteSalaryByEmployeeAndSalaryId(String employeeId, Long salaryId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            Salary salary = salaryRepository.findByEmployeeAndSalaryId(employee.get(), salaryId)
                    .orElse(null);
            if (salary != null) {
                salaryRepository.delete(salary);
                return true;
            }
        } else {
            throw new NoSuchElementException("Employee not found with ID: " + employeeId);
        }
        return false;
    }

    private SalaryDTO mapToDTO(Salary salary) {
        SalaryDTO salaryDTO = new SalaryDTO();
        salaryDTO.setEmployeeId(salary.getEmployee().getEmployeeId());
        salaryDTO.setBaseSalary(salary.getBaseSalary());
        salaryDTO.setBonus(salary.getBonus());
        salaryDTO.setSalaryMonth(salary.getSalaryMonth());
        return salaryDTO;
    }

    private Salary mapToEntity(SalaryDTO salaryDTO) {
        Salary salary = new Salary();
        Optional<Employee> employee = employeeRepository.findById(salaryDTO.getEmployeeId());
        if (employee.isPresent()) {
            salary.setEmployee(employee.get());
        } else {
            throw new NoSuchElementException("Employee not found with ID: " + salaryDTO.getEmployeeId());
        }
        salary.setBaseSalary(salaryDTO.getBaseSalary());
        salary.setBonus(salaryDTO.getBonus());
        salary.setSalaryMonth(LocalDateTime.now());
        return salary;
    }
}
