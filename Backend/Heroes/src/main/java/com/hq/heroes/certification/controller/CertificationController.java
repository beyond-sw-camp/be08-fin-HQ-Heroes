package com.hq.heroes.certification.controller;

import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certification-service")
@Tag(name = "Certification APIs", description = "자격증 관련 API 목록")
public class CertificationController {
    private final CertificationService certificationService;

    // 자격증 목록 조회하기
    @GetMapping("/certification")
    @Operation(summary = "자격증 목록 조회", description = "전체 자격증의 목록을 조회한다.")
    public ResponseEntity<List<CertificationResponseDTO>> getCertifications(@RequestParam(required = false) Long certificationId) {
        List<Certification> certifications = certificationService.getCertifications(certificationId);
        List<CertificationResponseDTO> certificationDTOS = certifications.stream().map(Certification::toResponseDTO).toList();

        if (certificationId != null) {
            return new ResponseEntity<>(certificationDTOS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(certificationDTOS, HttpStatus.NOT_FOUND);
        }
    }

    // 사원이 현재 가지고 있는 자격증 조회하기
//    @GetMapping("/certification/my")
//    @Operation(summary = "특정 자격증 목록 조회", description = "사원이 가지고 있는 자격증의 목록을 조회한다.")
//    public List<CertificationResponseDTO> getMyCertifications(@RequestParam(required = false) Long certificationId) {
//
//    }
}