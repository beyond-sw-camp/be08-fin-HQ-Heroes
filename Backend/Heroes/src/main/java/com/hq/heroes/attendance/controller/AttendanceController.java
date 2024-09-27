package com.hq.heroes.attendance.controller;

import com.hq.heroes.attendance.dto.AttendanceDTO;
import com.hq.heroes.attendance.repository.AttendanceRepository;
import com.hq.heroes.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;

    @GetMapping()
    @Operation(summary = "전체 근태 기록 조회")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceRecords() {
        List<AttendanceDTO> records = attendanceService.getAllAttendances();
        return ResponseEntity.ok(records);
    }
}