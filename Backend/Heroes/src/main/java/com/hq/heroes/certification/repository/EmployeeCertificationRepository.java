package com.hq.heroes.certification.repository;

import com.hq.heroes.certification.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {
    List<EmployeeCertification> findByEmployee_EmployeeId(String employeeId);
}
