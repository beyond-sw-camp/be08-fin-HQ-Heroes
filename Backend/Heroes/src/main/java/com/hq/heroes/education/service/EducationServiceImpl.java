package com.hq.heroes.education.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationCategoryRepository educationCategoryRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<Education> getEducations() {
            return educationRepository.findAll();
    }

    @Override
    public Education getEducationById(Long educationId) {
        return educationRepository.findById(educationId).orElse(null);
    }

    @Override
    public boolean incrementCurrentParticipants(Long educationId, String employeeId) {

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("교육을 찾을 수 없습니다."));

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("사원을 찾을 수 없습니다."));

        // 현재 인원 수 증가
        education.setCurrentParticipant(education.getCurrentParticipant() + 1);
        educationRepository.save(education);

        System.out.println("employee = " + employee);
        System.out.println("education = " + education);

        Course course = Course.builder()
                .education(education)
                .employee(employee)
                .courseStatus(CourseStatus.FAIL)
                .build();

        System.out.println("course = " + course);

        courseRepository.save(course);

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
                .participants(requestDTO.getParticipants()) // 수강 정원
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