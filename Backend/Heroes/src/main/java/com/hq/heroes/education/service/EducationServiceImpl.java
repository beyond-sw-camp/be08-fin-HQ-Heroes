package com.hq.heroes.education.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.dto.EducationRequestDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.education.entity.EducationCategory;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import com.hq.heroes.education.repository.EducationCategoryRepository;
import com.hq.heroes.education.repository.EducationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationCategoryRepository educationCategoryRepository;
    private final CourseRepository courseRepository;
    private final EmployeeRepository employeeRepository;

    public CourseResponseDTO convertToDto(Course course) {
        return CourseResponseDTO.builder()
                .courseId(course.getCourseId())
                .educationName(course.getEducation().getEducationName())
                .employeeName(course.getEmployee().getEmployeeName())
                .instructorName(course.getEducation().getInstructorName())
                .institution(course.getEducation().getInstitution())
                .startDate(course.getEducation().getStartDate())
                .endDate(course.getEducation().getEndDate())
                .categoryName(course.getEducation().getEducationCategory().getCategoryName())
                .courseStatus(course.getCourseStatus())
                .educationCurriculum(course.getEducation().getEducationCurriculum())
                .build();
    }

    public EducationResponseDTO convertToDTO(Education education) {
        return EducationResponseDTO.builder()
                .educationId(education.getEducationId())
                .instructorName(education.getInstructorName())
                .educationName(education.getEducationName())
                .institution(education.getInstitution())
                .educationStart(education.getStartDate() != null ? LocalDate.from(education.getStartDate()) : null)
                .educationEnd(education.getEndDate() != null ? LocalDate.from(education.getEndDate()) : null)
                .participants(education.getParticipants())
                .categoryName(education.getEducationCategory() != null ? education.getEducationCategory().getCategoryName() : null)
                .categoryId(education.getEducationCategory() != null ? education.getEducationCategory().getCategoryId() : null)
                .educationCurriculum(education.getEducationCurriculum())
                .currentParticipant(education.getCurrentParticipant())
                .build();
    }

    @Override
    public List<EducationResponseDTO> getEducations() {
        return educationRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    @Override
    public EducationResponseDTO getEducationById(Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 ID의 교육이 존재하지 않습니다."));

        return convertToDTO(education);
    }

    @Override
    public CourseResponseDTO incrementCurrentParticipants(Long educationId, String employeeId) {

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("교육을 찾을 수 없습니다."));

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("사원을 찾을 수 없습니다."));

        if (courseRepository.existsByEducation_EducationIdAndEmployee_EmployeeId(educationId, employeeId)) {
            throw new IllegalStateException("이미 신청한 교육입니다.");
        }

        education.setCurrentParticipant(education.getCurrentParticipant() + 1);
        educationRepository.save(education);

        Course course = Course.builder()
                .education(education)
                .employee(employee)
                .courseStatus(CourseStatus.FAIL)
                .build();

        courseRepository.save(course);

        return convertToDto(course);
    }

    @Override
    @Transactional
    public EducationResponseDTO createEducation(EducationRequestDTO requestDTO) {
        EducationCategory category = educationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Education education = Education.builder()
                .educationName(requestDTO.getEducationName())
                .instructorName(requestDTO.getInstructorName())
                .institution(requestDTO.getInstitution())
                .startDate(LocalDate.from(requestDTO.getEducationStart()))
                .endDate(LocalDate.from(requestDTO.getEducationEnd()))
                .participants(requestDTO.getParticipants())
                .educationCategory(category)
                .educationCurriculum(requestDTO.getEducationCurriculum() != null ? requestDTO.getEducationCurriculum() : "")
                .build();

        education = educationRepository.save(education);
        return convertToDTO(education);
    }

    @Override
    @Transactional
    public EducationResponseDTO updateEducation(Long educationId, EducationRequestDTO requestDTO) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 ID : " + educationId));
        EducationCategory category = educationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID : " + requestDTO.getCategoryId()));

        education.setEducationName(requestDTO.getEducationName());
        education.setInstitution(requestDTO.getInstitution());
        education.setInstructorName(requestDTO.getInstructorName());
        education.setStartDate(LocalDate.from(requestDTO.getEducationStart()));
        education.setEndDate(LocalDate.from(requestDTO.getEducationEnd()));
        education.setEducationCategory(category);
        education.setParticipants(requestDTO.getParticipants());
        education.setEducationCurriculum(requestDTO.getEducationCurriculum());

        education = educationRepository.save(education);
        return convertToDTO(education);
    }

    @Override
    @Transactional
    public boolean cancelEducation(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            Education education = course.getEducation();

            int currentParticipant = education.getCurrentParticipant();
            if (currentParticipant > 0) {
                education.setCurrentParticipant(currentParticipant - 1);
                educationRepository.save(education);
                System.out.println("현재 참가자 수 = " + (currentParticipant - 1));
            } else {
                log.debug("참가자가 없습니다.");
            }

            courseRepository.delete(course);
            return true;
        }
        return false;
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