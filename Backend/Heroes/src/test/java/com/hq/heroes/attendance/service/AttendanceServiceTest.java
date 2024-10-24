package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import com.hq.heroes.attendance.repository.AttendanceRepository;
import com.hq.heroes.auth.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
public class AttendanceServiceTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @InjectMocks
    AttendanceServiceImpl attendanceService;

    Employee employee;
    Attendance attendance;

    @BeforeEach
    void setUp() {
        // given: 테스트용 Employee 및 Attendance 객체 초기화
        employee = Employee.builder()
                .employeeId("2024100006")
                .employeeName("안유진")
                .build();

        attendance = Attendance.builder()
                .employee(employee)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.NORMAL)
                .build();
    }

    @Test
    @DisplayName("출근 처리 성공 테스트")
    void checkIn() {
        // given
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        // when
        Attendance result = attendanceService.checkIn(employee);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmployee().getEmployeeId()).isEqualTo("2024100006");
        assertThat(result.getStatus()).isEqualTo(AttendanceStatus.NORMAL);

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("퇴근 처리 성공 테스트")
    void checkOut() {
        // given
        when(attendanceRepository.findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(employee, AttendanceStatus.NORMAL))
                .thenReturn(Optional.of(attendance));

        attendance.setCheckIn(LocalDateTime.now().minusHours(8));

        // when
        attendanceService.checkOut(employee);

        // then
        assertThat(attendance.getCheckOut()).isNotNull();
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LEAVE_WORK);

        verify(attendanceRepository, times(1)).save(attendance);
    }

    @Test
    @DisplayName("기간별 근태 기록 조회 성공 테스트")
    void findByEmployee_IdAndDateBetween() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // given
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(attendance);

        when(attendanceRepository.findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate))
                .thenReturn(attendances);

        // when
        List<AttendanceDTO> result = attendanceService.findByEmployee_IdAndDateBetween("2024100006", startDate, endDate);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmployeeId()).isEqualTo("2024100006");

        verify(attendanceRepository, times(1))
                .findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate);
    }

    @Test
    @DisplayName("기간별 근태 기록 조회 실패 테스트 - 근태 기록 없음")
    void findByEmployee_IdAndDateBetweenFailNoRecords() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // given
        when(attendanceRepository.findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate))
                .thenReturn(new ArrayList<>());

        // when
        List<AttendanceDTO> result = attendanceService.findByEmployee_IdAndDateBetween("2024100006", startDate, endDate);

        // then
        assertThat(result).isEmpty();

        verify(attendanceRepository, times(1))
                .findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate);
    }

    @Test
    @DisplayName("특정 월 총 근무 시간 계산 성공 테스트")
    void calculateTotalWorkHours() {
        String employeeId = "2024100006";
        YearMonth targetMonth = YearMonth.of(2024, 1);  // 2024년 1월

        // given
        when(attendanceRepository.findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue()))
                .thenReturn(160);

        // when
        int totalWorkHours = attendanceService.calculateTotalWorkHours(employeeId, targetMonth);

        // then
        assertThat(totalWorkHours).isEqualTo(160);

        verify(attendanceRepository, times(1)).findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue());
    }

    @Test
    @DisplayName("특정 월 총 근무 시간 계산 실패 테스트 - 근무 기록 없음")
    void calculateTotalWorkHoursFailNoRecords() {
        String employeeId = "2024100006";
        YearMonth targetMonth = YearMonth.of(2024, 1);  // 2024년 1월

        // given
        when(attendanceRepository.findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue()))
                .thenReturn(0);

        // when
        int totalWorkHours = attendanceService.calculateTotalWorkHours(employeeId, targetMonth);

        // then
        assertThat(totalWorkHours).isEqualTo(0);

        verify(attendanceRepository, times(1)).findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue());
    }
}
