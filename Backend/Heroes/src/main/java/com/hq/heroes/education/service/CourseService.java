package com.hq.heroes.education.service;

import com.hq.heroes.education.entity.Course;

import java.util.List;

public interface CourseService  {
    List<Course> getCoursesByEmployeeId(String employeeId);
}
