package com.hq.heroes.auth.entity;

import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private long employeeId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "join_date", updatable = false)
    private Date joinDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private Date updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.Y;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_admin", nullable = false)
    private Role role = Role.ROLE_ADMIN;
}

