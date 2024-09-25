package com.hq.heroes.employee.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
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

    // Employee와의 One-to-Many 연관관계 매핑
    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    private List<Employee> employees;

}
