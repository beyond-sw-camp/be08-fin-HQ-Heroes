package com.hq.heroes.education.repository;

import com.hq.heroes.education.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
