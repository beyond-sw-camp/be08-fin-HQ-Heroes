package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationsReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    List<Notification> getAllNotifications();

    Notification getNotificationById(Long notificationId);

    @Async
    void createNotificationAsync(NotificationReqDTO requestDTO);

    @Transactional
    void createNotification(NotificationReqDTO requestDTO);

    @Transactional
    Notification updateNotification(Long notificationId, NotificationReqDTO requestDTO);

    List<Notification> getNotificationsByReceiverId(String employeeId);

    List<Notification> getNotificationsBySenderId(String employeeId);

    int getUnreadNotificationCount(String employeeId);

    boolean markAsRead(Long notificationId, String employeeId);

    @Transactional
    boolean deleteNotification(Long notificationId, String employeeId);

    @Transactional
    void sendAutomaticNotification(AutoNotificationType type, Map<String, Object> params, Object data);

    @Async
    void sendNotificationAsync(String receiverId, AutoNotificationType notificationType, Object data);

}
