package com.hq.heroes.vacation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.service.NotificationService;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import com.hq.heroes.vacation.repository.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Vacation submitVacation(VacationDTO vacationDTO) {

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

    @Override
    public Vacation cancelVacation(VacationDTO vacationDTO) {
        // 이미 불러온 vacationDTO의 정보를 그대로 사용하여 엔티티 수정
        Vacation vacation = vacationRepository.findById(vacationDTO.getVacationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 휴가 요청을 찾을 수 없습니다."));

        // 취소 요청 상태로 업데이트하고 코멘트를 설정
        vacation.setVacationStatus(VacationStatus.CANCEL);

        // vacation 엔티티를 저장하여 업데이트된 정보를 반환
        return vacationRepository.save(vacation);
    }

    public void approveCancelVacation(Long vacationId) {
        // 휴가 조회
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

        // 휴가 상태를 CANCELED로 업데이트
        vacation.setVacationStatus(VacationStatus.CANCELED);

        Employee employee = vacation.getEmployee();
        long refundAmount;

        // 휴가 종류에 따라 복구 일수 설정
        if (vacation.getVacationType() == VacationType.DAY_OFF) {
            // 시작일과 종료일 사이의 평일 수 계산 (주말 제외)
            long days = calculateWeekdaysBetween(vacation.getVacationStartDate(), vacation.getVacationEndDate());
            refundAmount = days * 4; // 평일에 따른 복구
        } else if (vacation.getVacationType() == VacationType.HALF_DAY_OFF ||
                vacation.getVacationType() == VacationType.SICK_LEAVE ||
                vacation.getVacationType() == VacationType.EVENT_LEAVE) {
            // 반차, 병가, 경조는 쿼터 단위로 복구
            refundAmount = calculateQuarterDeduction(vacation.getVacationStartTime(), vacation.getVacationEndTime());
        } else {
            throw new RuntimeException("Invalid vacation type");
        }

        // 연차 복구
        employee.setAnnualLeave(employee.getAnnualLeave() + refundAmount);

        // 변경 사항 저장
        employeeRepository.save(employee);
        vacationRepository.save(vacation);
    }


    public void rejectCancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        vacation.setVacationStatus(VacationStatus.REJECTED); // 상태를 반려로 변경
        vacationRepository.save(vacation);
    }


    public void approveVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

        vacation.setVacationStatus(VacationStatus.APPROVED);

        Employee employee = vacation.getEmployee();
        long deductionAmount;

        // 휴가 종류에 따른 차감 일수 설정
        if (vacation.getVacationType() == VacationType.DAY_OFF) {
            // 시작일과 종료일의 차이를 계산하여 일수로 차감 (하루당 4 차감)
            long days = calculateWeekdaysBetween(vacation.getVacationStartDate(), vacation.getVacationEndDate());
            deductionAmount = days * 4; // 일수에 따른 차감
        } else if (vacation.getVacationType() == VacationType.HALF_DAY_OFF ||
                vacation.getVacationType() == VacationType.SICK_LEAVE ||
                vacation.getVacationType() == VacationType.EVENT_LEAVE) {
            // 반차, 병가, 경조는 쿼터 단위로 차감
            deductionAmount = calculateQuarterDeduction(vacation.getVacationStartTime(), vacation.getVacationEndTime());
        } else {
            throw new RuntimeException("Invalid vacation type");
        }

        // 연차가 충분한지 확인하고 차감 적용
        if (employee.getAnnualLeave() >= deductionAmount) {
            employee.setAnnualLeave(employee.getAnnualLeave() - deductionAmount);
        } else {
            throw new RuntimeException("연차가 부족합니다.");
        }

        // 변경 사항 저장
        employeeRepository.save(employee);
        vacationRepository.save(vacation);
    }

    // 주말 제외한 평일 계산 함수
    private long calculateWeekdaysBetween(LocalDate startDate, LocalDate endDate) {
        long weekdays = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                weekdays++;
            }
            date = date.plusDays(1);
        }
        return weekdays;
    }

    private long calculateQuarterDeduction(LocalTime startTime, LocalTime endTime) {
        // 4개의 쿼터 시간대 리스트로 설정
        List<String> timeSlots = Arrays.asList("09:00", "11:00", "13:00", "16:00", "18:00");

        // LocalTime을 문자열로 변환
        String startStr = startTime.toString().substring(0, 5);
        String endStr = endTime.toString().substring(0, 5);

        int startIdx = timeSlots.indexOf(startStr);
        int endIdx = timeSlots.indexOf(endStr);

        // 인덱스가 유효한지 확인 후 차감 일수 계산
        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx) {
            throw new RuntimeException("Invalid time range for half-day vacation");
        }

        // 선택된 쿼터 수만큼 1씩 차감
        return endIdx - startIdx;
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
