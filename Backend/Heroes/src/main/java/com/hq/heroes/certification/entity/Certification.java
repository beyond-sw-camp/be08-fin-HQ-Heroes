package com.hq.heroes.certification.entity;

import com.hq.heroes.employee.entity.Department;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_certification")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private long certificationId;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "institution", nullable = false)
    private String institution;

    @Column(name = "benefit")
    private String benefit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
}
