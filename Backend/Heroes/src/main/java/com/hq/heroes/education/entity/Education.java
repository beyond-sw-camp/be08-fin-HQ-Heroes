package com.hq.heroes.education.entity;

import com.hq.heroes.education.dto.EducationResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_education")
public class Education {
    // 교육 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_id")
    private long educationId;

    // 강사 이름
    @Column(name = "instructor_name", nullable = false)
    private String instructorName;

    // 교육 이름
    @Column(name = "education_name", nullable = false)
    private String educationName;

    // 교육 카테고리
    @Column(name = "category", nullable = false)
    private String category;

    //교육 기관
    @Column(name = "institution", nullable = false)
    private String institution;

    // 교육 시작일
    @CreationTimestamp
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // 교육 종료일
    @CreationTimestamp
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // 참여 인원 수
    @Column(name = "participants", nullable = false)
    private int participants;

    // 총 인원 수
    @Column(name = "total_participants", nullable = false)
    private int totalParticipants;

    public EducationResponseDTO toResponseDTO() {
        return EducationResponseDTO.builder()
                .educationId(this.educationId)
                .instructorName(this.instructorName)
                .educationName(this.educationName)
                .category(this.category)
                .institution(this.institution)
                .educationStart(this.startDate)
                .educationEnd(this.endDate)
                .participants(this.participants)
                .totalParticipants(this.totalParticipants)
                .build();
    }
}
