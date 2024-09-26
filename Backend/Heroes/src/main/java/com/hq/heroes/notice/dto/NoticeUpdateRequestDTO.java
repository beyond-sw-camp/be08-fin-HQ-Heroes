package com.hq.heroes.notice.dto;

import com.hq.heroes.notice.entity.enums.NoticeCategory;
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
    private String writer;
    private NoticeCategory category;

}
