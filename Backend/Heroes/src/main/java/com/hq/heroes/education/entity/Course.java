package com.hq.heroes.education.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.education.dto.CourseResponseDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_course")
public class Course {
    // 수강 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;

    // Employee와의 Many-to-One 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Education과의 Many-to-One 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_id", nullable = false)
    private Education education;

    public CourseResponseDTO toResponseDTO() {
        return CourseResponseDTO.builder()
                .courseId(this.courseId)
                .educationName(this.education.getEducationName())
                .employeeName(this.employee.getEmployeeName())
                .startDate(this.education.getStartDate())
                .endDate(this.education.getEndDate())
                .applicationStartDate(this.education.getEducationCurriculum().getApplicationStartDate())
                .applicationEndDate(this.education.getEducationCurriculum().getApplicationEndDate())
                .categoryName(this.education.getEducationCategory().getCategoryName())
                .build();
    }
}
