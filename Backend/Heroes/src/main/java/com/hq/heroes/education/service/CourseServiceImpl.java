package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.enums.CourseStatus;
import com.hq.heroes.education.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseResponseDTO convertToDto(Course course) {
        return CourseResponseDTO.builder()
                .courseId(course.getCourseId())
                .employeeId(course.getEmployee().getEmployeeId())
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

    @Override
    public CourseResponseDTO getCourseById(Long id) {

        Optional<Course> course = courseRepository.findById(id);

        return convertToDto(course.get());
    }

    @Override
    public List<CourseResponseDTO> getCourseByEmployeeId(String employeeId) {
        return courseRepository.findByEmployee_EmployeeId(employeeId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public boolean cancelEducation(Long courseId) {
//        return courseRepository.deleteByCourseId(courseId);
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
            return true;
        }
        return false;
    }

    public CourseResponseDTO completeCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("수강 내역을 찾을 수 없습니다."));

        course.setCourseStatus(CourseStatus.PASS); // 상태를 이수로 변경
        courseRepository.save(course);
        return convertToDto(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(this::convertToDto).toList();
    }
}
