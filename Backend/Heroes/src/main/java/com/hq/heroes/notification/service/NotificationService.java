package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotifications();

    Notification getNotificationById(Long notificationId);

    @Transactional
    Notification createNotification(NotificationReqDTO requestDTO);

    @Transactional
    Notification updateNotification(Long notificationId, NotificationReqDTO requestDTO);

    List<Notification> getNotificationsByReceiverId(String employeeId);

    int getUnreadNotificationCount(String employeeId);

    boolean markAsRead(Long notificationId, String employeeId);

    @Transactional
    boolean deleteNotification(Long notificationId, String employeeId);
}
