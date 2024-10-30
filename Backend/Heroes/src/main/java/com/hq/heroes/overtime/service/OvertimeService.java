package com.hq.heroes.overtime.service;

import com.hq.heroes.overtime.dto.OvertimeDTO;

import java.time.YearMonth;
import java.util.List;

public interface OvertimeService {

    // 연장 근로 제출
    void submitOvertime(OvertimeDTO overtimeDTO);

    // 연장 근로 목록 조회
    List<OvertimeDTO> getAllOvertimes();

    // 연장 근로 승인
    void approveOvertime(Long overtimeId);

    // 연장 근로 반려
    void rejectOvertime(Long overtimeId);

    // 사원 번호로 승인된 연장 근로 조회
    List<OvertimeDTO> getApprovedOvertimesByEmployeeId(String employeeId);

    // 월별 연장 근로 시간 합계
    long getTotalOvertimeHoursForMonth(String employeeId, YearMonth month);

    // 잔여 연장 근로 시간 조회
    long getRemainingOvertimeHours(String employeeId, YearMonth month);
}
