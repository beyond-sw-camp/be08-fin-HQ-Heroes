package com.hq.heroes.notice.dto;

import com.hq.heroes.auth.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDTO {

    private Long noticeId;
    private Employee employee;
    private Employee updater;
    private String title;
    private String content;
    private Date createdAt;
    private Long categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름 (필요한 경우)
}