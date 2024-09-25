package com.hq.heroes.notice.entity;

import com.hq.heroes.auth.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "writer", nullable = false, length = 20)
    private String writer;

    @Column(name = "category", nullable = false, length = 20)
    private String category;
}
