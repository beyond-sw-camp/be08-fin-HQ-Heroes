package com.hq.heroes.education.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_education")
public class Education {
    // 교육 번호
    @Id
    @Column(name = "education_id")
    private long educationId;

    // 교육 이름
    @Column(name = "education_name", nullable = false)
    private String educationName;

    // 교육 카테고리
    @Column(name = "category", nullable = false)
    private String category;

    //교육 기관
    @Column(name = "institution", nullable = false)
    private String institution;

    // 교육 시작일
    @CreationTimestamp
    @Column(name = "start_date", nullable = false)
    private String startDate;

    // 교육 종료일
    @CreationTimestamp
    @Column(name = "end_date", nullable = false)
    private String endDate;

    // Employee와의 Many-to-One
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
