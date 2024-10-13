package com.hq.heroes.salary.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.salary.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "salary_month", nullable = false)
    private LocalDateTime salaryMonth;

    @Column(name = "base_salary", nullable = false)
    private Double baseSalary;

    @Column(name = "bonus", nullable = false)
    private Double bonus;

    @Column(name = "total_salary", nullable = false)
    private Double totalSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 연도를 반환하는 메서드
    public int getYear() {
        return salaryMonth.getYear();
    }

    // 월을 반환하는 메서드
    public int getMonth() {
        return salaryMonth.getMonthValue();
    }

}
