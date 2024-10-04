package com.hq.heroes.employeeCertification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_education_certification")
public class EmployeeCertification {
    // 등록 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private int registrationId;

    // 자격증 이름
    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    // 발급 기관
    @Column(name = "institution", nullable = false)
    private String institution;

    // 취득일
    @Column(name = "acquisition_date", nullable = false)
    private String acquisitionDate;
}
