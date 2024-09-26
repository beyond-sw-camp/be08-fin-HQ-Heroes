package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.DepartmentDTO;
import com.hq.heroes.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.DepartmentDTO(d.deptName) FROM Department d")
    List<DepartmentDTO> findAllDepartments();

}
