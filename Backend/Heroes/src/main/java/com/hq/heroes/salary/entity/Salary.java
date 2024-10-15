package com.hq.heroes.salary.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.employee.entity.Position;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "position_id", nullable = false)
    private Position position; // 직책에 따른 기본 급여

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "performance_bonus")
    private Double performanceBonus; // 평가 등급에 따른 성과 퍼센트

    @Column(name = "performance_date")
    private LocalDateTime performanceDate; // 성과급 지급 날짜

    // 성과급 계산 메서드 (1월, 7월에 지급)
    public Double calculatePerformanceBonus() {
        return position.getBaseSalary() * performanceBonus;
    }
}
