package com.hq.heroes.notification.dto;

import com.hq.heroes.notification.entity.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReqDTO {

    private String senderId;  // Employee sender ID
    private String receiverId; // Employee receiver ID
    private Long categoryId; // Notification Category ID
    private String message;
    private NotificationStatus status;

}
