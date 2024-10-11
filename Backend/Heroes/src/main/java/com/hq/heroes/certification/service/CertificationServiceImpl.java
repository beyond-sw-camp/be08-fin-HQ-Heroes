package com.hq.heroes.certification.service;

import com.hq.heroes.certification.dto.CertificationRequestDTO;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.repository.CertificationRepository;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {
    private final CertificationRepository certificationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Certification> getCertifications() {
            return certificationRepository.findAll();
    }

    @Override
    public Certification getCertificationById(Long certificationId) {
        return certificationRepository.findById(certificationId).orElse(null);
    }

    @Override
    @Transactional
    public Certification createCertification(CertificationRequestDTO requestDTO) {
        Department department = departmentRepository.findById(requestDTO.getDeptId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        Certification certification = Certification.builder()
                .certificationName(requestDTO.getCertificationName())
                .institution(requestDTO.getInstitution())
                .benefit(requestDTO.getBenefit())
                .applicationStartDate(requestDTO.getApplicationStartDate())
                .applicationEndDate(requestDTO.getApplicationEndDate())
                .examDate(requestDTO.getExamDate())
                .department(department)
                .build();

        return certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public Certification updateCertification(Long certificationId, CertificationRequestDTO requestDTO) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + certificationId));

        certification.setCertificationName(requestDTO.getCertificationName());
        certification.setInstitution(requestDTO.getInstitution());
        certification.setBenefit(requestDTO.getBenefit());
        certification.setApplicationStartDate(requestDTO.getApplicationStartDate());
        certification.setApplicationEndDate(requestDTO.getApplicationEndDate());
        certification.setExamDate(requestDTO.getExamDate());

        return certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public boolean deleteCertification(Long certificationId) {
        if (certificationRepository.existsById(certificationId)) {
            certificationRepository.deleteById(certificationId);
            return true;
        }
        return false;
    }

}

