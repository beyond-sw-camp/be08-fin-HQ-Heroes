package com.hq.heroes.employee.service;


import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    /*
        전체 사원 조회
     */
    List<EmployeeDTO> getAllEmployees();

    Employee getEmployeeById(String id);

    /*
        사원 정보 수정
     */
    Employee updateEmployee(EmployeeDTO employeeDTO);

    /*
        관리자 - 사원 정보 수정
     */
    Employee adminUpdateEmployee(EmployeeDTO employeeDTO);

    String setTempPassword(String to, String authCode);

    String updatePassword(String email, String tempPW);
}
