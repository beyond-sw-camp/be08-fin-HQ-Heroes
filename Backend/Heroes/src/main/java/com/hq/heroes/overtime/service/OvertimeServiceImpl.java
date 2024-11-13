package com.hq.heroes.overtime.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.overtime.dto.OvertimeDTO;
import com.hq.heroes.overtime.entity.Overtime;
import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import com.hq.heroes.overtime.repository.OvertimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OvertimeServiceImpl implements OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final EmployeeRepository employeeRepository;

    private static final long MAX_OVERTIME_HOURS = 5 * 60;

    public long getRemainingOvertimeHours(String employeeId, YearMonth month) {

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Overtime> approvedOvertimes = overtimeRepository.findByEmployee_EmployeeIdAndOvertimeStartDateBetween(employeeId, startDate, endDate)
                .stream()
                .filter(overtime -> overtime.getOvertimeStatus() == OvertimeStatus.APPROVED)
                .collect(Collectors.toList());

        long totalOvertimeMinutes = approvedOvertimes.stream()
                .mapToLong(overtime -> {
                    LocalDateTime startTime = overtime.getOvertimeStartDate().atTime(overtime.getOvertimeStartTime());
                    LocalDateTime endTime = overtime.getOvertimeEndDate().atTime(overtime.getOvertimeEndTime());
                    return Duration.between(startTime, endTime).toMinutes();
                })
                .sum();

        return Math.max(MAX_OVERTIME_HOURS - totalOvertimeMinutes, 0);
    }

    public long getTotalOvertimeHoursForMonth(String employeeId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Overtime> approvedOvertimes = overtimeRepository.findByEmployee_EmployeeIdAndOvertimeStartDateBetween(employeeId, startDate, endDate)
                .stream()
                .filter(overtime -> overtime.getOvertimeStatus() == OvertimeStatus.APPROVED)
                .collect(Collectors.toList());

        return approvedOvertimes.stream()
                .mapToLong(overtime -> {
                    LocalDateTime startTime = overtime.getOvertimeStartDate().atTime(overtime.getOvertimeStartTime());
                    LocalDateTime endTime = overtime.getOvertimeEndDate().atTime(overtime.getOvertimeEndTime());

                    Duration duration = Duration.between(startTime, endTime);
                    return duration.toMinutes();
                })
                .sum();
    }

    @Override
    public void submitOvertime(OvertimeDTO overtimeDTO) {

        if (overtimeDTO.getOvertimeStartDate() == null || overtimeDTO.getOvertimeEndDate() == null) {
            throw new IllegalArgumentException("연장근로 시작일 또는 종료일이 설정되지 않았습니다.");
        }


        Employee employee = employeeRepository.findByEmployeeId(overtimeDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 신청인을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findByEmployeeName(overtimeDTO.getApproverName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 결재자를 찾을 수 없습니다."));

        if (overtimeDTO.getOvertimeStartDate().isAfter(overtimeDTO.getOvertimeEndDate()) ||
                (overtimeDTO.getOvertimeStartDate().isEqual(overtimeDTO.getOvertimeEndDate()) &&
                        overtimeDTO.getOvertimeStartTime().isAfter(overtimeDTO.getOvertimeEndTime()))) {
            throw new IllegalArgumentException("연장근로 시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }

        Overtime overtime = Overtime.builder()
                .employee(employee)
                .approver(approver)
                .overtimeStartDate(overtimeDTO.getOvertimeStartDate())
                .overtimeStartTime(overtimeDTO.getOvertimeStartTime())
                .overtimeEndDate(overtimeDTO.getOvertimeEndDate())
                .overtimeEndTime(overtimeDTO.getOvertimeEndTime())
                .comment(overtimeDTO.getComment())
                .overtimeStatus(OvertimeStatus.PENDING)
                .build();

        overtimeRepository.save(overtime);
    }

    public void approveOvertime(Long overtimeId) {
        Overtime overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("연장근로를 찾을 수 없습니다."));
        overtime.setOvertimeStatus(OvertimeStatus.APPROVED); // 상태를 승인으로 변경
        overtimeRepository.save(overtime);
    }

    public void rejectOvertime(Long overtimeId) {
        Overtime overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("연장근로를 찾을 수 없습니다."));
        overtime.setOvertimeStatus(OvertimeStatus.REJECTED); // 상태를 반려로 변경
        overtimeRepository.save(overtime);
    }


    @Override
    public List<OvertimeDTO> getAllOvertimes() {
        List<Overtime> overtimes = overtimeRepository.findAll();
        return overtimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OvertimeDTO> getApprovedOvertimesByEmployeeId(String employeeId) {
        List<Overtime> approvedOvertimes = overtimeRepository.findApprovedOvertimesByEmployeeId(employeeId);
        return approvedOvertimes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OvertimeDTO convertToDTO(Overtime overtime) {
        return OvertimeDTO.builder()
                .overtimeId(overtime.getOvertimeId())
                .employeeId(overtime.getEmployee().getEmployeeId())
                .employeeName(overtime.getEmployee().getEmployeeName())
                .approverId(overtime.getApprover() != null ? overtime.getApprover().getEmployeeId() : null)
                .approverName(overtime.getApprover() != null ? overtime.getApprover().getEmployeeName() : null)
                .overtimeStartDate(overtime.getOvertimeStartDate())
                .overtimeStartTime(overtime.getOvertimeStartTime())
                .overtimeEndDate(overtime.getOvertimeEndDate())
                .overtimeEndTime(overtime.getOvertimeEndTime())
                .comment(overtime.getComment())
                .overtimeStatus(overtime.getOvertimeStatus())
                .build();
    }

}
