package com.hq.heroes.notification.dto;

import com.hq.heroes.notification.entity.enums.NotificationStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificationsReqDTO {

    private String senderId;
    private String[] receiverIds;
    private Long categoryId;
    private String message;

    // 기본 상태값을 'UNREAD'로 설정
    private NotificationStatus status = NotificationStatus.UNREAD;
}