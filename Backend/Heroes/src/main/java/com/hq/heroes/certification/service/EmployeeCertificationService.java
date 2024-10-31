package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;

import java.util.List;

public interface EmployeeCertificationService {

    List<EmployeeCertificationResponseDTO> getMyCertificationByEmployeeId(String employeeId);

    EmployeeCertificationResponseDTO createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO);

    EmployeeCertificationResponseDTO completeCertification(Long registrationId);

    List<EmployeeCertificationResponseDTO> getAllCertification();

}
