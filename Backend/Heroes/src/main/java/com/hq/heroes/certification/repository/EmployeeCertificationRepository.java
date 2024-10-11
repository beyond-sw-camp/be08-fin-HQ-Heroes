package com.hq.heroes.certification.repository;

import com.hq.heroes.certification.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {
}
