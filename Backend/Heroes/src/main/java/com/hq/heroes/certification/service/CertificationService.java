package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;

import java.util.List;

public interface CertificationService {

    // 자격증 조회
    List<CertificationResponseDTO> getCertifications();

    // 자격증 번호로 자격증 조회
    CertificationResponseDTO getCertificationById(Long certificationId);

    // 부서 이름으로 자격증 목록 조회
    List<CertificationResponseDTO> getCertificationListByDeptName(String deptName);

    // 자격증 생성
    CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO);

    // 자격증 수정
    CertificationResponseDTO updateCertification(Long certificationId, CertificationRequestDTO requestDTO);

    // 자격증 삭제
    boolean deleteCertification(Long certificationId);

}
