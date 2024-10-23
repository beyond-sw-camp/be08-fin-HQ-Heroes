package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Education;

import java.util.List;

public interface EducationService {
    List<Education> getEducations();

    //- 테스트
    Education getEducationById(Long educationId);

    //- 테스트
    Education createEducation(EducationRequestDTO requestDTO);

    //- 테스트
    Education updateEducation(Long educationId, EducationRequestDTO requestDTO);

    //- 테스트
    boolean deleteEducation(Long educationId);

//    boolean incrementCurrentParticipants(Long educationId, String employeeId);

    //- 테스트
    String incrementCurrentParticipants(Long educationId, String employeeId);

    boolean cancelEducation(Long courseId);

//    void incrementCurrentParticipants(Long curriculumId);
}
