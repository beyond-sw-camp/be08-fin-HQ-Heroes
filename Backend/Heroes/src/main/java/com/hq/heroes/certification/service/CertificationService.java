package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;

import java.util.List;

public interface CertificationService {

    List<CertificationResponseDTO> getCertifications();

    CertificationResponseDTO getCertificationById(Long certificationId);

    List<CertificationResponseDTO> getCertificationListByDeptName(String deptName);

    CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO);

    CertificationResponseDTO updateCertification(Long certificationId, CertificationRequestDTO requestDTO);

    boolean deleteCertification(Long certificationId);

}
