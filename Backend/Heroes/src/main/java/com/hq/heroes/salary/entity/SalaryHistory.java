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
    private LocalDateTime salaryMonth; // 정산 월

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status; // 지급 상태

    @CreationTimestamp
    @Column(name = "payment_date", nullable = true)
    private LocalDateTime paymentDate; // 지급 일자

    // 공제 항목 추가
    @Column(name = "national_pension", nullable = false)
    private Double nationalPension; // 국민연금

    @Column(name = "health_insurance", nullable = false)
    private Double healthInsurance; // 건강보험

    @Column(name = "long_term_care", nullable = false)
    private Double longTermCare; // 장기요양

    @Column(name = "employment_insurance", nullable = false)
    private Double employmentInsurance; // 고용보험

    @Column(name = "income_tax", nullable = false)
    private Double incomeTax; // 소득세

    @Column(name = "local_income_tax", nullable = false)
    private Double localIncomeTax; // 지방소득세
}
