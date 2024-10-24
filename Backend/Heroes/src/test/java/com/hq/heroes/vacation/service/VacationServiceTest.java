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
                .vacationStartTime(LocalTime.of(9, 0)) // 휴가 시작 시간
                .vacationEndDate(LocalDate.of(2024, 1, 5))
                .vacationEndTime(LocalTime.of(18, 0)) // 휴가 종료 시간
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

        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));
        when(employeeRepository.findByEmployeeName("장원영")).thenReturn(Optional.of(applicant));

        when(vacationRepository.save(any(Vacation.class))).thenReturn(vacation);

        vacationService.submitVacation(vacationDTO);

        verify(vacationRepository, times(1)).save(any(Vacation.class));
    }


    @Test
    @DisplayName("휴가 제출 실패 테스트 - 휴가 시작일이 종료일보다 늦음")
    void submitVacationFailInvalidDates() {
        // 가짜 DTO 객체 생성 (시작일이 종료일보다 늦음)
        VacationDTO vacationDTO = VacationDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .applicantName("장원영")
                .vacationStartDate(LocalDate.of(2024, 1, 5))
                .vacationEndDate(LocalDate.of(2024, 1, 1))
                .build();

        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));
        when(employeeRepository.findByEmployeeName("장원영")).thenReturn(Optional.of(applicant));

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

        when(employeeRepository.findByEmployeeId("9999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacationService.submitVacation(vacationDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 대리인을 찾을 수 없습니다.");

        verify(vacationRepository, never()).save(any(Vacation.class));
    }



    @Test
    @DisplayName("휴가 승인 TEST")
    void approveVacation() {
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.of(vacation));

        vacationService.approveVacation(1L);

        assertThat(vacation.getVacationStatus()).isEqualTo(VacationStatus.APPROVED);

        verify(vacationRepository, times(1)).save(vacation);
    }

    @Test
    @DisplayName("휴가 거절 TEST")
    void rejectVacation() {
        when(vacationRepository.findById(anyLong())).thenReturn(Optional.of(vacation));

        vacationService.rejectVacation(1L);

        assertThat(vacation.getVacationStatus()).isEqualTo(VacationStatus.REJECTED);

        verify(vacationRepository, times(1)).save(vacation);
    }

    @Test
    @DisplayName("승인된 휴가 조회 TEST")
    void getApprovedVacationsByEmployeeId() {
        List<Vacation> vacations = new ArrayList<>();
        vacations.add(vacation);

        when(vacationRepository.findApprovedVacationsByEmployeeId("2024100006"))
                .thenReturn(vacations);

        List<VacationDTO> result = vacationService.getApprovedVacationsByEmployeeId("2024100006");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmployeeId()).isEqualTo("2024100006");

        verify(vacationRepository, times(1))
                .findApprovedVacationsByEmployeeId("2024100006");
    }
}
