package com.hq.heroes.vacation.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
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

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_type", nullable = false)
    private VacationType vacationType;

    @Column(name = "vacation_start", nullable = false)
    private LocalDateTime vacationStart;

    @Column(name = "vacation_end", nullable = false)
    private LocalDateTime vacationEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_status", nullable = false)
    private VacationStatus vacationStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 엔티티 생성 시 현재 시간 설정
        this.updatedAt = LocalDateTime.now(); // 엔티티 생성 시 수정 시간도 동일하게 설정
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // 엔티티 업데이트 시 수정 시간 갱신
    }
}
