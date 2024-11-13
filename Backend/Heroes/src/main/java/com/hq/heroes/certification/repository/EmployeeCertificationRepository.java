package com.hq.heroes.certification.repository;

import com.hq.heroes.certification.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    // 사원 번호로 사원의 자격증 목록 조회
    List<EmployeeCertification> findByEmployee_EmployeeId(String employeeId);
}
