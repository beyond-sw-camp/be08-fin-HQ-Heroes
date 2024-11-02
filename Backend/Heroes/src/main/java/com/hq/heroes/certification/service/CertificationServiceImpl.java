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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {
    private final CertificationRepository certificationRepository;
    private final DepartmentRepository departmentRepository;

    public CertificationResponseDTO convertToDTO(Certification certification) {
        return CertificationResponseDTO.builder()
                .certificationId(certification.getCertificationId())
                .certificationName(certification.getCertificationName())
                .institution(certification.getInstitution())
                .benefit(certification.getBenefit())
                .deptName(certification.getDepartment() != null ? certification.getDepartment().getDeptName() : null) // null 체크 추가
                .deptId(certification.getDepartment() != null ? certification.getDepartment().getDeptId() : null)
                .build();
    }

    @Override
    public List<CertificationResponseDTO> getCertifications() {
            return certificationRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    @Override
    public CertificationResponseDTO getCertificationById(Long certificationId) {
        Optional<Certification> certification =
                Optional.ofNullable(certificationRepository
                        .findById(certificationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID : " + certificationId)));;
        return convertToDTO(certification.get());
    }

    @Override
    public List<CertificationResponseDTO> getCertificationListByDeptName (String deptName) {
        Department department = departmentRepository.findByDeptName(deptName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department name: " + deptName));

        return certificationRepository
                .findByDepartment(department).stream().map(this::convertToDTO).toList();
    }

    @Override
    @Transactional
    public CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO) {
        // 예외가 발생할 수 있는 부분에 대해 예외 처리
        try {
            Department department = departmentRepository.findById(requestDTO.getDeptId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));

            Certification certification = Certification.builder()
                    .certificationName(requestDTO.getCertificationName())
                    .institution(requestDTO.getInstitution())
                    .benefit(requestDTO.getBenefit())
                    .department(department)
                    .build();

            return convertToDTO(certificationRepository.save(certification));
        } catch (Exception e) {
            // 로그 기록 및 null 반환
            System.err.println("Certification creation failed: " + e.getMessage());
            return null;
        }
    }


    @Override
    @Transactional
    public CertificationResponseDTO updateCertification(Long certificationId, CertificationRequestDTO requestDTO) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + certificationId));

        Optional<Department> optionalDepartment = departmentRepository.findById(requestDTO.getDeptId());

        if(optionalDepartment.isPresent()){
            Department department = optionalDepartment.get();
            certification.setDepartment(department);
        }

        certification.setCertificationName(requestDTO.getCertificationName());
        certification.setInstitution(requestDTO.getInstitution());
        certification.setBenefit(requestDTO.getBenefit());


        return convertToDTO(certificationRepository.save(certification));
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

