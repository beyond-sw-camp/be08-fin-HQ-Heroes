package com.hq.heroes.auth.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {

    @Query("SELECT e FROM Employee e " +
            "JOIN FETCH e.team t " + // Team과의 Fetch Join
            "JOIN FETCH t.department d " + // Department와의 Fetch Join
            "JOIN FETCH e.job j " + // JobRole과의 Fetch Join
            "JOIN FETCH e.position p " + // Position과의 Fetch Join
            "WHERE e.employeeId = :employeeId")
    Optional<Employee> findByIdWithDetails(@Param("employeeId") String employeeId);

//    @Query("SELECT new com.hq.heroes.employee.dto.EmployeeDTO(e.employeeId, e.employeeName, " +
//            "e.team.teamId, e.team.teamName, e.team.department.deptId, e.team.department.deptName, " +
//            "e.email, e.job.jobRoleId, e.job.jobRoleName, e.position.positionId, e.position.positionName, " +
//            "e.joinDate, e.annualLeave, e.status, e.birthDate, e.phoneNumber, e.roadAddress, e.lotAddress, e.detailedAddress, e.profileImageUrl) " +
//            "FROM Employee e")
//    List<EmployeeDTO> findAllEmployeesDTO();

    @Query("SELECT new com.hq.heroes.employee.dto.EmployeeDTO(e.employeeId, e.employeeName, " +
            "e.team.teamId, e.team.teamName, e.team.department.deptId, e.team.department.deptName, " +
            "e.email, e.job.jobRoleId, e.job.jobRoleName, e.position.positionId, e.position.positionName, " +
            "e.joinDate, e.annualLeave, e.status, e.birthDate, e.phoneNumber, e.roadAddress, e.lotAddress, e.detailedAddress, e.profileImageUrl) " +
            "FROM Employee e WHERE e.team.department.deptId = :departmentId")
    List<EmployeeDTO> findEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    Boolean existsByEmail(String email);

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmployeeName(String name);

    Optional<Employee> findByEmail(String email);

    // employeeId를 통해 해당 직원의 teamId를 조회
    @Query("SELECT e.team.teamId FROM Employee e WHERE e.employeeId = :employeeId")
    Long findTeamIdByEmployeeId(@Param("employeeId") String employeeId);

    // 근태 기록이 있는 사원 조회
    @Query("SELECT DISTINCT e FROM Employee e " +
            "JOIN FETCH e.position p " + // Position과의 관계를 Fetch Join
            "JOIN FETCH Attendance a ON e.employeeId = a.employee.employeeId " +
            "WHERE a.checkIn IS NOT NULL OR a.checkOut IS NOT NULL")
    Page<Employee> findEmployeesWithAttendance(Pageable pageable);

}
