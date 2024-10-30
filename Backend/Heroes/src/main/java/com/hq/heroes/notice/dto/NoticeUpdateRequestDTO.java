package com.hq.heroes.notice.dto;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notice.entity.NoticeCategory;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequestDTO {
    private String title;
    private String content;
    private String employeeId;
    private Long categoryId;

}
