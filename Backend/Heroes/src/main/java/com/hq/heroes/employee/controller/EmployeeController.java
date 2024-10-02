package com.hq.heroes.employee.controller;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.dto.TeamDTO;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import com.hq.heroes.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;

    // 모든 사원 조회를 위한 API
    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();

        System.out.println("employees = " + employees);

        // 사원이 없을 경우 메시지와 함께 200 OK 반환
        if (employees.isEmpty()) {
            return ResponseEntity.ok("사원이 없습니다.");
        }

        return ResponseEntity.ok(employees);
    }

    // 부서 조회를 위한 API
    @GetMapping("/departments")
    @Transactional
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

}