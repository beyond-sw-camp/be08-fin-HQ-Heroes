package com.hq.heroes.notice.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.dto.NoticeUpdateRequestDTO;
import com.hq.heroes.notice.repository.NoticeCategoryRepository;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    // 작성자
    // Employee 과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    //수정자
    // Employee 과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updater_id")
    private Employee updater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private NoticeCategory category;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // Notice 클래스에 추가된 메소드
    public void updateNotice(NoticeUpdateRequestDTO requestDTO, Employee employee, NoticeCategoryRepository categoryRepository) {
        this.title = requestDTO.getTitle(); // 제목 업데이트
        this.content = requestDTO.getContent(); // 내용 업데이트
        this.category = categoryRepository.findById(requestDTO.getCategoryId()) // 카테고리 업데이트 (ID로 조회)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + requestDTO.getCategoryId()));
        this.updateAt = LocalDateTime.now();
        this.updater = employee;
    }

    public static Notice fromRequestDTO(NoticeRequestDTO requestDTO, Employee employee, NoticeCategory category) {
        return Notice.builder()
                .employee(employee) // 작성자 정보
                .title(requestDTO.getTitle()) // 제목
                .content(requestDTO.getContent()) // 내용
                .category(category) // 카테고리 (ID로 조회한 Category 객체)
                .build();
    }

}