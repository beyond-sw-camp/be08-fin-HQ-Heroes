package com.hq.heroes.salary.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.salary.entity.enums.Status;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "tb_salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Long payrollId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "salary_month", nullable = false)
    private Date salaryMonth;

    @Column(name = "base_salary", precision = 10, scale = 2, nullable = false)
    private double baseSalary;

    @Column(name = "bonus", precision = 10, scale = 2)
    private double bonus;

    @Column(name = "total_salary", precision = 10, scale = 2, nullable = false)
    private double totalSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
