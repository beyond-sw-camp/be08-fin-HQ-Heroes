package com.hq.heroes.salary.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_salary_history")
public class SalaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_history_id")
    private Long salaryHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pre_tax_total", nullable = false)
    private Double preTaxTotal; // 세전 (급여 + 성과급)

    @Column(name = "post_tax_total", nullable = false)
    private Double postTaxTotal; // 세후 (세전 - 공제액)

    @Column(name = "salary_month", nullable = false)
    private LocalDateTime salaryMonth;

    @Column(name = "bonus")
    private Double bonus;

    @Column(name = "work_time")
    private long workTime;

    @Column(name = "over_time")
    private long overTime;

    // 공제 항목 추가
    @Column(name = "national_pension", nullable = false)
    private Double nationalPension;

    @Column(name = "health_insurance", nullable = false)
    private Double healthInsurance;

    @Column(name = "long_term_care", nullable = false)
    private Double longTermCare;

    @Column(name = "employment_insurance", nullable = false)
    private Double employmentInsurance;

    @Column(name = "income_tax", nullable = false)
    private Double incomeTax;

    @Column(name = "local_income_tax", nullable = false)
    private Double localIncomeTax;
}
