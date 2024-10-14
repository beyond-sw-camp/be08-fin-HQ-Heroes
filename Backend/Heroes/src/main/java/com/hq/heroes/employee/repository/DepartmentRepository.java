package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.DepartmentDTO;
import com.hq.heroes.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.DepartmentDTO(d.deptId, d.deptName) FROM Department d")
    List<DepartmentDTO> findAllDepartments(); // 모든 부서를 가져오는 메서드

    Optional<Department> findByDeptName(String deptName);
}
