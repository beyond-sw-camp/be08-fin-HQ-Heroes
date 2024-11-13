package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;

import java.util.List;

public interface EducationService {

    // 교육 조회
    List<EducationResponseDTO> getEducations();

    // 교육 번호로 교육 조회
    EducationResponseDTO getEducationById(Long educationId);

    // 교육 생성
    EducationResponseDTO createEducation(EducationRequestDTO requestDTO);

    // 교육 수정
    EducationResponseDTO updateEducation(Long educationId, EducationRequestDTO requestDTO);

    // 교육 삭제
    boolean deleteEducation(Long educationId);

    // 현재 신청자 수 증가
    CourseResponseDTO incrementCurrentParticipants(Long educationId, String employeeId);

    // 교육 취소
    boolean cancelEducation(Long courseId);
}
