package com.hq.heroes.certification.repository;

import com.hq.heroes.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByCertificationId(Long certificationId);
}
