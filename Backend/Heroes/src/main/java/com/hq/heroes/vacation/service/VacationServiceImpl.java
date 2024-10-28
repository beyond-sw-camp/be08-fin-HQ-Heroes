package com.hq.heroes.vacation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Vacation submitVacation(VacationDTO vacationDTO) {

        System.out.println("============================");
        System.out.println("Employee ID: " + vacationDTO.getEmployeeId());
        System.out.println("Approver Name: " + vacationDTO.getApproverName());
        System.out.println("Applicant Name: " + vacationDTO.getApplicantName());
        System.out.println("Start Date: " + vacationDTO.getVacationStartDate());
        System.out.println("End Date: " + vacationDTO.getVacationEndDate());
        System.out.println("DTO" +  vacationDTO.toString());
        System.out.println("============================");

        // null 체크 및 로그 출력
        if (vacationDTO.getVacationStartDate() == null || vacationDTO.getVacationEndDate() == null) {
            throw new IllegalArgumentException("휴가 시작일 또는 종료일이 설정되지 않았습니다.");
        }


        Employee employee = employeeRepository.findByEmployeeId(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 대리인을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findByEmployeeName(vacationDTO.getApproverName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 결재자를 찾을 수 없습니다."));

        Employee applicant = employeeRepository.findByEmployeeName(vacationDTO.getApplicantName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 신청인을 찾을 수 없습니다."));

        // 날짜 비교 전 null 체크
        if (vacationDTO.getVacationStartDate().isAfter(vacationDTO.getVacationEndDate())) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 빠를 수 없습니다.");
        }

        Vacation vacation = Vacation.builder()
                .employee(employee)
                .approver(approver)
                .applicant(applicant)
                .vacationType(vacationDTO.getVacationType())
                .vacationStartDate(vacationDTO.getVacationStartDate())
                .vacationStartTime(vacationDTO.getVacationStartTime())
                .vacationEndDate(vacationDTO.getVacationEndDate())
                .vacationEndTime(vacationDTO.getVacationEndTime())
                .comment(vacationDTO.getComment())
                .vacationStatus(VacationStatus.PENDING)
                .build();

        return vacationRepository.save(vacation);
    }

    public void approveVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        vacation.setVacationStatus(VacationStatus.APPROVED); // 상태를 승인으로 변경
        vacationRepository.save(vacation);
    }

    public void rejectVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        vacation.setVacationStatus(VacationStatus.REJECTED); // 상태를 반려로 변경
        vacationRepository.save(vacation);
    }


    @Override
    public List<VacationDTO> getAllVacations() {
        List<Vacation> vacations = vacationRepository.findAll();
        return vacations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 사용자의 승인된 휴가 목록 조회
    public List<VacationDTO> getApprovedVacationsByEmployeeId(String employeeId) {
        List<Vacation> approvedVacations = vacationRepository.findApprovedVacationsByEmployeeId(employeeId);
        return approvedVacations.stream()
                .map(this::convertToDTO)  // Vacation 객체를 VacationDTO로 변환하는 메서드
                .collect(Collectors.toList());
    }


    // Vacation 엔티티를 VacationDTO로 변환하는 메서드
    private VacationDTO convertToDTO(Vacation vacation) {
        return VacationDTO.builder()
                .vacationId(vacation.getVacationId())
                .employeeId(vacation.getEmployee().getEmployeeId()) // 신청인 ID
                .employeeName(vacation.getEmployee().getEmployeeName()) // 신청인 이름 추가
                .approverId(vacation.getApprover() != null ? vacation.getApprover().getEmployeeId() : null) // 결재자 ID
                .approverName(vacation.getApprover() != null ? vacation.getApprover().getEmployeeName() : null) // 결재자 이름 추가
                .applicantId(vacation.getApplicant() != null ? vacation.getApplicant().getEmployeeId() : null) // 결재자 ID
                .applicantName(vacation.getApplicant() != null ? vacation.getApplicant().getEmployeeName() : null) // 결재자 이름 추가
                .vacationType(vacation.getVacationType())
                .vacationStartDate(vacation.getVacationStartDate())
                .vacationStartTime(vacation.getVacationStartTime())
                .vacationEndDate(vacation.getVacationEndDate())
                .vacationEndTime(vacation.getVacationEndTime())
                .comment(vacation.getComment())
                .vacationStatus(vacation.getVacationStatus())
                .build();
    }

    @Override
    public List<VacationDTO> getTeamVacations(String employeeId) {
        Long teamId = employeeRepository.findTeamIdByEmployeeId(employeeId);
        if (teamId == null) {
            return Collections.emptyList();
        }

        List<Vacation> vacations = vacationRepository.findVacationsByTeamId(teamId);

        // Vacation 엔티티를 VacationDTO로 변환
        return vacations.stream()
                .map(vacation -> new VacationDTO(
                        vacation.getVacationId(),
                        vacation.getEmployee().getEmployeeId(),
                        vacation.getEmployee().getEmployeeName(),
                        vacation.getApprover() != null ? vacation.getApprover().getEmployeeId() : null,
                        vacation.getApprover() != null ? vacation.getApprover().getEmployeeName() : null,
                        vacation.getEmployee().getTeam() != null ? vacation.getEmployee().getTeam().getTeamName() : null,
                        vacation.getEmployee().getDepartment() != null ? vacation.getEmployee().getDepartment().getDeptName() : null,
                        vacation.getVacationType(),
                        vacation.getVacationStartDate(),
                        vacation.getVacationStartTime(),
                        vacation.getVacationEndDate(),
                        vacation.getVacationEndTime(),
                        vacation.getComment(),
                        vacation.getVacationStatus()
                ))
                .collect(Collectors.toList());
    }

}
