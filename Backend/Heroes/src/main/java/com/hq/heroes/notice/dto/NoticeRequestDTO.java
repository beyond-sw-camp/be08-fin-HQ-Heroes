package com.hq.heroes.notice.dto;

import com.hq.heroes.notice.entity.enums.NoticeCategory;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRequestDTO {

    private String employeeId; // This is the employee who created the notice
    private String title;
    private String content;
    private String writer;
    private NoticeCategory category;
}
