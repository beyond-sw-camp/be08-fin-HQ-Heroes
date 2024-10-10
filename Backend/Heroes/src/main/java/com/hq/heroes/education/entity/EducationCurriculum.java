package com.hq.heroes.education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
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

    // 신청 시작일
    @Column(name = "application_start_date", nullable = false)
    private LocalDate applicationStartDate;

    // 신청 종료일
    @Column(name = "application_end_date", nullable = false)
    private LocalDate applicationEndDate;

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
