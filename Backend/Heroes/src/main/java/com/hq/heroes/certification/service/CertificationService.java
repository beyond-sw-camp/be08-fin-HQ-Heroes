package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.entity.Certification;

import java.util.List;

public interface CertificationService {
    List<Certification> getCertifications();

    //- 테스트
    Certification getCertificationById(Long certificationId);

    List<Certification> getCertificationListByDeptName(String deptName);

    //- 테스트
    Certification createCertification(CertificationRequestDTO requestDTO);

    //- 테스트
    Certification updateCertification(Long certificationId, CertificationRequestDTO requestDTO);

    //- 테스트
    boolean deleteCertification(Long certificationId);
}
