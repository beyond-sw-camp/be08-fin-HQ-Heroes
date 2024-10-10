package com.hq.heroes.notification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.repository.NotificationCategoryRepository;
import com.hq.heroes.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationCategoryRepository notificationCategoryRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElse(null);
    }

    @Override
    @Transactional
    public Notification createNotification(NotificationReqDTO requestDTO) {
        // Fetch sender, receiver, and category
        Employee sender = employeeRepository.findById(requestDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        Employee receiver = employeeRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));
        NotificationCategory category = notificationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        // Create notification entity
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .category(category)
                .message(requestDTO.getMessage())
                .status(requestDTO.getStatus())
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification updateNotification(Long notificationId, NotificationReqDTO requestDTO) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 ID : " + notificationId));

        // Update notification details
        notification.setMessage(requestDTO.getMessage());
        notification.setStatus(requestDTO.getStatus());

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
}
