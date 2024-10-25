package com.hq.heroes.salary.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.salary.dto.SalaryDTO;
import com.hq.heroes.salary.entity.Salary;
import com.hq.heroes.salary.repository.SalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private Employee employee;
    private Position position;
    private SalaryDTO salaryDTO;

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
                .role(Role.ROLE_USER)
                .position(position)
                .build();

        salaryDTO = SalaryDTO.builder()
                .employeeId(employee.getEmployeeId())
                .positionId(position.getPositionId())
                .build();
    }

    @Test
    @DisplayName("급여 정보 생성 성공 테스트")
    void 급여정보생성성공() {
        // Given
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.of(employee));
        when(positionRepository.findById(position.getPositionId())).thenReturn(Optional.of(position));
        when(salaryRepository.save(any(Salary.class))).thenAnswer(invocation -> {
            Salary savedSalary = invocation.getArgument(0);
            savedSalary.setEmployee(employee);
            return savedSalary;
        });

        // When
        SalaryDTO result = salaryService.createSalary(salaryDTO);

        // Then
        assertNotNull(result);
        assertEquals(employee.getEmployeeId(), result.getEmployeeId());
        assertEquals(position.getPositionId(), result.getPositionId());

        verify(employeeRepository, times(1)).findById(employee.getEmployeeId());
        verify(positionRepository, times(1)).findById(position.getPositionId());
        verify(salaryRepository, times(1)).save(any(Salary.class));
    }

    @Test
    @DisplayName("급여 정보 생성 실패 테스트 - 존재하지 않는 사원 ID")
    void 급여정보생성실패_없는사원() {
        // Given
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> salaryService.createSalary(salaryDTO));

        verify(employeeRepository, times(1)).findById(employee.getEmployeeId());
        verify(positionRepository, never()).findById(anyLong());
        verify(salaryRepository, never()).save(any(Salary.class));
    }

    @Test
    @DisplayName("급여 정보 조회 성공 테스트")
    void 급여정보조회성공() {
        // Given
        Long positionId = 1L;
        Position position = Position.builder()
                .positionId(positionId)
                .baseSalary(1000.0)
                .build();

        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));
        String employeeId = "2024100002";
        Employee employee = Employee.builder()
                .employeeId(employeeId)
                .position(position)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // When
        Optional<Double> baseSalary = salaryService.getBaseSalaryByPositionId(employeeId);

        // Then
        assertTrue(baseSalary.isPresent());
        assertEquals(1000.0, baseSalary.get());
    }

    @Test
    @DisplayName("급여 정보 조회 실패 테스트 - 존재하지 않는 직원 ID")
    void 급여정보조회실패_없는사원() {
        // Given
        when(employeeRepository.findById(employee.getEmployeeId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> salaryService.getBaseSalaryByPositionId(employee.getEmployeeId()));

        verify(employeeRepository, times(1)).findById(employee.getEmployeeId());
        verify(positionRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("급여 정보 목록 조회 성공 테스트")
    void 급여정보목록조회성공() {
        // Given
        String employeeId = "2024100002";
        Salary salary = new Salary();
        salary.setPosition(position);
        salary.setEmployee(employee);

        List<Salary> salaries = List.of(salary);
        when(salaryRepository.findByEmployeeId(employeeId)).thenReturn(salaries);

        // When
        List<SalaryDTO> result = salaryService.getAllSalariesByEmployeeId(employeeId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(position.getPositionId(), result.get(0).getPositionId());

        verify(salaryRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    @DisplayName("급여 정보 목록 조회 실패 테스트 - 급여 없음")
    void 급여정보목록조회실패_없는급여() {
        // Given
        String employeeId = "2024100002";
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> salaryService.getAllSalariesByEmployeeId(employeeId));
    }

    @Test
    @DisplayName("급여 정보 목록 조회 실패 테스트 - 존재하지 않는 직원 ID")
    void 급여정보목록조회실패_없는사원() {
        // Given
        when(salaryRepository.findByEmployeeId(employee.getEmployeeId())).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> salaryService.getAllSalariesByEmployeeId(employee.getEmployeeId()));

        verify(salaryRepository, times(1)).findByEmployeeId(employee.getEmployeeId());
    }
}
