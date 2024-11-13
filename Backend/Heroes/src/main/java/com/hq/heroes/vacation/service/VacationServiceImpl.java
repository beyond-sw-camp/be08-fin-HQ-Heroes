package com.hq.heroes.vacation.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import com.hq.heroes.vacation.repository.VacationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Long getPendingVacationQuarters(String employeeId) {
        List<Vacation> pendingVacations = vacationRepository.findByEmployeeEmployeeIdAndVacationStatus(employeeId, VacationStatus.PENDING);
        return pendingVacations.stream()
                .mapToLong(this::calculateRequestedQuarters)
                .sum();
    }

    private int calculateRequestedQuarters(Vacation vacation) {
        LocalTime[] quarterTimes = {
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0)
        };

        int requestedQuarters = 0;

        if (vacation.getVacationStartDate().equals(vacation.getVacationEndDate())) {

            for (int i = 0; i < quarterTimes.length; i++) {
                if (vacation.getVacationStartTime().isBefore(quarterTimes[i].plus(2, ChronoUnit.HOURS)) &&
                        vacation.getVacationEndTime().isAfter(quarterTimes[i])) {
                    requestedQuarters++;
                }
            }
        } else {
            long totalDays = ChronoUnit.DAYS.between(vacation.getVacationStartDate(), vacation.getVacationEndDate()) + 1;

            for (LocalTime quarterTime : quarterTimes) {
                if (vacation.getVacationStartTime().isBefore(quarterTime.plus(2, ChronoUnit.HOURS))) {
                    requestedQuarters++;
                }
            }

            for (LocalTime quarterTime : quarterTimes) {
                if (vacation.getVacationEndTime().isAfter(quarterTime)) {
                    requestedQuarters++;
                }
            }

            requestedQuarters += (totalDays - 2) * 4;
        }

        System.out.println("계산된 쿼터 수: " + requestedQuarters);
        return requestedQuarters;
    }

    @Override
    public Vacation submitVacation(VacationDTO vacationDTO) {

        if (vacationDTO.getVacationStartDate() == null || vacationDTO.getVacationEndDate() == null) {
            throw new IllegalArgumentException("휴가 시작일 또는 종료일이 설정되지 않았습니다.");
        }


        Employee employee = employeeRepository.findByEmployeeId(vacationDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 대리인을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findByEmployeeName(vacationDTO.getApproverName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 결재자를 찾을 수 없습니다."));

        Employee applicant = employeeRepository.findByEmployeeName(vacationDTO.getApplicantName())
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 신청인을 찾을 수 없습니다."));

        if (vacationDTO.getVacationStartDate().isAfter(vacationDTO.getVacationEndDate())) {
            throw new IllegalArgumentException("휴가 시작일은 종료일보다 빠를 수 없습니다.");
        }

        boolean isOverlap = vacationRepository.existsByEmployeeAndVacationStartDateLessThanEqualAndVacationEndDateGreaterThanEqualAndVacationStatusNot(
                employee,
                vacationDTO.getVacationEndDate(),
                vacationDTO.getVacationStartDate(),
                VacationStatus.CANCEL_APPROVED
        );

        if (isOverlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "선택한 기간에 이미 휴가가 존재합니다.");
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

        Vacation vacation = vacationRepository.findById(vacationDTO.getVacationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 휴가 요청을 찾을 수 없습니다."));

        vacation.setVacationStatus(VacationStatus.CANCEL);

        return vacationRepository.save(vacation);
    }

    public void approveCancelVacation(Long vacationId) {

        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

        vacation.setVacationStatus(VacationStatus.CANCEL_APPROVED);

        Employee employee = vacation.getEmployee();
        long refundAmount;

        if (vacation.getVacationType() == VacationType.DAY_OFF) {

            long days = calculateWeekdaysBetween(vacation.getVacationStartDate(), vacation.getVacationEndDate());
            refundAmount = days * 4;
        } else if (vacation.getVacationType() == VacationType.HALF_DAY_OFF ||
                vacation.getVacationType() == VacationType.SICK_LEAVE ||
                vacation.getVacationType() == VacationType.EVENT_LEAVE) {

            refundAmount = calculateQuarterDeduction(vacation.getVacationStartTime(), vacation.getVacationEndTime());
        } else {
            throw new RuntimeException("Invalid vacation type");
        }

        employee.setAnnualLeave(employee.getAnnualLeave() + refundAmount);

        employeeRepository.save(employee);
        vacationRepository.save(vacation);
    }


    public void rejectCancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        vacation.setVacationStatus(VacationStatus.CANCEL_REJECTED);
        vacationRepository.save(vacation);
    }

    public void approveVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("Invalid vacation ID"));

        vacation.setVacationStatus(VacationStatus.APPROVED);

        Employee employee = vacation.getEmployee();
        long deductionAmount;

        if (vacation.getVacationType() == VacationType.DAY_OFF) {

            long days = calculateWeekdaysBetween(vacation.getVacationStartDate(), vacation.getVacationEndDate());
            deductionAmount = days * 4;
        } else if (vacation.getVacationType() == VacationType.HALF_DAY_OFF ||
                vacation.getVacationType() == VacationType.SICK_LEAVE ||
                vacation.getVacationType() == VacationType.EVENT_LEAVE) {

            deductionAmount = calculateQuarterDeduction(vacation.getVacationStartTime(), vacation.getVacationEndTime());
        } else {
            throw new RuntimeException("Invalid vacation type");
        }

        if (employee.getAnnualLeave() >= deductionAmount) {
            employee.setAnnualLeave(employee.getAnnualLeave() - deductionAmount);
        } else {
            throw new RuntimeException("연차가 부족합니다.");
        }

        employeeRepository.save(employee);
        vacationRepository.save(vacation);
    }

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

        List<String> timeSlots = Arrays.asList("09:00", "11:00", "13:00", "16:00", "18:00");

        String startStr = startTime.toString().substring(0, 5);
        String endStr = endTime.toString().substring(0, 5);

        int startIdx = timeSlots.indexOf(startStr);
        int endIdx = timeSlots.indexOf(endStr);

        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx) {
            throw new RuntimeException("Invalid time range for half-day vacation");
        }

        return endIdx - startIdx;
    }

    public void rejectVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new RuntimeException("휴가를 찾을 수 없습니다."));
        vacation.setVacationStatus(VacationStatus.REJECTED);
        vacationRepository.save(vacation);
    }

    @Override
    public List<VacationDTO> getAllVacations() {
        List<Vacation> vacations = vacationRepository.findAll();
        return vacations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VacationDTO> getApprovedVacationsByEmployeeId(String employeeId) {
        List<Vacation> approvedVacations = vacationRepository.findApprovedVacationsByEmployeeId(employeeId);
        return approvedVacations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VacationDTO convertToDTO(Vacation vacation) {
        return VacationDTO.builder()
                .vacationId(vacation.getVacationId())
                .employeeId(vacation.getEmployee().getEmployeeId())
                .employeeName(vacation.getEmployee().getEmployeeName())
                .approverId(vacation.getApprover() != null ? vacation.getApprover().getEmployeeId() : null)
                .approverName(vacation.getApprover() != null ? vacation.getApprover().getEmployeeName() : null)
                .applicantId(vacation.getApplicant() != null ? vacation.getApplicant().getEmployeeId() : null)
                .applicantName(vacation.getApplicant() != null ? vacation.getApplicant().getEmployeeName() : null)
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

        return vacations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 휴가 요청을 찾을 수 없습니다."));

        if (vacation.getVacationStatus() != VacationStatus.PENDING) {
            throw new IllegalStateException("대기 중 상태가 아닌 휴가는 삭제할 수 없습니다.");
        }

        vacationRepository.delete(vacation);
    }
}
