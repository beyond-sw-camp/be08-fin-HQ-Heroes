package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.entity.EducationCurriculum;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationCurriculumRepository;
import com.hq.heroes.education.repository.EducationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final EducationRepository educationRepository;
    private final EducationCategoryRepository educationCategoryRepository;
    private final EducationCurriculumRepository educationCurriculumRepository;

    @Override
    public List<Education> getEducations(String educationName) {
        if (educationName != null && !educationName.isEmpty()) {
            return educationRepository.findByEducationName(educationName);
        } else {
            return educationRepository.findAll();
        }
    }

    @Override
    public Education getEducationById(Long educationId) {
        return educationRepository.findById(educationId).orElse(null);
    }

    @Override
    @Transactional
    public Education createEducation(EducationRequestDTO requestDTO) {
        EducationCategory category = educationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        EducationCurriculum curriculum = educationCurriculumRepository.findById(requestDTO.getCurriculumId())
                .orElseThrow(() -> new IllegalArgumentException("Curriculum not found"));

        Education education = Education.builder()
                .educationName(requestDTO.getEducationName())
                .instructorName(requestDTO.getInstructorName())
                .institution(requestDTO.getInstitution())
                .startDate(LocalDate.from(requestDTO.getEducationStart()))
                .endDate(LocalDate.from(requestDTO.getEducationEnd()))
                .educationCategory(category)
                .educationCurriculum(curriculum)
                .build();

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public Education updateEducation(Long educationId, EducationRequestDTO requestDTO) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + educationId));

        education.setEducationName(requestDTO.getEducationName());
        education.setInstitution(requestDTO.getInstitution());
        education.setInstructorName(requestDTO.getInstructorName());
        education.setStartDate(LocalDate.from(requestDTO.getEducationStart()));
        education.setEndDate(LocalDate.from(requestDTO.getEducationEnd()));

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