package com.hq.heroes.notification.dto;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notification.entity.NotificationCategory;
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
public class NotificationDTO {

    private Long notificationId;
    private Employee sender;
    private Employee receiver;
    private NotificationCategory category;
    private String message;
    private NotificationStatus status;
    private Date createdAt;

}
