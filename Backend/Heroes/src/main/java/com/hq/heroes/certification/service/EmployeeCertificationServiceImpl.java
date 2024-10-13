package com.hq.heroes.certification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.repository.EmployeeCertificationRepository;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationServiceImpl implements EmployeeCertificationService {
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeCertification> getEmployeeCertificationByEmployeeId (String employeeId) {
        return employeeCertificationRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    public EmployeeCertification createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(String.valueOf(requestDTO.getEmployeeId()))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        EmployeeCertification employeeCertification = EmployeeCertification.builder()
                .certificationName(requestDTO.getCertificationName())
                .institution(requestDTO.getInstitution())
                .acquisitionDate(requestDTO.getAcquisitionDate())
                .employee(employee)
                .build();

        return employeeCertificationRepository.save(employeeCertification);

    }
}
