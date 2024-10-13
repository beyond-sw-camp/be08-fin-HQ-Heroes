package com.hq.heroes.attendance.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.attendance.entity.enums.AttendanceStatus;
import com.hq.heroes.attendance.repository.AttendanceRepository;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    @Override
    public void checkOut(Employee employee) {
        // 가장 최근 출근 기록 조회
        Attendance attendance = attendanceRepository.findTopByEmployeeAndStatusOrderByCheckInDesc(employee, AttendanceStatus.NORMAL)
                .orElseThrow(() -> new IllegalArgumentException("No active attendance record found"));

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
            // 직원의 가장 최근 출근 상태 확인
            Optional<Attendance> attendance = attendanceRepository.findTopByEmployeeAndStatusOrderByCheckInDesc(employee, AttendanceStatus.NORMAL);
            return attendance.isPresent() && attendance.get().getCheckOut() == null; // 퇴근 기록이 없는 경우 출근 상태로 판단
        }
        return false;
    }
}
