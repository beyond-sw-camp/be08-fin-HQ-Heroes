package com.hq.heroes.notification.dto;

import com.hq.heroes.notification.entity.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResDTO {

    private Long notificationId;
    private String senderName;    // Employee sender name
    private String receiverName;  // Employee receiver name
    private String categoryName;  // Notification category name
    private String message;
    private NotificationStatus status;
    private Date createdAt;
    private Boolean receiveDelete;
    private Boolean sendDelete;

}
