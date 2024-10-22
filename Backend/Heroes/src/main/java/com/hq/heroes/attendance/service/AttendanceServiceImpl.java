package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import com.hq.heroes.attendance.repository.AttendanceRepository;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // 전체 근태 기록 조회
    @Override
    public List<AttendanceDTO> getAllAttendances() {
        List<Attendance> attendances = attendanceRepository.findAll();
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .attendanceId(attendance.getAttendanceId())
                .employeeId(attendance.getEmployee().getEmployeeId())
                .employeeName(attendance.getEmployee().getEmployeeName())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .status(attendance.getStatus())
                .build();
    }

    private Attendance convertToEntity(AttendanceDTO attendanceDTO) {
        return Attendance.builder()
                .attendanceId(attendanceDTO.getAttendanceId())
                .checkIn(attendanceDTO.getCheckIn())
                .checkOut(attendanceDTO.getCheckOut())
                .status(attendanceDTO.getStatus())
                .build();
    }

    // 출근 처리
    @Override
    public Attendance checkIn(Employee employee) {
        Attendance attendance = Attendance.builder()
                .employee(employee)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.NORMAL)
                .build();

        attendanceRepository.save(attendance);
        return attendance;
    }

    // 퇴근 처리
    @Transactional
    @Override
    public void checkOut(Employee employee) {

        // 가장 최근 출근 기록 조회 (상태가 NORMAL이고 퇴근 시간이 NULL인 기록)
        Attendance attendance = attendanceRepository.findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(employee, AttendanceStatus.NORMAL)
                .orElseThrow(() -> {
                    log.error("출근 기록 조회 실패 - employeeId: {}", employee.getEmployeeId());
                    return new IllegalArgumentException("No active attendance record found");
                });

        attendance.setCheckOut(LocalDateTime.now());  // 퇴근 시간 설정
        attendance.setStatus(AttendanceStatus.LEAVE_WORK);  // 상태를 퇴근으로 변경

        attendanceRepository.save(attendance);  // 저장
    }

    @Override
    public boolean isAlreadyCheckedIn(String employeeId) {
        // employeeId로 직원 정보 조회
        Optional<Employee> employeeOptional = employeeRepository.findByEmployeeId(employeeId);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            // 직원의 가장 최근 출근 상태 확인 (상태가 NORMAL이고 퇴근 시간이 NULL인 경우만 확인)
            Optional<Attendance> attendanceOptional = attendanceRepository.findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(employee, AttendanceStatus.NORMAL);

            if (attendanceOptional.isPresent()) {
                Attendance attendance = attendanceOptional.get();
                LocalDateTime now = LocalDateTime.now();

                // 오후 6시 10분 이후 퇴근 시간이 없으면 상태를 ON_HOLD로 변경
                if (now.isAfter(LocalDate.now().atTime(18, 10)) && attendance.getCheckOut() == null) {
                    attendance.setStatus(AttendanceStatus.ON_HOLD);
                    attendanceRepository.save(attendance);  // 상태 업데이트
                }

                // 퇴근 기록이 없는 경우 출근 상태로 판단
                return attendance.getCheckOut() == null;
            }
        }
        return false;
    }

    public List<AttendanceDTO> getAttendancesByEmployeeId(String employeeId) {
        List<Attendance> attendances = attendanceRepository.findByEmployeeEmployeeId(employeeId);
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO getLatestAttendance(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));
        Attendance attendance = attendanceRepository.findTopByEmployeeAndStatusAndCheckOutIsNullOrderByCheckInDesc(employee, AttendanceStatus.NORMAL)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다."));

        // DTO로 변환하여 반환
        return new AttendanceDTO(
                attendance.getAttendanceId(),
                employee.getEmployeeName(),
                employee.getEmployeeId(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getStatus()
        );
    }

    @Override
    public List<AttendanceDTO> findByEmployee_IdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployee_EmployeeIdAndCheckInBetween(employeeId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 총 근무 시간 계산
    public int calculateTotalWorkHours(String employeeId, YearMonth targetMonth) {
        Integer totalHours = attendanceRepository.findTotalWorkHours(
                employeeId,
                targetMonth.getYear(),
                targetMonth.getMonthValue()
        );
        return totalHours != null ? totalHours : 0;  // null일 경우 기본값 0 반환
    }

    public AttendanceDTO getLatestAttendanceRecord(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원 정보를 찾을 수 없습니다."));

        Attendance latestAttendance = attendanceRepository.findTopByEmployeeOrderByCheckInDesc(employee)
                .orElseThrow(() -> new IllegalArgumentException("출퇴근 기록이 없습니다."));

        return convertToDTO(latestAttendance);
    }

}
