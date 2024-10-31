package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;

import java.util.List;

public interface EducationService {
    List<EducationResponseDTO> getEducations();

    EducationResponseDTO getEducationById(Long educationId);

    EducationResponseDTO createEducation(EducationRequestDTO requestDTO);

    EducationResponseDTO updateEducation(Long educationId, EducationRequestDTO requestDTO);

    boolean deleteEducation(Long educationId);

    CourseResponseDTO incrementCurrentParticipants(Long educationId, String employeeId);

    boolean cancelEducation(Long courseId);
}
