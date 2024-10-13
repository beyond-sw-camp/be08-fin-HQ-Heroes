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

    private String senderId;
    private String receiverId;
    private Long categoryId;
    private String message;

    // 기본 상태값을 'UNREAD'로 설정
    private NotificationStatus status = NotificationStatus.UNREAD;
}

