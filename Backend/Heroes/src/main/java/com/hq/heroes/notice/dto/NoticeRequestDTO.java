package com.hq.heroes.notice.dto;

import com.hq.heroes.notice.entity.NoticeCategory;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NoticeRequestDTO {

    private String employeeId;
    private String employeeName;
    private String title;
    private String content;
    private Long categoryId;
    private String updaterId;
    private String updaterName;
}