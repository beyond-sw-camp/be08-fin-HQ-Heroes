package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
                .performanceDate(LocalDateTime.now())
                .build();

        // Salary 저장
        Salary savedSalary = salaryRepository.save(salary);

        // 저장된 Salary를 SalaryDTO로 변환하여 반환
        return SalaryDTO.builder()
                .salaryId(savedSalary.getSalaryId())
                .employeeId(savedSalary.getEmployee().getEmployeeId())
                .positionId(savedSalary.getPosition().getPositionId())
                .baseSalary(savedSalary.getPosition().getBaseSalary())
                .build();
    }

    @Override
    public SalaryDTO createSalary(SalaryDTO salaryDTO, List<Evaluation> evaluations) {
        Employee employee = employeeRepository.findById(salaryDTO.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Position position = positionRepository.findById(employee.getPosition().getPositionId())
                .orElseThrow(() -> new EntityNotFoundException("Position not found"));

        // 현재 월
        int currentMonth = LocalDateTime.now().getMonthValue();

        // 현재 년도
        int currentYear = LocalDateTime.now().getYear();

        // 필터링된 평가 리스트
        Optional<Evaluation> filteredEvaluation = Optional.empty(); // Optional로 초기화

        // 1월인 경우: 7월 ~ 12월의 평가 데이터 가져오기
        if (currentMonth == 1) {
            filteredEvaluation = evaluations.stream()
                    .filter(evaluation -> {
                        LocalDateTime updatedAt = evaluation.getUpdatedAt().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        return (updatedAt.getYear() == currentYear) &&
                                (updatedAt.getMonth() == Month.JULY ||
                                        updatedAt.getMonth() == Month.AUGUST ||
                                        updatedAt.getMonth() == Month.SEPTEMBER ||
                                        updatedAt.getMonth() == Month.OCTOBER ||
                                        updatedAt.getMonth() == Month.NOVEMBER ||
                                        updatedAt.getMonth() == Month.DECEMBER); // 7~12월
                    })
                    .sorted(Comparator.comparing(Evaluation::getUpdatedAt).reversed())
                    .findFirst(); // 첫 번째 요소를 가져옴
        }
        // 7월인 경우: 1월 ~ 6월의 평가 데이터 가져오기
        else if (currentMonth == 7) {
            filteredEvaluation = evaluations.stream()
                    .filter(evaluation -> {
                        LocalDateTime updatedAt = evaluation.getUpdatedAt().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        return (updatedAt.getYear() == currentYear) &&
                                (updatedAt.getMonth() == Month.JANUARY ||
                                        updatedAt.getMonth() == Month.FEBRUARY ||
                                        updatedAt.getMonth() == Month.MARCH ||
                                        updatedAt.getMonth() == Month.APRIL ||
                                        updatedAt.getMonth() == Month.MAY ||
                                        updatedAt.getMonth() == Month.JUNE); // 1~6월
                    })
                    .sorted(Comparator.comparing(Evaluation::getUpdatedAt).reversed())
                    .findFirst(); // 첫 번째 요소를 가져옴
        }

        // 평가 데이터가 존재하는 경우 처리
        if (filteredEvaluation.isPresent()) {

            double score = filteredEvaluation.get().getScore();

            // 성과급 비율 계산
            double rate = calculatePerformanceBonus(score);

            LocalDateTime updatedAt = filteredEvaluation.get().getUpdatedAt().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Salary 엔티티로 변환
            Salary salary = Salary.builder()
                    .employee(employee)
                    .position(position)
                    .performanceDate(updatedAt)
                    .performanceBonus(rate)
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
        } else {
            throw new EntityNotFoundException("평가 데이터가 존재하지 않습니다.");
        }

    }

    // 성과급 비율 계산
    private double calculatePerformanceBonus(double score) {
        if (score < 80) {
            return 0.035; // 80점 이하 -> 35%
        } else if (score <= 89) {
            return 0.05; // 80~89점 -> 50%
        } else {
            return 0.065; // 90~100점 -> 65%
        }
    }

    @Override
    public Optional<Double> getBaseSalaryByPositionId(String employeeId) {
        // 1. 사원 조회 (employeeId로 사원의 positionId 찾기)
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 직원이 존재하지 않습니다."));

        // 2. 직책 정보 조회 (positionId로 baseSalary 찾기)
        return Optional.ofNullable(employee.getPosition().getBaseSalary());
    }

    @Override
    public List<SalaryDTO> getAllSalariesByEmployeeId(String employeeId) {
        List<Salary> salaries = salaryRepository.findByEmployeeId(employeeId);

        if (salaries.isEmpty()) {
            throw new EntityNotFoundException("No salaries found for employee ID: " + employeeId); // 급여 데이터가 없는 경우 예외 처리
        }

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
