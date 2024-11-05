package com.hq.heroes.vacation.service;

import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;

import java.util.List;

public interface VacationService {

    // 휴가 제출
    Vacation submitVacation(VacationDTO vacationDTO);

    // 휴가 취소
    Vacation cancelVacation(VacationDTO vacationDTO);

    // 모든 휴가 조회
    List<VacationDTO> getAllVacations();

    // 휴가 승인
    void approveVacation(Long vacationId);

    // 휴가 반려
    void rejectVacation(Long vacationId);

    // 휴가 취소 승인
    void approveCancelVacation(Long vacationId);

    // 휴가 취소 반려
    void rejectCancelVacation(Long vacationId);

    // 사원 번호로 승인된 휴가 조회
    List<VacationDTO> getApprovedVacationsByEmployeeId(String employeeId);

    // 같은 팀 휴가 목록 조회
    List<VacationDTO> getTeamVacations(String employeeId);

    // 대기 중인 휴가 삭제
    void deleteVacation(Long vacationId);

    // 대기중인 휴가 쿼더수 계산
    Long getPendingVacationQuarters(String employeeId);

}
