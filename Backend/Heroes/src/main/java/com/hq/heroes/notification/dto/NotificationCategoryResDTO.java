package com.hq.heroes.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCategoryResDTO {
    private Long notificationCategoryId;  // ID of the notification category
    private String categoryName;          // Name of the notification category
}
