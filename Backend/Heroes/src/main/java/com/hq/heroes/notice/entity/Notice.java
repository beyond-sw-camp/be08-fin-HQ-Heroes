package com.hq.heroes.notice.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
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

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    public void updateNotice(NoticeRequestDTO requestDTO) {
        this.title = requestDTO.getTitle();    // 제목 업데이트
        this.content = requestDTO.getContent(); // 내용 업데이트
        this.category = requestDTO.getCategory(); // 카테고리 업데이트
    }


    public NoticeResponseDTO toResponseDTO() {
        return NoticeResponseDTO.builder()
                .noticeId(this.noticeId) // 공지사항 ID
                .employee(this.employee) // 작성자 이름 (Employee 엔티티에서 가져옴)
                .updater(this.updater)
                .title(this.title) // 제목
                .content(this.content) // 내용
                .categoryId(this.category.getNoticeCategoryId()) // 카테고리 ID
                .categoryName(this.category.getCategoryName()) // 카테고리 이름
                .build();
    }


    public static Notice fromRequestDTO(NoticeRequestDTO requestDTO, Employee employee) {
        return Notice.builder()
                .employee(employee) // 작성자 정보
                .title(requestDTO.getTitle()) // 제목
                .content(requestDTO.getContent()) // 내용
                .category(requestDTO.getCategory()) // 카테고리
                .build();
    }

}