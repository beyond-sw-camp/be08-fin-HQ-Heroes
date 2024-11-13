package com.hq.heroes.salary.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
import com.hq.heroes.overtime.service.OvertimeService;
import com.hq.heroes.salary.dto.RetireDTO;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.Deduct;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.repository.DeductRepository;
import com.hq.heroes.salary.repository.SalaryHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryHistoryServiceImpl implements SalaryHistoryService {
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final DeductRepository deductRepository;
    private final AttendanceService attendanceService;
    private final EvaluationRepository evaluationRepository;
    private final OvertimeService overtimeService;

    @Override
    public List<SalaryHistoryDTO> getAllSalaries(String employeeId) {
        List<SalaryHistory> histories = salaryHistoryRepository.findSalaryHistoriesByEmployeeId(employeeId);

        if (histories.isEmpty()) {
            throw new EntityNotFoundException("급여 이력이 존재하지 않습니다.");
        }

        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사원입니다.");
        }

        long years = ChronoUnit.YEARS.between(employee.get().getJoinDate(), LocalDate.now());

        double salary = calculateSalary(employee.get().getPosition().getBaseSalary(), years);

        return histories.stream()
                .map(salaryHistory -> {
                    return SalaryHistoryDTO.builder()
                            .salaryId(salaryHistory.getSalaryHistoryId())
                            .employeeId(employee.get().getEmployeeId())
                            .salaryMonth(salaryHistory.getSalaryMonth())
                            .preTaxTotal(salaryHistory.getPreTaxTotal())
                            .postTaxTotal(salaryHistory.getPostTaxTotal())
                            .bonus(salaryHistory.getBonus())
                            .workTime(salaryHistory.getWorkTime())
                            .baseSalary(employee.get().getPosition().getBaseSalary())
                            .totalSalary(salary * salaryHistory.getWorkTime())
                            .overTime(salaryHistory.getOverTime())
                            .overSalary(salaryHistory.getOverTime() * (salary * salaryHistory.getWorkTime() * 0.01))
                            .nationalPension(salaryHistory.getNationalPension())
                            .healthInsurance(salaryHistory.getHealthInsurance())
                            .longTermCare(salaryHistory.getLongTermCare())
                            .employmentInsurance(salaryHistory.getEmploymentInsurance())
                            .incomeTax(salaryHistory.getIncomeTax())
                            .localIncomeTax(salaryHistory.getLocalIncomeTax())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public SalaryHistoryDTO createSalary(SalaryHistoryDTO dto) {
        Optional<Employee> employeeEntity = employeeRepository.findByIdWithDetails(dto.getEmployeeId());
        if (employeeEntity.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사원입니다.");
        }

        Employee employee = employeeEntity.get();
        Position position = employee.getPosition();

        LocalDate salaryDate = (dto.getSalaryDate() != null) ? dto.getSalaryDate() : LocalDate.now();
        YearMonth currentMonth = YearMonth.from(salaryDate);
        YearMonth previousMonth = getPreviousMonth(currentMonth);

        long totalWorkHours = attendanceService.calculateTotalWorkHours(employee.getEmployeeId(), previousMonth);

        long years = ChronoUnit.YEARS.between(employee.getJoinDate(), LocalDate.now());

        double baseSalary = calculateSalary(position.getBaseSalary(), years) * totalWorkHours;

        long totalOverHours = overtimeService.getTotalOvertimeHoursForMonth(employee.getEmployeeId(), previousMonth) / 60;

        double overSalary = (baseSalary * 0.01) * totalOverHours;

        double bonus = 0;
        int monthValue = currentMonth.getMonthValue();
        if (monthValue == 1 || monthValue == 7) {
            bonus = calculateBonus(employee, baseSalary, currentMonth.getYear(), monthValue);
        }

        double preTaxTotal = baseSalary + bonus + overSalary;

        List<Deduct> deducts = deductRepository.findAll();
        double nationalPension = calculateDeduction(preTaxTotal, deducts, "국민연금");
        double healthInsurance = calculateDeduction(preTaxTotal, deducts, "건강보험");
        double longTermCare = healthInsurance * calculateDeductionRate(deducts, "장기요양");
        double employmentInsurance = calculateDeduction(preTaxTotal, deducts, "고용보험");
        double incomeTax = calculateDeduction(preTaxTotal, deducts, "소득세");
        double localIncomeTax = incomeTax * calculateDeductionRate(deducts, "지방소득세");

        double postTaxTotal = preTaxTotal - (nationalPension + healthInsurance + longTermCare +
                employmentInsurance + incomeTax + localIncomeTax);

        SalaryHistory salaryHistory = SalaryHistory.builder()
                .employee(employee)
                .salaryMonth(previousMonth.atDay(10).atStartOfDay())
                .preTaxTotal(preTaxTotal)
                .nationalPension(nationalPension)
                .healthInsurance(healthInsurance)
                .longTermCare(longTermCare)
                .employmentInsurance(employmentInsurance)
                .incomeTax(incomeTax)
                .localIncomeTax(localIncomeTax)
                .postTaxTotal(postTaxTotal)
                .bonus(bonus)
                .workTime(totalWorkHours)
                .overTime(totalOverHours)
                .build();

        SalaryHistory savedSalaryHistory = salaryHistoryRepository.save(salaryHistory);
        return convertToDTO(savedSalaryHistory);
    }

    // 근무 연수에 따른 5% 복리 적용
    private double calculateSalary(double baseSalary, long years) {

        for (long i = 0; i < years; i++) {
            baseSalary *= 1.05;
        }

        return baseSalary;
    }

    // 이전 달 계산 메서드
    private YearMonth getPreviousMonth(YearMonth currentMonth) {
        if (currentMonth.getMonthValue() == 1) {
            // 1월이면 작년 12월 반환
            return YearMonth.of(currentMonth.getYear() - 1, 12);
        } else {
            // 그 외의 경우 이전 달 반환
            return currentMonth.minusMonths(1);
        }
    }

    private double calculateBonus(Employee employee, double baseSalary, int currentYear, int currentMonth) {
        List<Evaluation> evaluations = evaluationRepository.findByEmployee_EmployeeId(employee.getEmployeeId());

        List<Evaluation> filteredEvaluations = evaluations.stream()
                .filter(evaluation -> {
                    LocalDateTime updatedAt = evaluation.getUpdatedAt();
                    return updatedAt.getYear() == currentYear &&
                            ((currentMonth == 1 && updatedAt.getMonthValue() >= 7) ||
                                    (currentMonth == 7 && updatedAt.getMonthValue() <= 6));
                })
                .sorted(Comparator.comparing(Evaluation::getUpdatedAt).reversed())
                .toList();

        if (!filteredEvaluations.isEmpty()) {
            Evaluation latestEvaluation = filteredEvaluations.get(0);
            double score = latestEvaluation.getScore();
            double bonusRate = calculateBonusRate(score);
            return baseSalary * bonusRate;
        }
        return 0;
    }

    private double calculateBonusRate(double score) {
        if (score < 80) {
            return 0.035; // 80점 이하 -> 3.5%
        } else if (score <= 89) {
            return 0.05; // 80~89점 -> 5%
        } else {
            return 0.065; // 90~100점 -> 6.5%
        }
    }

    private Double calculateDeduction(Double preTaxTotal, List<Deduct> deducts, String deductionName) {
        return deducts.stream()
                .filter(deduct -> deduct.getDeductName().equals(deductionName))
                .map(deduct -> preTaxTotal * deduct.getDeduction_rate())
                .findFirst()
                .orElse(0.0);
    }

    private Double calculateDeductionRate(List<Deduct> deducts, String deductionName) {
        return deducts.stream()
                .filter(deduct -> deduct.getDeductName().equals(deductionName))
                .map(Deduct::getDeduction_rate)
                .findFirst()
                .orElse(0.0);
    }

    private SalaryHistoryDTO convertToDTO(SalaryHistory salaryHistory) {
        return SalaryHistoryDTO.builder()
                .salaryId(salaryHistory.getSalaryHistoryId())
                .employeeId(salaryHistory.getEmployee().getEmployeeId())
                .salaryMonth(salaryHistory.getSalaryMonth())
                .preTaxTotal(salaryHistory.getPreTaxTotal())
                .postTaxTotal(salaryHistory.getPostTaxTotal())
                .nationalPension(salaryHistory.getNationalPension())
                .healthInsurance(salaryHistory.getHealthInsurance())
                .longTermCare(salaryHistory.getLongTermCare())
                .employmentInsurance(salaryHistory.getEmploymentInsurance())
                .incomeTax(salaryHistory.getIncomeTax())
                .localIncomeTax(salaryHistory.getLocalIncomeTax())
                .build();
    }

    public Double getLastThreeMonthsSalarySum(String employeeId) {
        LocalDateTime lastCheckInDate = getLastCheckInDate(employeeId);
        LocalDateTime threeMonthsAgo = lastCheckInDate.minusMonths(3);

        List<SalaryHistory> salaryHistories = salaryHistoryRepository.findSalaryHistoriesByEmployeeIdAndSalaryMonthBetween(
                employeeId, threeMonthsAgo, lastCheckInDate);

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

        AttendanceDTO response = attendanceService.getLatestAttendanceRecord(employeeId);

        if (response != null) {
            return response.getCheckIn();
        } else {
            return LocalDateTime.now();
        }
    }

}
