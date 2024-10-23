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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class AttendanceServiceTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @InjectMocks
    AttendanceServiceImpl attendanceService;

    Employee employee;
    Attendance attendance;

    @BeforeEach
    void setUp() {
        // 테스트용 Employee 객체 초기화
        employee = Employee.builder()
                .employeeId("2024100006")
                .employeeName("안유진")
                .build();

        // 가짜 Attendance 객체 생성
        attendance = Attendance.builder()
                .employee(employee)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.NORMAL)
                .build();
    }

    @Test
    @DisplayName("출근 처리 TEST")
    void checkIn() {
        // 스터빙 설정
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        // when (메서드 호출)
        Attendance result = attendanceService.checkIn(employee);

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.getEmployee().getEmployeeId()).isEqualTo("2024100006");
        assertThat(result.getStatus()).isEqualTo(AttendanceStatus.NORMAL);

        // 출근 기록 저장 여부 확인
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("퇴근 처리 TEST")
    void checkOut() {

        when(attendanceRepository.findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(employee, AttendanceStatus.NORMAL))
                .thenReturn(Optional.of(attendance));

        // 출근 기록 설정
        attendance.setCheckIn(LocalDateTime.now().minusHours(8));  // 8시간 전 출근

        // when (메서드 호출)
        attendanceService.checkOut(employee);

        // then (검증)
        assertThat(attendance.getCheckOut()).isNotNull();
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LEAVE_WORK);

        // 퇴근 기록 저장 여부 확인
        verify(attendanceRepository, times(1)).save(attendance);
    }

    @Test
    @DisplayName("기간별 근태 기록 조회 TEST")
    void findByEmployee_IdAndDateBetween() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // 가짜 근태 기록 리스트 생성
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(attendance);

        // 스터빙 설정
        when(attendanceRepository.findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate))
                .thenReturn(attendances);

        // when (메서드 호출)
        List<AttendanceDTO> result = attendanceService.findByEmployee_IdAndDateBetween("2024100006", startDate, endDate);

        // then (검증)
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEmployeeId()).isEqualTo("2024100006");

        // 리포지토리 호출 여부 확인
        verify(attendanceRepository, times(1))
                .findByEmployee_EmployeeIdAndCheckInBetween("2024100006", startDate, endDate);
    }

    @Test
    @DisplayName("특정 월 총 근무 시간 계산 TEST")
    void calculateTotalWorkHours() {
        String employeeId = "2024100006";
        YearMonth targetMonth = YearMonth.of(2024, 1);  // 2024년 1월

        // 리포지토리의 행동 설정 (해당 월의 총 근무 시간을 160시간으로 설정)
        when(attendanceRepository.findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue()))
                .thenReturn(160);

        // when (메서드 호출)
        int totalWorkHours = attendanceService.calculateTotalWorkHours(employeeId, targetMonth);

        // then (검증)
        assertThat(totalWorkHours).isEqualTo(160);  // 총 근무 시간이 160시간이어야 함

        // 리포지토리 호출 여부 확인
        verify(attendanceRepository, times(1)).findTotalWorkHours(employeeId, targetMonth.getYear(), targetMonth.getMonthValue());
    }

}
