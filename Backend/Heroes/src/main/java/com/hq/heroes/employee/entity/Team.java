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
@Table(name = "tb_team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    // 부서와의 Many-to-One 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    // Employee와의 One-to-Many 연관관계 매핑
    @OneToMany(mappedBy = "team")
    private List<Employee> employees;

}
