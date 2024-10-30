package com.hq.heroes.notice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDTO {

    private Long noticeId;
    private String employeeId;
    private String employeeName;
    private String updaterId;
    private String updaterName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Long categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름 (필요한 경우)
}