package com.hq.heroes.certification.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.employee.entity.Department;
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
                .deptId(this.department.getDeptId())
                .build();
    }
}
