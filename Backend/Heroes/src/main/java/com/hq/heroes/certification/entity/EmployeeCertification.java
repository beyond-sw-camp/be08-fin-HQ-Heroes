package com.hq.heroes.certification.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.certification.entity.enums.EmployeeCertificationStatus;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;

    @Column(name = "certification_name")
    private String certificationName;

    @Column(name = "institution")
    private String institution;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_certification_status", nullable = false)
    private EmployeeCertificationStatus employeeCertificationStatus = EmployeeCertificationStatus.DENIED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
