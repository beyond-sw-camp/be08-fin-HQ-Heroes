package com.hq.heroes.education.repository;

import com.hq.heroes.education.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByEmployee_EmployeeId(String employeeId);
}
