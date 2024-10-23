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
        특정 사원 조회  - 테스트 코드 필요
     */
    Employee getEmployeeById(String id);

    /*
        사원 정보 수정 - 테스트
     */
    Employee updateEmployee(EmployeeDTO employeeDTO);

    /*
        관리자 - 사원 정보 수정 - 테스트
     */
    Employee adminUpdateEmployee(EmployeeDTO employeeDTO);

    //- 테스트
    String setTempPassword(String to, String authCode);

    //- 테스트
    String updatePassword(String email, String tempPW);

    // 비밀번호 변경 로직 - 테스트
    boolean updatePassword(String employeeId, String currentPassword, String newPassword);
}
