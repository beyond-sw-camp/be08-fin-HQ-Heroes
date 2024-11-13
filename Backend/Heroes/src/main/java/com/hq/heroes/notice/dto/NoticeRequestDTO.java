package com.hq.heroes.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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