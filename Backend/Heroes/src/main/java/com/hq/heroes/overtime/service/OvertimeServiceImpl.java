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

    public long getTotalOvertimeHoursForMonth(String employeeId, YearMonth month) {
        LocalDate startDate = month.atDay(1);  // 해당 월의 첫날
        LocalDate endDate = month.atEndOfMonth();  // 해당 월의 마지막 날

        List<Overtime> overtimes = overtimeRepository.findByEmployee_EmployeeIdAndOvertimeStartDateBetween(employeeId, startDate, endDate);

        // 두 시간의 차이를 계산하고 합산
        return overtimes.stream()
                .mapToLong(overtime -> {
                    LocalDateTime startTime = overtime.getOvertimeStartDate().atTime(overtime.getOvertimeStartTime());
                    LocalDateTime endTime = overtime.getOvertimeEndDate().atTime(overtime.getOvertimeEndTime());

                    // 시작 시간과 종료 시간 사이의 차이를 계산
                    Duration duration = Duration.between(startTime, endTime);
                    return duration.toMinutes(); // 분 단위로 반환
                })
                .sum(); // 모든 연장 근로 시간을 합산
    }

    @Override
    public void submitOvertime(OvertimeDTO overtimeDTO) {

        System.out.println("============================");
        System.out.println("Employee ID: " + overtimeDTO.getEmployeeId());
        System.out.println("Approver Name: " + overtimeDTO.getApproverName());
        System.out.println("Start Date: " + overtimeDTO.getOvertimeStartDate());
        System.out.println("End Date: " + overtimeDTO.getOvertimeEndDate());
        System.out.println("============================");

        // null 체크 및 로그 출력
        if (overtimeDTO.getOvertimeStartDate() == null || overtimeDTO.getOvertimeEndDate() == null) {
            throw new IllegalArgumentException("휴가 시작일 또는 종료일이 설정되지 않았습니다.");
        }


        Employee employee = employeeRepository.findByEmployeeId(overtimeDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 신청인을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findByEmployeeName(overtimeDTO.getApproverName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 결재자를 찾을 수 없습니다."));

        // 날짜 비교 전 null 체크
        if (overtimeDTO.getOvertimeStartDate().isAfter(overtimeDTO.getOvertimeEndDate())) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 빠를 수 없습니다.");
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
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        overtime.setOvertimeStatus(OvertimeStatus.APPROVED); // 상태를 승인으로 변경
        overtimeRepository.save(overtime);
    }

    public void rejectOvertime(Long overtimeId) {
        Overtime overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
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

    // 특정 사용자의 승인된 연장 근로 목록 조회
    public List<OvertimeDTO> getApprovedOvertimesByEmployeeId(String employeeId) {
        List<Overtime> approvedOvertimes = overtimeRepository.findApprovedOvertimesByEmployeeId(employeeId);
        return approvedOvertimes.stream()
                .map(this::convertToDTO)  // Vacation 객체를 VacationDTO로 변환하는 메서드
                .collect(Collectors.toList());
    }


    // Overtime 엔티티를 OvertiemDTO로 변환하는 메서드
    private OvertimeDTO convertToDTO(Overtime overtime) {
        return OvertimeDTO.builder()
                .overtimeId(overtime.getOvertimeId())
                .employeeId(overtime.getEmployee().getEmployeeId()) // 신청인 ID
                .employeeName(overtime.getEmployee().getEmployeeName()) // 신청인 이름 추가
                .approverId(overtime.getApprover() != null ? overtime.getApprover().getEmployeeId() : null) // 결재자 ID
                .approverName(overtime.getApprover() != null ? overtime.getApprover().getEmployeeName() : null) // 결재자 이름 추가
                .overtimeStartDate(overtime.getOvertimeStartDate())
                .overtimeStartTime(overtime.getOvertimeStartTime())
                .overtimeEndDate(overtime.getOvertimeEndDate())
                .overtimeEndTime(overtime.getOvertimeEndTime())
                .comment(overtime.getComment())
                .overtimeStatus(overtime.getOvertimeStatus())
                .build();
    }

}
