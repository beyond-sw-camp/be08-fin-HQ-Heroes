package com.hq.heroes.vacation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
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
@Table(name = "tb_vacation")
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_id", nullable = false, updatable = false)
    private Long vacationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = true)
    private Employee approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = true)
    private Employee applicant;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_type", nullable = false)
    private VacationType vacationType;

    @Column(name = "vacation_start_date", nullable = false)
    private LocalDate vacationStartDate;

    @Column(name = "vacation_start_time", nullable = false)
    private LocalTime vacationStartTime;

    @Column(name = "vacation_end_date", nullable = false)
    private LocalDate vacationEndDate;

    @Column(name = "vacation_end_time", nullable = false)
    private LocalTime vacationEndTime;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = true)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_status", nullable = false)
    private VacationStatus vacationStatus = VacationStatus.PENDING;
}
