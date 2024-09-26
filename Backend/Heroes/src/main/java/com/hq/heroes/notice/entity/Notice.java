package com.hq.heroes.notice.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.dto.NoticeRequestDTO;
import com.hq.heroes.notice.dto.NoticeResponseDTO;
import com.hq.heroes.notice.entity.enums.NoticeCategory;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "writer", nullable = false, length = 20)
    private String writer;

    @Column(name = "category", nullable = false, length = 20)
    private NoticeCategory category;

    public void updateNotice(NoticeRequestDTO requestDTO) {
        this.title = requestDTO.getTitle();
        this.content = requestDTO.getContent();
        this.writer = requestDTO.getWriter();
        this.category = requestDTO.getCategory();
    }

    public NoticeResponseDTO toResponseDTO() {
        return NoticeResponseDTO.builder()
                .noticeId(this.noticeId)
                .employeeName(this.employee.getEmployeeName()) // Assuming Employee has a getName() method
                .title(this.title)
                .content(this.content)
                .createdAt(this.createdAt)
                .writer(this.writer)
                .category(this.category)
                .build();
    }

    public static Notice fromRequestDTO(NoticeRequestDTO requestDTO, Employee employee) {
        return Notice.builder()
                .employee(employee)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .writer(requestDTO.getWriter())
                .category(requestDTO.getCategory())
                .build();
    }
}
