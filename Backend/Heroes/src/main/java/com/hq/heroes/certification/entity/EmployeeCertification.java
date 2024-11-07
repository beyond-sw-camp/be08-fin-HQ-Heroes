package com.hq.heroes.certification.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
import com.hq.heroes.education.entity.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "tb_employee_certification")
public class EmployeeCertification {
    // 등록 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;

    // 자격증 이름
    @Column(name = "certification_name")
    private String certificationName;

    // 발급 기관
    @Column(name = "institution")
    private String institution;

    // 취득일
    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    // 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "employee_certification_status", nullable = false)
    private EmployeeCertificationStatus employeeCertificationStatus = EmployeeCertificationStatus.DENIED;

    // 사원과 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
