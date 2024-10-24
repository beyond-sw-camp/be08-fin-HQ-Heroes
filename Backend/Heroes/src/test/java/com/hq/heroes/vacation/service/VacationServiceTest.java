package com.hq.heroes.vacation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.repository.VacationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacationServiceTest {

    @Mock
    VacationRepository vacationRepository;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    VacationServiceImpl vacationService;

    Employee employee;
    Employee approver;
    Employee applicant;
    Vacation vacation;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId("2024100006")
                .employeeName("안유진")
                .build();

        approver = Employee.builder()
                .employeeId("2024100007")
                .employeeName("김채원")
                .build();

        applicant = Employee.builder()
                .employeeId("2024100008")
                .employeeName("장원영")
                .build();

        vacation = Vacation.builder()
                .employee(employee)
                .approver(approver)
                .applicant(applicant)
                .vacationStartDate(LocalDate.of(2024, 1, 1))
                .vacationStartTime(LocalTime.of(9, 0))
                .vacationEndDate(LocalDate.of(2024, 1, 5))
                .vacationEndTime(LocalTime.of(18, 0))
                .vacationStatus(VacationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("휴가 제출 성공 테스트")
    void submitVacationSuccess() {
        VacationDTO vacationDTO = VacationDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .applicantName("장원영")
                .vacationStartDate(LocalDate.of(2024, 1, 1))
                .vacationStartTime(LocalTime.of(9, 0))
                .vacationEndDate(LocalDate.of(2024, 1, 5))
                .vacationEndTime(LocalTime.of(18, 0))
                .vacationStatus(VacationStatus.PENDING)
                .build();

        // when
        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));
        when(employeeRepository.findByEmployeeName("장원영")).thenReturn(Optional.of(applicant));
        when(vacationRepository.save(any(Vacation.class))).thenReturn(vacation);

        vacationService.submitVacation(vacationDTO);

        // then
        verify(vacationRepository, times(1)).save(any(Vacation.class));
    }


    @Test
    @DisplayName("휴가 제출 실패 테스트 - 휴가 시작일이 종료일보다 늦음")
    void submitVacationFailInvalidDates() {
        VacationDTO vacationDTO = VacationDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .applicantName("장원영")
                .vacationStartDate(LocalDate.of(2024, 1, 5))
                .vacationEndDate(LocalDate.of(2024, 1, 1))
                .build();

        // when
        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));
        when(employeeRepository.findByEmployeeName("장원영")).thenReturn(Optional.of(applicant));

        // then
        assertThatThrownBy(() -> vacationService.submitVacation(vacationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("휴가 시작일은 종료일보다 빠를 수 없습니다.");

        verify(vacationRepository, never()).save(any(Vacation.class));
    }

    @Test
    @DisplayName("휴가 제출 실패 테스트 - 대리인 정보 없음")
    void submitVacationFailNoEmployee() {
        VacationDTO vacationDTO = VacationDTO.builder()
                .employeeId("9999999999")
                .approverName("김채원")
                .applicantName("장원영")
                .vacationStartDate(LocalDate.of(2024, 1, 1))
                .vacationEndDate(LocalDate.of(2024, 1, 5))
                .build();

        // when
        when(employeeRepository.findByEmployeeId("9999999999")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> vacationService.submitVacation(vacationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 대리인을 찾을 수 없습니다.");

        verify(vacationRepository, never()).save(any(Vacation.class));
    }

    @Test
    @DisplayName("휴가 승인 성공 테스트")
    void approveVacationSuccess() {
        // when
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.of(vacation));

        vacationService.approveVacation(1L);

        // then
        assertThat(vacation.getVacationStatus()).isEqualTo(VacationStatus.APPROVED);
        verify(vacationRepository, times(1)).save(vacation);
    }

    @Test
    @DisplayName("휴가 승인 실패 테스트 - 휴가 ID가 존재하지 않음")
    void approveVacationFailNoVacationFound() {
        // when
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> vacationService.approveVacation(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("휴가를 찾을 수 없습니다.");

        verify(vacationRepository, never()).save(any(Vacation.class));
    }

    @Test
    @DisplayName("휴가 거절 성공 테스트")
    void rejectVacationSuccess() {
        // when
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.of(vacation));

        vacationService.rejectVacation(1L);

        // then
        assertThat(vacation.getVacationStatus()).isEqualTo(VacationStatus.REJECTED);
        verify(vacationRepository, times(1)).save(vacation);
    }

    @Test
    @DisplayName("휴가 거절 실패 테스트 - 휴가 ID가 존재하지 않음")
    void rejectVacationFailNoVacationFound() {
        // when
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> vacationService.rejectVacation(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("휴가를 찾을 수 없습니다.");

        verify(vacationRepository, never()).save(any(Vacation.class));
    }


    @Test
    @DisplayName("승인된 휴가 조회 성공 테스트")
    void getApprovedVacationsByEmployeeIdSuccess() {
        List<Vacation> vacations = new ArrayList<>();
        vacations.add(vacation);

        // when
        when(vacationRepository.findApprovedVacationsByEmployeeId("2024100006"))
                .thenReturn(vacations);

        List<VacationDTO> result = vacationService.getApprovedVacationsByEmployeeId("2024100006");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEmployeeId()).isEqualTo("2024100006");
        assertThat(result.get(0).getVacationStatus()).isEqualTo(VacationStatus.PENDING);

        verify(vacationRepository, times(1)).findApprovedVacationsByEmployeeId("2024100006");
    }

    @Test
    @DisplayName("승인된 휴가 조회 실패 테스트 - 승인된 휴가 없음")
    void getApprovedVacationsByEmployeeIdNoVacations() {
        // when
        when(vacationRepository.findApprovedVacationsByEmployeeId("2024100006"))
                .thenReturn(new ArrayList<>());

        List<VacationDTO> result = vacationService.getApprovedVacationsByEmployeeId("2024100006");

        // then
        assertThat(result).isEmpty();
        verify(vacationRepository, times(1)).findApprovedVacationsByEmployeeId("2024100006");
    }

}