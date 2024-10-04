package com.hq.heroes.education.entity;

import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.educationCategory.entity.EducationCategory;
import com.hq.heroes.educationCurriculum.entity.EducationCurriculum;
import com.hq.heroes.employeeCertification.entity.EmployeeCertification;
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

    //교육 기관
    @Column(name = "institution", nullable = false)
    private String institution;

    // 교육 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // 교육 종료일
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // EducationCategory과 Many-to-One 관계 (카테고리 번호)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private EducationCategory educationCategory;

    public EducationResponseDTO toResponseDTO() {
        return EducationResponseDTO.builder()
                .educationId(this.educationId)
                .instructorName(this.instructorName)
                .educationName(this.educationName)
                .institution(this.institution)
                .educationStart(this.startDate)
                .educationEnd(this.endDate)
                .category(this.educationCategory.getCategoryName())
                .build();
    }
}
