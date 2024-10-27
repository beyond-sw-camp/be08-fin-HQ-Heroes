package com.hq.heroes.certification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
import com.hq.heroes.certification.repository.EmployeeCertificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeCertificationServiceImpl implements EmployeeCertificationService {
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeCertification> getMyCertificationByEmployeeId(String employeeId) {
        return employeeCertificationRepository.findByEmployee_EmployeeId(employeeId);
    }

    @Override
    @Transactional
    public EmployeeCertification createEmployeeCertification(EmployeeCertificationRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        EmployeeCertification employeeCertification = EmployeeCertification.builder()
                .certificationName(requestDTO.getCertificationName())
                .institution(requestDTO.getInstitution())
                .acquisitionDate(requestDTO.getAcquisitionDate())
                .employeeCertificationStatus(EmployeeCertificationStatus.DENIED)
                .employee(employee)
                .build();

        return employeeCertificationRepository.save(employeeCertification);
    }

    public void completeCertification(Long registrationId) {
        EmployeeCertification employeeCertification = employeeCertificationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("등록한 자격증을 찾을 수 없습니다."));

        employeeCertification.setEmployeeCertificationStatus(EmployeeCertificationStatus.APPROVE); // 상태를 승인으로 변경
        employeeCertificationRepository.save(employeeCertification);
    }

    @Override
    public List<EmployeeCertificationResponseDTO> getAllCertification() {
        List<EmployeeCertification> employeeCertifications = employeeCertificationRepository.findAll();
        return employeeCertifications.stream()
                .map(EmployeeCertification::toECResponseDTO)
                .collect(Collectors.toList());
    }

}
