package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final EducationRepository educationRepository;
    private final EducationCategoryRepository educationCategoryRepository;

    @Override
    public List<Education> getEducations() {
            return educationRepository.findAll();
    }

    @Override
    public Education getEducationById(Long educationId) {
        return educationRepository.findById(educationId).orElse(null);
    }

    @Override
    public boolean incrementCurrentParticipants(Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("교육을 찾을 수 없습니다."));

        // 현재 인원 수 증가
        education.setCurrentParticipant(education.getCurrentParticipant() + 1);
        educationRepository.save(education);
        return true;
    }

    @Override
    @Transactional
    public Education createEducation(EducationRequestDTO requestDTO) {
        EducationCategory category = educationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Education education = Education.builder()
                .educationName(requestDTO.getEducationName()) // 교육 이름
                .instructorName(requestDTO.getInstructorName()) // 강사 이름
                .institution(requestDTO.getInstitution()) // 교육 기관
                .startDate(LocalDate.from(requestDTO.getEducationStart())) // 교육 시작일
                .endDate(LocalDate.from(requestDTO.getEducationEnd())) // 교육 종료일
                .educationCategory(category) // 카테고리
                .educationCurriculum(requestDTO.getEducationCurriculum()) // 커리큘럼
                .build();

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public Education updateEducation(Long educationId, EducationRequestDTO requestDTO) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + educationId));

        education.setEducationName(requestDTO.getEducationName()); // 교육 이름
        education.setInstitution(requestDTO.getInstitution()); // 교육 기관
        education.setInstructorName(requestDTO.getInstructorName()); // 강사 이름
        education.setStartDate(LocalDate.from(requestDTO.getEducationStart())); // 강의 시작일
        education.setEndDate(LocalDate.from(requestDTO.getEducationEnd())); // 강의 종료일

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public boolean deleteEducation(Long educationId) {
        if (educationRepository.existsById(educationId)) {
            educationRepository.deleteById(educationId);
            return true;
        }
        return false;
    }

}