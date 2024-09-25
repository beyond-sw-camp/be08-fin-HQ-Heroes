package com.hq.heroes.retire.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_retirement_pay")
public class Retire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "retirement_pay_id")
    private Long retirementPayId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "period", nullable = false)
    private int period;

    @Column(name = "payroll_avg", nullable = false)
    private Double payrollAvg;

    @Column(name = "anuual_bonus", nullable = false)
    private Double annualBonus;

    @Column(name = "received_amount", nullable = false)
    private Double receivedAmount;

}
