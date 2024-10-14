package com.hq.heroes.notice.dto;

import com.hq.heroes.notice.entity.NoticeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRequestDTO {

    private String employeeId; // This is the employee who created the notice
    private String title;
    private String content;
    private NoticeCategory category;
}