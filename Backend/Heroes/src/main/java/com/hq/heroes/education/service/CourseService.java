package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.entity.Course;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface CourseService  {
    List<Course> getCourseByEmployeeId(String employeeId);

    boolean cancelEducation(Long courseId);

    void completeCourse(Long courseId);

    List<CourseResponseDTO> getAllCourses();
}
