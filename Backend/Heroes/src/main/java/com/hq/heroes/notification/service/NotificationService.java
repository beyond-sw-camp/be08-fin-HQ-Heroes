package com.hq.heroes.notification.service;

import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotifications();

    //- 테스트
    Notification getNotificationById(Long notificationId);

    //- 테스트
    @Transactional
    Notification createNotification(NotificationReqDTO requestDTO);

    //- 테스트
    @Transactional
    Notification updateNotification(Long notificationId, NotificationReqDTO requestDTO);

    //- 테스트
    List<Notification> getNotificationsByReceiverId(String employeeId);

    //- 테스트
    List<Notification> getNotificationsBySenderId(String employeeId);

    int getUnreadNotificationCount(String employeeId);

    //- 테스트
    boolean markAsRead(Long notificationId, String employeeId);

    //- 테스트
    @Transactional
    boolean deleteNotification(Long notificationId, String employeeId);
}
