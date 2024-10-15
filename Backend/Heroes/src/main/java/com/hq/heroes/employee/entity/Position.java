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
@Table(name = "tb_position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    @Column(name = "base_salary", nullable = false)
    private Double baseSalary;

    // Employee와의 One-to-Many 연관관계 매핑
    @OneToMany(mappedBy = "position")
    private List<Employee> employees;

}
