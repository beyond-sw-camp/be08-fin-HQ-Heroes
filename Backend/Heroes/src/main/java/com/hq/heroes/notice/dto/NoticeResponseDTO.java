package com.hq.heroes.notice.dto;

import com.hq.heroes.notice.entity.NoticeCategory;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDTO {

    private Long noticeId;
    private String employeeName; // Assuming employee has a name field
    private String title;
    private String content;
    private Date createdAt;
    private String writer;
    private NoticeCategory category;
}
