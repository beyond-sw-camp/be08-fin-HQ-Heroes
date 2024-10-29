package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.JobRoleDTO;
import com.hq.heroes.employee.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.JobRoleDTO(j.jobRoleId, j.jobRoleName) FROM JobRole j")
    List<JobRoleDTO> findAllJobRoles(); // 모든 직무를 가져오는 메서드
}
