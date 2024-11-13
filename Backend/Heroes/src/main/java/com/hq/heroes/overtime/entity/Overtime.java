package com.hq.heroes.overtime.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_overtime")
public class Overtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overtime_id", nullable = false, updatable = false)
    private Long overtimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = true)
    private Employee approver;

    @Column(name = "overtime_start_date", nullable = false)
    private LocalDate overtimeStartDate;

    @Column(name = "overtime_start_time", nullable = false)
    private LocalTime overtimeStartTime;

    @Column(name = "overtime_end_date", nullable = false)
    private LocalDate overtimeEndDate;

    @Column(name = "overtime_end_time", nullable = false)
    private LocalTime overtimeEndTime;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = true)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_status", nullable = false)
    private OvertimeStatus overtimeStatus = OvertimeStatus.PENDING;
}
