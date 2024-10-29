package com.hq.heroes.salary.service;

import com.hq.heroes.attendance.service.AttendanceService;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.Deduct;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.salary.repository.DeductRepository;
import com.hq.heroes.salary.repository.SalaryHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalaryHistoryServiceTest {

    @Mock
    private SalaryHistoryRepository salaryHistoryRepository;

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DeductRepository deductRepository;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private SalaryHistoryServiceImpl salaryHistoryService;

    private Employee employee;
    private Position position;
    private SalaryHistoryDTO salaryHistoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        position = Position.builder()
                .positionId(1L)
                .baseSalary(3000.0)
                .build();

        employee = Employee.builder()
                .employeeId("2024100002")
                .employeeName("김철수")
                .position(position)
                .build();

        salaryHistoryDTO = SalaryHistoryDTO.builder()
                .salaryId(1L)
                .employeeId(employee.getEmployeeId())
                .salaryMonth(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("급여 기록 생성 성공 테스트")
    void 급여기록생성성공() {
        // Given
        employee.setJoinDate(LocalDate.of(2020, 1, 1)); // 입사일
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(attendanceService.calculateTotalWorkHours(employee.getEmployeeId(), YearMonth.now())).thenReturn(160); // 총 근무시간

        // Salary 객체 생성 및 성과급 비율 설정
        Salary salary = new Salary();
        salary.setPerformanceBonus(0.1); // 성과급 비율을 10%로 설정
        when(salaryRepository.findByPosition(position)).thenReturn(Optional.of(salary));

        // 공제 항목 설정
        when(deductRepository.findAll()).thenReturn(Arrays.asList(
                Deduct.builder()
                        .deductName("국민연금")
                        .deduction_rate(0.045)
                        .build(),
                Deduct.builder()
                        .deductName("건강보험")
                        .deduction_rate(0.034)
                        .build(),
                Deduct.builder()
                        .deductName("장기요양")
                        .deduction_rate(0.015)
                        .build(),
                Deduct.builder()
                        .deductName("소득세")
                        .deduction_rate(0.03)
                        .build(),
                Deduct.builder()
                        .deductName("지방소득세")
                        .deduction_rate(0.01)
                        .build()
        )); // 공제 항목 추가

        // 고정된 날짜 및 시간 설정
        LocalDateTime fixedDateTime = LocalDateTime.of(2024, 10, 25, 11, 36, 35);

        // SalaryHistory 객체 모킹 및 Employee 설정
        SalaryHistory savedSalaryHistory = new SalaryHistory();
        savedSalaryHistory.setSalaryHistoryId(1L);
        savedSalaryHistory.setEmployee(employee);
        savedSalaryHistory.setSalaryMonth(fixedDateTime);
        when(salaryHistoryRepository.save(any(SalaryHistory.class))).thenReturn(savedSalaryHistory);

        // SalaryHistoryDTO 생성 및 설정
        SalaryHistoryDTO salaryHistoryDTO = SalaryHistoryDTO.builder()
                .employeeId(employee.getEmployeeId())
                .salaryMonth(fixedDateTime) // 고정된 급여 월 설정
                .build();

        // When
        SalaryHistoryDTO result = salaryHistoryService.createSalary(salaryHistoryDTO);

        // Then
        assertNotNull(result);
        assertEquals(employee.getEmployeeId(), result.getEmployeeId());
        assertEquals(fixedDateTime, result.getSalaryMonth());

        verify(employeeRepository, times(1)).findById(employee.getEmployeeId());
        verify(attendanceService, times(1)).calculateTotalWorkHours(employee.getEmployeeId(), YearMonth.now());
        verify(salaryRepository, times(1)).findByPosition(position);
        verify(deductRepository, times(1)).findAll();
        verify(salaryHistoryRepository, times(1)).save(any(SalaryHistory.class));
    }

    @Test
    @DisplayName("급여 기록 생성 실패 테스트 - 존재하지 않는 사원 ID")
    void 급여기록생성실패_없는사원() {
        // Given
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> salaryHistoryService.createSalary(salaryHistoryDTO));

        verify(employeeRepository, times(1)).findById(employee.getEmployeeId());
        verify(attendanceService, never()).calculateTotalWorkHours(anyString(), any());
        verify(salaryRepository, never()).findByPosition(any());
        verify(deductRepository, never()).findAll();
        verify(salaryHistoryRepository, never()).save(any(SalaryHistory.class));
    }

    @Test
    @DisplayName("급여 기록 조회 성공 테스트")
    void 급여기록조회성공() {
        // Given
        Employee employee = new Employee();
        employee.setEmployeeId("testEmployeeId"); // Employee ID를 설정합니다.

        SalaryHistory salaryHistory = new SalaryHistory();
        salaryHistory.setEmployee(employee); // SalaryHistory에 employee를 설정합니다.

        when(salaryHistoryRepository.findByEmployee_EmployeeId(employee.getEmployeeId())).thenReturn(List.of(salaryHistory));

        // When
        List<SalaryHistoryDTO> result = salaryHistoryService.getAllSalaries(employee.getEmployeeId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(salaryHistoryRepository, times(1)).findByEmployee_EmployeeId(employee.getEmployeeId());
    }

    @Test
    @DisplayName("급여 기록 조회 실패 테스트 - 이력 없음")
    void 급여기록조회실패_내역없음() {
        // Given
        when(salaryHistoryRepository.findByEmployee_EmployeeId(employee.getEmployeeId())).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> salaryHistoryService.getAllSalaries(employee.getEmployeeId()));

        verify(salaryHistoryRepository, times(1)).findByEmployee_EmployeeId(employee.getEmployeeId());
    }
}
