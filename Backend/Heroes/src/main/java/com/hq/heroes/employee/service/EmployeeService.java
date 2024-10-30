package com.hq.heroes.employee.service;


import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    /*
        전체 사원 조회
     */
    List<EmployeeDTO> getAllEmployees();

    /*
    부서 별 사원 조회
    */
    List<EmployeeDTO> getAllEmployeesByDepartment(Long deptId);

    /*
        특정 사원 조회
     */
    EmployeeDTO getEmployeeById(String id);

    /*
        사원 정보 수정
     */
    Employee updateEmployee(EmployeeDTO employeeDTO);

    /*
        관리자 - 사원 정보 수정
     */
    Employee adminUpdateEmployee(EmployeeDTO employeeDTO);

    /*
        임시 비밀번호 발급
     */
    String setTempPassword(String to, String authCode);

    /*
        비밀번호 변경
     */
    boolean updatePassword(String employeeId, String currentPassword, String newPassword);
}
