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

    @Override
    public List<Course> getCourseByEmployeeId (String employeeId) {
        return courseRepository.findByEmployee_EmployeeId(employeeId);
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

    public void completeCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("수강 내역을 찾을 수 없습니다."));

        course.setCourseStatus(CourseStatus.PASS); // 상태를 이수로 변경
        courseRepository.save(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(Course::toResponseDTO)
                .collect(Collectors.toList());
    }
}
