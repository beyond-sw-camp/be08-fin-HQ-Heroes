package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.entity.Course;

import java.util.List;

public interface CourseService  {

    CourseResponseDTO getCourseById(Long id);

    //- 테스트
    List<CourseResponseDTO> getCourseByEmployeeId(String employeeId);

    //- 테스트
    boolean cancelEducation(Long courseId);

    //- 테스트
    CourseResponseDTO completeCourse(Long courseId);

    List<CourseResponseDTO> getAllCourses();
}
