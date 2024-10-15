package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;

import java.util.List;

public interface EmployeeCertificationService {
    List<EmployeeCertification> getMyCertificationByEmployeeId(String employeeId);

    EmployeeCertification createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO);
}
