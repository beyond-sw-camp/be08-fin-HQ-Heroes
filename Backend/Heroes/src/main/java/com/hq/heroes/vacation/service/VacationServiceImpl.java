package com.hq.heroes.vacation.service;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.entity.Attendance;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository; // 신청자 및 결재자 정보 확인을 위한 EmployeeRepository

    @Override
    public void submitVacation(VacationDTO vacationDTO) {

        Employee employee = employeeRepository.findById(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 신청인을 찾을 수 없습니다."));

        Employee approver = null;
        if (vacationDTO.getApproverId() != null) {
            approver = employeeRepository.findById(vacationDTO.getApproverId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 결재자를 찾을 수 없습니다."));
        }

        if (vacationDTO.getVacationStart().isAfter(vacationDTO.getVacationEnd())) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 빠를 수 없습니다.");
        }

        Vacation vacation = Vacation.builder()
                .employee(employee)
                .approver(approver) // 결재자 설정
                .vacationType(vacationDTO.getVacationType())
                .vacationStart(vacationDTO.getVacationStart())
                .vacationEnd(vacationDTO.getVacationEnd())
                .comment(vacationDTO.getComment()) // 사유 설정
                .vacationStatus(VacationStatus.PENDING) // 처음에는 대기 상태로 설정
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        vacationRepository.save(vacation);
    }

    @Override
    public List<VacationDTO> getAllVacations() {
        List<Vacation> vacations = vacationRepository.findAll();
        return vacations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VacationDTO convertToDTO(Vacation vacation) {
        return VacationDTO.builder()
                .vacationId(vacation.getVacationId())
                .employeeId(vacation.getEmployee().getEmployeeId()) // 신청인 ID
                .approverId(vacation.getApprover() != null ? vacation.getApprover().getEmployeeId() : null) // 결재자 ID (null 가능)
                .vacationType(vacation.getVacationType()) // 휴가 종류
                .vacationStart(vacation.getVacationStart()) // 시작 일시
                .vacationEnd(vacation.getVacationEnd()) // 종료 일시
                .comment(vacation.getComment()) // 사유 (댓글)
                .vacationStatus(vacation.getVacationStatus()) // 휴가 상태
                .createdAt(vacation.getCreatedAt()) // 생성 일시
                .updatedAt(vacation.getUpdatedAt()) // 수정 일시
                .build();
    }
}
