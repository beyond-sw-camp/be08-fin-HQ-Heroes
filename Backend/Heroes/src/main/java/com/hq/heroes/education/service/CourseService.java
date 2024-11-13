package com.hq.heroes.education.service;

import com.hq.heroes.education.dto.CourseResponseDTO;

import java.util.List;

public interface CourseService  {

    // 수강 번호로 조회
    CourseResponseDTO getCourseById(Long id);

    // 사원 번호로 수강 조회
    List<CourseResponseDTO> getCourseByEmployeeId(String employeeId);

    // 이수한 수강
    CourseResponseDTO completeCourse(Long courseId);

    // 모든 수강 조회
    List<CourseResponseDTO> getAllCourses();
}
