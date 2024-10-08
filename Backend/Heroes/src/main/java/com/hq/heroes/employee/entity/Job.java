package com.hq.heroes.employee.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    // Employee와의 One-to-Many 연관관계 매핑
    @OneToMany(mappedBy = "job")
    private List<Employee> employees;
}
