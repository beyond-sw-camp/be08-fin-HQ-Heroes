package com.hq.heroes.certification.service;

import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {
    private final CertificationRepository certificationRepository;

    @Override
    public List<Certification> getCertifications(Long certificationId) {
        if (certificationId != null) {
            return certificationRepository.findByCertificationId(certificationId);
        } else {
            return certificationRepository.findAll();
        }
    }
}
