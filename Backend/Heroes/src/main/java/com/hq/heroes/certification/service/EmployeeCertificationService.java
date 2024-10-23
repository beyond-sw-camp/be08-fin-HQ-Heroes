package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;

import java.util.List;

public interface EmployeeCertificationService {
    //- 테스트
    List<EmployeeCertification> getMyCertificationByEmployeeId(String employeeId);

    //- 테스트
    EmployeeCertification createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO);
}
