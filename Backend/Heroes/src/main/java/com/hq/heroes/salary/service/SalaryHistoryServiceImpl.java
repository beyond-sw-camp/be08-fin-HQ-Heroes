package com.hq.heroes.salary.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.repository.AttendanceRepository;
import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.salary.dto.RetireDTO;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.Deduct;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.entity.enums.Status;
import com.hq.heroes.salary.repository.DeductRepository;
import com.hq.heroes.salary.repository.SalaryHistoryRepository;
import com.hq.heroes.salary.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final AttendanceService attendanceService;

    @Override
    public List<SalaryHistoryDTO> getAllSalaries(String employeeId) {
        List<SalaryHistory> histories = salaryHistoryRepository.findByEmployee_EmployeeId(employeeId);

        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    // 마지막 출퇴근 날짜로부터 3개월 동안의 급여 합계 조회
    public Double getLastThreeMonthsSalarySum(String employeeId) {
        LocalDateTime lastCheckInDate = getLastCheckInDate(employeeId);
        LocalDateTime threeMonthsAgo = lastCheckInDate.minusMonths(3);

        List<SalaryHistory> salaryHistories = salaryHistoryRepository.findByEmployee_EmployeeIdAndSalaryMonthBetween(
                employeeId, threeMonthsAgo, lastCheckInDate);

        // SalaryHistory를 SalaryHistoryDTO로 변환
        List<RetireDTO> salaryHistoryDTOs = convertToRetireDTO(salaryHistories);

        return salaryHistoryDTOs.stream()
                .mapToDouble(RetireDTO::getPostTaxTotal) // 세후 총액을 기준으로 합산
                .sum();
    }

    private List<RetireDTO> convertToRetireDTO(List<SalaryHistory> salaryHistories) {
        return salaryHistories.stream()
                .map(salaryHistory -> {
                    AttendanceDTO latestAttendance = attendanceService.getLatestAttendanceRecord(salaryHistory.getEmployee().getEmployeeId());
                    return new RetireDTO(
                            salaryHistory.getEmployee().getEmployeeId(),
                            latestAttendance.getCheckIn(),
                            salaryHistory.getPostTaxTotal()
                    );
                })
                .collect(Collectors.toList());
    }
    private LocalDateTime getLastCheckInDate(String employeeId) {
        // 최신 출석 기록을 가져오기
        AttendanceDTO response = attendanceService.getLatestAttendanceRecord(employeeId);

        if (response != null) {
            return response.getCheckIn(); // checkInDate 반환
        } else {
            return LocalDateTime.now(); // 데이터가 없을 경우 현재 날짜 반환
        }
    }

}
