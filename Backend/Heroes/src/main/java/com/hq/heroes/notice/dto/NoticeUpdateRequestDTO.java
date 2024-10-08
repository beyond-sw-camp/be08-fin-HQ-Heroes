package com.hq.heroes.notice.dto;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.entity.NoticeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequestDTO {

    private String title;
    private String content;
    private Employee updater;
    private NoticeCategory category;

}
