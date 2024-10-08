package com.hq.heroes.vacation.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.service.EmployeeService;
import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.service.VacationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/submit")
    @Operation(summary = "휴가 신청")
    public ResponseEntity<String> submitVacation(@RequestBody VacationDTO vacationDTO) {
        try {
            vacationService.submitVacation(vacationDTO);
            return ResponseEntity.ok("휴가 신청이 성공적으로 제출되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("휴가 신청 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "휴가 신청 목록 조회")
    public ResponseEntity<List<VacationDTO>> getVacationList() {
        List<VacationDTO> lists = vacationService.getAllVacations();
        return ResponseEntity.ok(lists);
    }

    // 로그인된 사용자 정보 가져오기
    @GetMapping("/username")
    @Operation(summary = "로그인된 사용자 이름 조회")
    public ResponseEntity<String> getLoggedInUser() {
        // SecurityContextHolder를 사용하여 현재 로그인한 사용자 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";

        if (principal instanceof UserDetails) {
            // UserDetails에서 사용자 ID (employeeId) 가져오기
            String employeeId = ((UserDetails) principal).getUsername(); // ID를 사용자 ID로 설정
            // employeeId를 통해 Employee 엔티티에서 사용자 이름 가져오기
            Employee employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            username = employee.getEmployeeName(); // 이름을 가져옴
        } else {
            username = principal.toString();
        }

        return ResponseEntity.ok(username);
    }
}
