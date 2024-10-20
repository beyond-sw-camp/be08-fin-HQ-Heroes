package com.hq.heroes.notification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
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

        // 상태값이 없으면 기본값으로 'UNREAD' 설정
        NotificationStatus status = requestDTO.getStatus() != null ? requestDTO.getStatus() : NotificationStatus.UNREAD;

        // Create notification entity
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .category(category)
                .message(requestDTO.getMessage())
                .status(status)  // 상태값 설정
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

    public List<Notification> getNotificationsByReceiverId(String employeeId) {
        return notificationRepository.findByReceiver_EmployeeId(employeeId);
    }

    @Override
    public List<Notification> getNotificationsBySenderId(String employeeId) {
        return notificationRepository.findBySender_EmployeeId(employeeId);
    }

    @Override
    public int getUnreadNotificationCount(String employeeId) {
        return notificationRepository.countByReceiver_EmployeeIdAndStatus(employeeId, NotificationStatus.UNREAD);
    }


    @Override
    @Transactional
    public boolean markAsRead(Long notificationId, String employeeId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        // 수신자의 ID가 맞는지 확인
        if (!notification.getReceiver().getEmployeeId().equals(employeeId)) {
            throw new IllegalArgumentException("잘못된 수신자 ID입니다.");
        }

        // 알림 상태를 READ로 변경
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteNotification(Long notificationId, String employeeId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        // 수신자 또는 발신자인지 확인
        boolean isSender = notification.getSender().getEmployeeId().equals(employeeId);
        boolean isReceiver = notification.getReceiver().getEmployeeId().equals(employeeId);

        if (isSender) {
            // 발신자인 경우 sendDelete를 true로 설정
            notification.setSendDelete(true);
        } else if (isReceiver) {
            // 수신자인 경우 receiveDelete를 true로 설정
            notification.setRecieveDelete(true);
        } else {
            throw new IllegalArgumentException("해당 알림과 관련된 사용자가 아닙니다.");
        }

        // 만약 발신자와 수신자가 모두 삭제한 경우 알림을 삭제
        if (notification.isSendDelete() && notification.isRecieveDelete()) {
            notificationRepository.deleteById(notificationId);
        } else {
            notificationRepository.save(notification); // 플래그 업데이트 후 저장
        }

        return true;
    }

}
