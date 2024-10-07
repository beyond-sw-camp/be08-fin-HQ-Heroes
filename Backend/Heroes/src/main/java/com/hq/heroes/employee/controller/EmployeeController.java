package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.dto.TeamDTO;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import com.hq.heroes.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final JobRepository jobRepository;
    private final PositionRepository positionRepository;

    // 특정 사원 조회를 위한 API
    @GetMapping("/employees/{employee-id}")
    public ResponseEntity<EmployeeDTO> getAllEmployee(
            @Parameter(description = "사원 ID", example = "2024106824") @PathVariable("employee-id") String employeeId
    ) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        System.out.println("employee = " + employee);

        if (employee != null) {
            return ResponseEntity.ok(employee.toResponseDTO());
        } else {
            return ResponseEntity.notFound().build(); // 수정된 부분
        }
    }

    // 모든 사원 조회를 위한 API
    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        // 사원이 없을 경우 메시지와 함께 200 OK 반환
        if (employees.isEmpty()) {
            return ResponseEntity.ok("사원이 없습니다.");
        }

        return ResponseEntity.ok(employees);
    }

    // 부서 조회를 위한 API
    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAllDepartments());
    }

    // 팀 조회를 위한 API
    @GetMapping("/teams")
    public ResponseEntity<?> getTeamsByDepartment(@RequestParam(required = false) Long deptId) {

        List<TeamDTO> teams;

        if (deptId != null) {
            teams = teamRepository.findByDepartment_DeptId(deptId); // 부서 ID로 팀을 조회
        } else {
            teams = teamRepository.findAllTeams(); // 모든 팀 조회
        }

        return ResponseEntity.ok(teams);
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs() {
        return ResponseEntity.ok((jobRepository.findAllJobs()));
    }

    @GetMapping("/positions")
    public ResponseEntity<?> getPositions() {
        return ResponseEntity.ok((positionRepository.findAllPositions()));
    }

}