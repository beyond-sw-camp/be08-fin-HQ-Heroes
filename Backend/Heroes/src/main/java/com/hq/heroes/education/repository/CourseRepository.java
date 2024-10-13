package com.hq.heroes.education.repository;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByEmployee_EmployeeId(String employeeId);
}
