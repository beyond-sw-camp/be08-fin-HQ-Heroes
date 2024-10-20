package com.hq.heroes.overtime.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.overtime.entity.enums.OvertimeStatus;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
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
    @JoinColumn(name = "employee_id", nullable = false)  // 신청인
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = true)  // 결재자, Employee 엔티티 재사용
    private Employee approver;

    @Column(name = "overtime_start_date", nullable = false)
    private LocalDate overtimeStartDate;  // 시작 날짜

    @Column(name = "overtime_start_time", nullable = false)
    private LocalTime overtimeStartTime;  // 시작 시간

    @Column(name = "overtime_end_date", nullable = false)
    private LocalDate overtimeEndDate;  // 종료 날짜

    @Column(name = "overtime_end_time", nullable = false)
    private LocalTime overtimeEndTime;  // 종료 시간

    @Column(name = "comment", columnDefinition = "TEXT", nullable = true)  // 사유 필드 추가
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_status", nullable = false)
    private OvertimeStatus overtimeStatus = OvertimeStatus.PENDING;
}
