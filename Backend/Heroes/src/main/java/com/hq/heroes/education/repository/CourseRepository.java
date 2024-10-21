package com.hq.heroes.education.repository;

import com.hq.heroes.education.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c JOIN FETCH c.employee JOIN FETCH c.education WHERE c.employee.employeeId = :employeeId")
    List<Course> findByEmployee_EmployeeId(String employeeId);

    boolean existsByEducation_EducationIdAndEmployee_EmployeeId(Long educationId, String employeeId);

}
