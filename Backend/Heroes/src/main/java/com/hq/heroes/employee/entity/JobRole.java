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
@Table(name = "tb_jobrole")
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobrole_id")
    private Long jobRoleId;

    @Column(name = "jobrole_name", nullable = false)
    private String jobRoleName;

    // Employee와의 One-to-Many 연관관계 매핑
    @OneToMany(mappedBy = "job")
    private List<Employee> employees;
}
