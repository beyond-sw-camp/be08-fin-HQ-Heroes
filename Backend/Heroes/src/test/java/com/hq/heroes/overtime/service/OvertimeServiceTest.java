package com.hq.heroes.overtime.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.overtime.dto.OvertimeDTO;
import com.hq.heroes.overtime.entity.Overtime;
import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import com.hq.heroes.overtime.repository.OvertimeRepository;
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
public class OvertimeServiceTest {

    @Mock
    OvertimeRepository overtimeRepository;
    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    OvertimeServiceImpl overtimeService;

    Employee employee;
    Employee approver;
    Overtime overtime;

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

        overtime = Overtime.builder()
                .employee(employee)
                .approver(approver)
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))  // 종료일 추가
                .overtimeStartTime(LocalTime.of(18, 0))
                .overtimeEndTime(LocalTime.of(21, 0))
                .overtimeStatus(OvertimeStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("연장근로 제출 성공 테스트")
    void submitOvertimeSuccess() {
        OvertimeDTO overtimeDTO = OvertimeDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))  // 종료일 추가
                .overtimeStartTime(LocalTime.of(18, 0))
                .overtimeEndTime(LocalTime.of(21, 0))
                .overtimeStatus(OvertimeStatus.PENDING)
                .build();

        // when
        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));
        when(overtimeRepository.save(any(Overtime.class))).thenReturn(overtime);

        overtimeService.submitOvertime(overtimeDTO);

        // then
        verify(overtimeRepository, times(1)).save(any(Overtime.class));
    }

    @Test
    @DisplayName("연장근로 제출 실패 테스트 - 연장근로 종료 시간이 시작 시간보다 이른 경우")
    void submitOvertimeFailInvalidTimes() {
        // given: 유효하지 않은 연장근로 시간 설정
        OvertimeDTO overtimeDTO = OvertimeDTO.builder()
                .employeeId("2024100006")
                .approverName("김채원")
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))
                .overtimeStartTime(LocalTime.of(21, 0))  // 잘못된 시간 (종료 시간보다 늦음)
                .overtimeEndTime(LocalTime.of(18, 0))    // 잘못된 시간 (시작 시간보다 이름)
                .build();

        // when: 신청인의 정보가 올바르게 설정되어야 함
        when(employeeRepository.findByEmployeeId("2024100006")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeName("김채원")).thenReturn(Optional.of(approver));

        // then: 예외가 발생해야 함
        assertThatThrownBy(() -> overtimeService.submitOvertime(overtimeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연장근로 시작 시간이 종료 시간보다 늦을 수 없습니다.");

        // overtimeRepository.save()가 호출되지 않아야 함
        verify(overtimeRepository, never()).save(any(Overtime.class));
    }


    @Test
    @DisplayName("연장근로 제출 실패 테스트 - 신청인 정보 없음")
    void submitOvertimeFailNoEmployee() {
        OvertimeDTO overtimeDTO = OvertimeDTO.builder()
                .employeeId("9999999999")
                .approverName("김채원")
                .overtimeStartDate(LocalDate.of(2024, 1, 1))
                .overtimeEndDate(LocalDate.of(2024, 1, 1))  // 종료일 추가
                .overtimeStartTime(LocalTime.of(18, 0))
                .overtimeEndTime(LocalTime.of(21, 0))
                .build();

        // when
        when(employeeRepository.findByEmployeeId("9999999999")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> overtimeService.submitOvertime(overtimeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 신청인을 찾을 수 없습니다.");

        verify(overtimeRepository, never()).save(any(Overtime.class));
    }

    @Test
    @DisplayName("연장근로 승인 성공 테스트")
    void approveOvertimeSuccess() {
        // when
        when(overtimeRepository.findById(anyLong())).thenReturn(Optional.of(overtime));

        overtimeService.approveOvertime(1L);

        // then
        assertThat(overtime.getOvertimeStatus()).isEqualTo(OvertimeStatus.APPROVED);
        verify(overtimeRepository, times(1)).save(overtime);
    }

    @Test
    @DisplayName("연장근로 승인 실패 테스트 - 연장근로 ID가 존재하지 않음")
    void approveOvertimeFailNoOvertimeFound() {
        // when
        when(overtimeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> overtimeService.approveOvertime(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("연장근로를 찾을 수 없습니다.");

        verify(overtimeRepository, never()).save(any(Overtime.class));
    }

    @Test
    @DisplayName("연장근로 거절 성공 테스트")
    void rejectOvertimeSuccess() {
        // when
        when(overtimeRepository.findById(anyLong())).thenReturn(Optional.of(overtime));

        overtimeService.rejectOvertime(1L);

        // then
        assertThat(overtime.getOvertimeStatus()).isEqualTo(OvertimeStatus.REJECTED);
        verify(overtimeRepository, times(1)).save(overtime);
    }

    @Test
    @DisplayName("연장근로 거절 실패 테스트 - 연장근로 ID가 존재하지 않음")
    void rejectOvertimeFailNoOvertimeFound() {
        // when
        when(overtimeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> overtimeService.rejectOvertime(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("연장근로를 찾을 수 없습니다.");

        verify(overtimeRepository, never()).save(any(Overtime.class));
    }

    @Test
    @DisplayName("승인된 연장근로 조회 성공 테스트")
    void getApprovedOvertimesByEmployeeIdSuccess() {
        List<Overtime> overtimes = new ArrayList<>();
        overtimes.add(overtime);

        // when
        when(overtimeRepository.findApprovedOvertimesByEmployeeId("2024100006"))
                .thenReturn(overtimes);

        List<OvertimeDTO> result = overtimeService.getApprovedOvertimesByEmployeeId("2024100006");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEmployeeId()).isEqualTo("2024100006");
        assertThat(result.get(0).getOvertimeStatus()).isEqualTo(OvertimeStatus.PENDING);

        verify(overtimeRepository, times(1)).findApprovedOvertimesByEmployeeId("2024100006");
    }

    @Test
    @DisplayName("승인된 연장근로 조회 실패 테스트 - 승인된 연장근로 없음")
    void getApprovedOvertimesByEmployeeIdNoOvertimes() {
        // when
        when(overtimeRepository.findApprovedOvertimesByEmployeeId("2024100006"))
                .thenReturn(new ArrayList<>());

        List<OvertimeDTO> result = overtimeService.getApprovedOvertimesByEmployeeId("2024100006");

        // then
        assertThat(result).isEmpty();
        verify(overtimeRepository, times(1)).findApprovedOvertimesByEmployeeId("2024100006");
    }
}
