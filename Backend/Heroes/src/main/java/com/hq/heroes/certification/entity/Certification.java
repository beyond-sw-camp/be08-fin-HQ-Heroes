package com.hq.heroes.certification.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.employee.entity.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_certification")
public class Certification {
    // 자격증 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private long certificationId;

    // 자격증 이름
    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    // 발급기관
    @Column(name = "institution", nullable = false)
    private String institution;

    // 혜택
    @Column(name = "benefit")
    private String benefit;

    // 신청 시작일
    @Column(name = "application_start_date")
    private LocalDate applicationStartDate;

    // 신청 종료일
    @Column(name = "application_end_date")
    private LocalDate applicationEndDate;

    // 시험 날짜
    @Column(name = "exam_date")
    private LocalDate examDate;

    // Department와의 Many-to-One 관계 (부서 번호)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    public CertificationResponseDTO toResponseDTO() {
        return CertificationResponseDTO.builder()
                .certificationId(this.certificationId)
                .certificationName(this.certificationName)
                .institution(this.institution)
                .benefit(this.benefit)
                .applicationStartDate(this.applicationStartDate)
                .applicationEndDate(this.applicationEndDate)
                .examDate(this.examDate)
                .deptId(this.department.getDeptId())
                .build();
    }
}
