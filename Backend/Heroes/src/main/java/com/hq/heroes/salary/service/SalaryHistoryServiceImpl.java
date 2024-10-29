package com.hq.heroes.salary.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.evaluation.repository.EvaluationRepository;
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
import java.time.ZoneId;
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

    @Override
    public List<SalaryHistoryDTO> getAllSalaries(String employeeId) {
        List<SalaryHistory> histories = salaryHistoryRepository.findByEmployee_EmployeeId(employeeId);

        // 급여 기록이 없으면 예외 처리
        if (histories.isEmpty()) {
            throw new EntityNotFoundException("급여 이력이 존재하지 않습니다.");
        }

        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalaryHistoryDTO createSalary(SalaryHistoryDTO dto) {
        Optional<Employee> employeeEntity = employeeRepository.findById(dto.getEmployeeId());
        if (employeeEntity.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사원입니다.");
        }

        Employee employee = employeeEntity.get();
        Position position = employee.getPosition();

        // 현재 날짜 정보
        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = getPreviousMonth(currentMonth);  // 이전 달 계산

        // 총 근무 시간
        int totalWorkHours = attendanceService.calculateTotalWorkHours(employee.getEmployeeId(), previousMonth);

        // 근무 연수 계산
        long years = ChronoUnit.YEARS.between(employee.getJoinDate(), LocalDate.now());

        // 기본급 계산
        double baseSalary = position.getBaseSalary() * totalWorkHours;

        // 근무 연수에 따른 5% 복리 적용
        for (int i = 0; i < years; i++) {
            baseSalary *= 1.05;
        }

        // 연장 근로 시간

        // 연장 근로 수당 계산

        // 성과급 계산 (1월 또는 7월에만 적용)
        double bonus = 0;
        int monthValue = currentMonth.getMonthValue();
        if (monthValue == 1 || monthValue == 7) {
            bonus = calculateBonus(employee, currentMonth.getYear(), monthValue);
        }

        // 세전 총액 계산
        double preTaxTotal = baseSalary + bonus;

        // 공제 항목 계산
        List<Deduct> deducts = deductRepository.findAll();
        double nationalPension = calculateDeduction(preTaxTotal, deducts, "국민연금");
        double healthInsurance = calculateDeduction(preTaxTotal, deducts, "건강보험");
        double longTermCare = healthInsurance * calculateDeductionRate(deducts, "장기요양");
        double employmentInsurance = calculateDeduction(preTaxTotal, deducts, "고용보험");
        double incomeTax = calculateDeduction(preTaxTotal, deducts, "소득세");
        double localIncomeTax = incomeTax * calculateDeductionRate(deducts, "지방소득세");

        // 세후 총액 계산
        double postTaxTotal = preTaxTotal - (nationalPension + healthInsurance + longTermCare +
                employmentInsurance + incomeTax + localIncomeTax);

        // SalaryHistory 엔티티 생성 및 저장
        SalaryHistory salaryHistory = SalaryHistory.builder()
                .employee(employee)
                .salaryMonth(dto.getSalaryMonth())
                .preTaxTotal(preTaxTotal)
                .nationalPension(nationalPension)
                .healthInsurance(healthInsurance)
                .longTermCare(longTermCare)
                .employmentInsurance(employmentInsurance)
                .incomeTax(incomeTax)
                .localIncomeTax(localIncomeTax)
                .postTaxTotal(postTaxTotal)
                .bonus(bonus)
                .workTime(totalWorkHours/60)
                .build();

        SalaryHistory savedSalaryHistory = salaryHistoryRepository.save(salaryHistory);
        return convertToDTO(savedSalaryHistory);
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

    // 성과급 계산 메서드
    private double calculateBonus(Employee employee, int currentYear, int currentMonth) {
        List<Evaluation> evaluations = evaluationRepository.findByEmployee_EmployeeId(employee.getEmployeeId());

        List<Evaluation> filteredEvaluations = evaluations.stream()
                .filter(evaluation -> {
                    LocalDateTime updatedAt = evaluation.getUpdatedAt().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return updatedAt.getYear() == currentYear &&
                            ((currentMonth == 1 && updatedAt.getMonthValue() >= 7) ||
                                    (currentMonth == 7 && updatedAt.getMonthValue() <= 6));
                })
                .sorted(Comparator.comparing(Evaluation::getUpdatedAt).reversed())
                .collect(Collectors.toList());

        if (!filteredEvaluations.isEmpty()) {
            Evaluation latestEvaluation = filteredEvaluations.get(0);
            double score = latestEvaluation.getScore();
            double bonusRate = calculateBonusRate(score);
            return employee.getPosition().getBaseSalary() * bonusRate;
        }
        return 0;
    }

    // 성과급 비율 계산 메서드
    private double calculateBonusRate(double score) {
        if (score < 80) {
            return 0.035; // 80점 이하 -> 3.5%
        } else if (score <= 89) {
            return 0.05; // 80~89점 -> 5%
        } else {
            return 0.065; // 90~100점 -> 6.5%
        }
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
