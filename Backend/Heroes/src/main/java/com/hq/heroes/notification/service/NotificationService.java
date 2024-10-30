package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.dto.NotificationsReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    List<NotificationResDTO> getAllNotifications();

    NotificationResDTO getNotificationById(Long notificationId);

    @Async
    void createNotificationAsync(NotificationReqDTO requestDTO);

    @Transactional
    void createNotification(NotificationReqDTO requestDTO);

    @Transactional
    NotificationResDTO updateNotification(Long notificationId, NotificationReqDTO requestDTO);

    List<NotificationResDTO> getNotificationsByReceiverId(String employeeId);

    List<NotificationResDTO> getNotificationsBySenderId(String employeeId);

    int getUnreadNotificationCount(String employeeId);

    boolean markAsRead(Long notificationId, String employeeId);

    @Async
    void deleteNotificationAsync(Long notificationId, String employeeId);

    @Transactional
    boolean deleteNotification(Long notificationId, String employeeId);

    @Transactional
    Notification sendAutomaticNotification(AutoNotificationType type, Map<String, Object> params, Object data);

    @Async
    void sendNotificationAsync(String receiverId, AutoNotificationType notificationType, Object data);

}
