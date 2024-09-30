package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseDTO;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.repository.EducationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {
    private final EducationRepository educationRepository;

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
        Education education = Education.builder()
                .educationName(requestDTO.getEducationName())
                .instructorName(requestDTO.getInstructorName())
                .category(requestDTO.getCategory())
                .institution(requestDTO.getInstitution())
                .startDate(requestDTO.getEducationStart())
                .endDate(requestDTO.getEducationEnd())
                .totalParticipants(requestDTO.getTotalParticipants()) // 총인원 추가
                .build();

        return educationRepository.save(education);
    }

    @Override
    @Transactional
    public Education updateEducation(Long educationId, EducationRequestDTO requestDTO) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + educationId));

        education.setEducationName(requestDTO.getEducationName());
        education.setCategory(requestDTO.getCategory());
        education.setInstitution(requestDTO.getInstitution());
        education.setInstructorName(requestDTO.getInstructorName());
        education.setStartDate(requestDTO.getEducationStart());
        education.setEndDate(requestDTO.getEducationEnd());
        education.setTotalParticipants(requestDTO.getTotalParticipants()); // 총인원 업데이트

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

    @Override
    public CourseDTO applyForCourse(Long educationId) {
        Education education = educationRepository.findById(educationId).orElse(null);

        if (education != null) {
            // 신청한 인원 수 증가
            education.setParticipants(education.getParticipants() + 1);

            educationRepository.save(education);

            return CourseDTO.builder()
                    .courseId(education.getEducationId())
                    .educationName(education.getEducationName())
                    .instructorName(education.getInstructorName())
                    .category(education.getCategory())
                    .institution(education.getInstitution())
                    .totalParticipants(education.getTotalParticipants())
                    .participants(education.getParticipants()) // 신청한 인원 수 포함
                    .build();
        }
        return null; // 교육이 존재하지 않을 경우 null 반환
    }
}
