package com.hq.heroes.education.repository;

import com.hq.heroes.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education,Long> {
    // 교육 이름으로 교육 정보를 찾는 메서드 추가
    List<Education> findByEducationName(String educationName);

    void deleteByEducationId(Long courseId);
}
