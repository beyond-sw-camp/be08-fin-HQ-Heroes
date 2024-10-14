package com.hq.heroes.education.service;

import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public List<Course> getCoursesByEmployeeId(String employeeId) {
        return courseRepository.findByEmployee_EmployeeId(employeeId);
    }
}
