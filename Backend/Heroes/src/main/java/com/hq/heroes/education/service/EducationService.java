package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseDTO;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Education;

import java.util.List;

public interface EducationService {
    List<Education> getEducations();

    Education getEducationById(Long educationId);

    Education createEducation(EducationRequestDTO requestDTO);

    Education updateEducation(Long educationId, EducationRequestDTO requestDTO);

    boolean deleteEducation(Long educationId);
}
