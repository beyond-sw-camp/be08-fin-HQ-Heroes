package com.hq.heroes.vacation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "employee_id", nullable = false)  // 신청인
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = true)  // 결재자, Employee 엔티티 재사용
    private Employee approver;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_type", nullable = false)
    private VacationType vacationType;

    @Column(name = "vacation_start", nullable = false)
    private LocalDateTime vacationStart;

    @Column(name = "vacation_end", nullable = false)
    private LocalDateTime vacationEnd;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = true)  // 사유 필드 추가
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_status", nullable = false)
    private VacationStatus vacationStatus = VacationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

