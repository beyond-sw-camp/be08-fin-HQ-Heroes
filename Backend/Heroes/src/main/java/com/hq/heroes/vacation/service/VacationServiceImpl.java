package com.hq.heroes.vacation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void submitVacation(VacationDTO vacationDTO) {

        System.out.println("============================");
        System.out.println("Employee ID: " + vacationDTO.getEmployeeId());
        System.out.println("Approver Name: " + vacationDTO.getApproverName());
        System.out.println("Start Date: " + vacationDTO.getVacationStartDate());
        System.out.println("End Date: " + vacationDTO.getVacationEndDate());
        System.out.println("============================");

        // null 체크 및 로그 출력
        if (vacationDTO.getVacationStartDate() == null || vacationDTO.getVacationEndDate() == null) {
            throw new IllegalArgumentException("휴가 시작일 또는 종료일이 설정되지 않았습니다.");
        }


        Employee employee = employeeRepository.findByEmployeeId(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 신청인을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findByEmployeeName(vacationDTO.getApproverName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 결재자를 찾을 수 없습니다."));

        // 날짜 비교 전 null 체크
        if (vacationDTO.getVacationStartDate().isAfter(vacationDTO.getVacationEndDate())) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 빠를 수 없습니다.");
        }

        Vacation vacation = Vacation.builder()
                .employee(employee)
                .approver(approver)
                .vacationType(vacationDTO.getVacationType())
                .vacationStartDate(vacationDTO.getVacationStartDate())
                .vacationStartTime(vacationDTO.getVacationStartTime())
                .vacationEndDate(vacationDTO.getVacationEndDate())
                .vacationEndTime(vacationDTO.getVacationEndTime())
                .comment(vacationDTO.getComment())
                .vacationStatus(VacationStatus.PENDING)
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

    // Vacation 엔티티를 VacationDTO로 변환하는 메서드
    private VacationDTO convertToDTO(Vacation vacation) {
        return VacationDTO.builder()
                .vacationId(vacation.getVacationId())
                .employeeId(vacation.getEmployee().getEmployeeId()) // 신청인 ID
                .approverId(vacation.getApprover() != null ? vacation.getApprover().getEmployeeId() : null) // 결재자 ID
                .vacationType(vacation.getVacationType())
                .vacationStartDate(vacation.getVacationStartDate())
                .vacationStartTime(vacation.getVacationStartTime())
                .vacationEndDate(vacation.getVacationEndDate())
                .vacationEndTime(vacation.getVacationEndTime())
                .comment(vacation.getComment())
                .vacationStatus(vacation.getVacationStatus())
                .build();
    }
}
