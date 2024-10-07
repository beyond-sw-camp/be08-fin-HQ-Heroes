package com.hq.heroes.certification.entity;

import com.hq.heroes.auth.entity.Employee;
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
@Table(name = "tb_employee_certification")
public class EmployeeCertification {
    // 등록 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private long registrationId;

    // 자격증 이름
    @Column(name = "certification_name")
    private String certificationName;

    // 발급 기관
    @Column(name = "institution")
    private String institution;

    // 취득일
    @Column(name = "acquisition_date")
    private String acquisitionDate;

    // 사원과 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}

