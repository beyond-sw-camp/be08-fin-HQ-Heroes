package com.hq.heroes.certification.service;

import com.hq.heroes.certification.entity.Certification;

import java.util.List;

public interface CertificationService {
    List<Certification> getCertifications(Long certificationId);
}
