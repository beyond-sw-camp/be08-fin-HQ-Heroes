package com.hq.heroes.auth.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.dto.EmployeeDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {

    @Query("SELECT new com.hq.heroes.employee.dto.EmployeeDTO(e.employeeId, e.employeeName,"
            + "e.team.teamName, e.team.department.deptName, e.job.jobName, e.position.positionName, "
            + "e.joinDate, e.birthDate, e.phoneNumber, e.roadAddress, e.lotAddress, e.detailedAddress, e.profileImageUrl)" +
            " FROM Employee e")

    List<EmployeeDTO> findAllEmployeesDTO();

    Optional<Employee> findByEmail(String email);

    Boolean existsByEmail(String email);
}
