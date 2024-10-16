package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.Deduct;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.repository.DeductRepository;
import com.hq.heroes.salary.repository.SalaryHistoryRepository;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryHistoryServiceImpl implements SalaryHistoryService {

    private final SalaryHistoryRepository salaryHistoryRepository;
    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final DeductRepository deductRepository;

    @Override
    public List<SalaryHistoryDTO> getSalariesUpToCurrentMonth(String employeeId, int year) {
        List<SalaryHistory> salaries = salaryHistoryRepository.findByEmployee_employeeIdAndYear(employeeId, year);
        int currentMonth = LocalDate.now().getMonthValue();
        List<SalaryHistory> filteredSalaries = salaries.stream()
                .filter(s -> s.getSalaryMonth().getMonthValue() <= currentMonth)
                .collect(Collectors.toList());

        return filteredSalaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public SalaryHistoryDTO getSalaryByEmployeeIdAndMonth(String employeeId, int year, int month) {
        List<SalaryHistory> salaries = salaryHistoryRepository.findByEmployee_employeeIdAndYearAndMonth(employeeId, year, month);
        return salaries.isEmpty() ? null : convertToDTO(salaries.get(0));
    }

    @Override
    public SalaryHistoryDTO createSalary(SalaryHistoryDTO dto) {
        Optional<Employee> employee = employeeRepository.findById(dto.getEmployeeId());
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        Employee employeeEntity = employee.get();
        Double baseSalary = employeeEntity.getPosition().getBaseSalary();
        Position position = employeeEntity.getPosition();
        Salary salary = salaryRepository.findByPosition(position)
                .orElseThrow(() -> new IllegalArgumentException("No salary record found for this position"));

        Double performanceBonus = salary.calculatePerformanceBonus();
        Double preTaxTotal = baseSalary + performanceBonus;

        // 각 공제 항목 계산
        List<Deduct> deducts = deductRepository.findAll(); // 모든 공제 항목 가져오기

        Double nationalPension = calculateDeduction(preTaxTotal, deducts, "국민연금");
        Double healthInsurance = calculateDeduction(preTaxTotal, deducts, "건강보험");
        Double longTermCare = healthInsurance * calculateDeductionRate(deducts, "장기요양");
        Double employmentInsurance = calculateDeduction(preTaxTotal, deducts, "고용보험");
        Double incomeTax = calculateDeduction(preTaxTotal, deducts, "소득세");
        Double localIncomeTax = incomeTax * calculateDeductionRate(deducts, "지방소득세");

        Double postTaxTotal = preTaxTotal - (nationalPension + healthInsurance + longTermCare + employmentInsurance + incomeTax + localIncomeTax);

        SalaryHistory salaryHistory = SalaryHistory.builder()
                .employee(employeeEntity)
                .salaryMonth(dto.getSalaryMonth())
                .preTaxTotal(preTaxTotal)
                .nationalPension(nationalPension)
                .healthInsurance(healthInsurance)
                .longTermCare(longTermCare)
                .employmentInsurance(employmentInsurance)
                .incomeTax(incomeTax)
                .localIncomeTax(localIncomeTax)
                .postTaxTotal(postTaxTotal)
                .status(dto.getStatus())
                .paymentDate(LocalDate.now().atStartOfDay())
                .build();

        SalaryHistory savedSalaryHistory = salaryHistoryRepository.save(salaryHistory);
        return convertToDTO(savedSalaryHistory);
    }

    // 세금 및 공제 항목을 계산하는 메서드
    private Double calculateDeduction(Double preTaxTotal, List<Deduct> deducts, String deductionName) {
        return deducts.stream()
                .filter(deduct -> deduct.getDeductName().equals(deductionName))
                .map(deduct -> preTaxTotal * deduct.getDeduction_rate())
                .findFirst()
                .orElse(0.0);
    }

    // 공제 항목의 비율을 가져오는 메서드
    private Double calculateDeductionRate(List<Deduct> deducts, String deductionName) {
        return deducts.stream()
                .filter(deduct -> deduct.getDeductName().equals(deductionName))
                .map(Deduct::getDeduction_rate)
                .findFirst()
                .orElse(0.0);
    }

    // SalaryHistory 엔티티 -> SalaryHistoryDTO 변환 메서드
    private SalaryHistoryDTO convertToDTO(SalaryHistory salaryHistory) {
        return SalaryHistoryDTO.builder()
                .salaryId(salaryHistory.getSalaryHistoryId())
                .employeeId(salaryHistory.getEmployee().getEmployeeId())
                .salaryMonth(salaryHistory.getSalaryMonth())
                .preTaxTotal(salaryHistory.getPreTaxTotal())
                .postTaxTotal(salaryHistory.getPostTaxTotal())
                .status(salaryHistory.getStatus())
                .paymentDate(salaryHistory.getPaymentDate())
                .nationalPension(salaryHistory.getNationalPension())
                .healthInsurance(salaryHistory.getHealthInsurance())
                .longTermCare(salaryHistory.getLongTermCare())
                .employmentInsurance(salaryHistory.getEmploymentInsurance())
                .incomeTax(salaryHistory.getIncomeTax())
                .localIncomeTax(salaryHistory.getLocalIncomeTax())
                .build();
    }

}
