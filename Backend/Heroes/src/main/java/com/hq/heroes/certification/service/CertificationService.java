package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.entity.Certification;

import java.util.List;

public interface CertificationService {
    List<Certification> getCertifications();
    
    Certification getCertificationById(Long certificationId);

    Certification createCertification(CertificationRequestDTO requestDTO);

    Certification updateCertification(Long certificationId, CertificationRequestDTO requestDTO);

    boolean deleteCertification(Long certificationId);
}
