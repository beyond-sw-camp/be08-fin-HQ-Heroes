package com.hq.heroes.employee.repository;

import com.hq.heroes.employee.dto.JobDTO;
import com.hq.heroes.employee.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT new com.hq.heroes.employee.dto.JobDTO(j.jobId, j.jobName) FROM Job j")
    List<JobDTO> findAllJobs(); // 모든 직무를 가져오는 메서드
}
