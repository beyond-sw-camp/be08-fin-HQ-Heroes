package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;

import java.util.List;

public interface EmployeeCertificationService {

    // 사원 번호로 자신이 취득한 자격증 조회
    List<EmployeeCertificationResponseDTO> getMyCertificationByEmployeeId(String employeeId);

    // 자격증 등록
    EmployeeCertificationResponseDTO createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO);

    // 자격증 상태 승인으로 변경
    EmployeeCertificationResponseDTO completeCertification(Long registrationId);

    // 모든 자격증 조회
    List<EmployeeCertificationResponseDTO> getAllCertification();

}
