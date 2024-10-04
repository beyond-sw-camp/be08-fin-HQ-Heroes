package com.hq.heroes.educationCurriculum.entity;

import com.hq.heroes.education.entity.Education;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_education_curriculum")
public class EducationCurriculum {
    // 커리큘럼 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    private Long curriculumId;

    // 신청 기간
    @Column(name = "application_date", nullable = false)
    private Date applicationDate;

    // 수강 정원
    @Column(name = "participants", nullable = false)
    private Integer participants;

    // 강의 장소
    @Column(name = "education_place", nullable = false)
    private String educationPlace;

    // Education과의 One-to-One 관계
    @OneToOne
    @JoinColumn(name = "education_id", referencedColumnName = "education_id")
    private Education education;
}
