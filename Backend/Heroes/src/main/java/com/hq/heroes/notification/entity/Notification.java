package com.hq.heroes.notification.entity;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Employee sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Employee receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private NotificationCategory category;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    // 기본 상태를 'UNREAD'로 설정
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name="recieve_delete", nullable = false)
    private boolean recieveDelete = false;

    @Column(name="send_delete", nullable = false)
    private boolean sendDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public NotificationReqDTO toReqDTO() {
        return NotificationReqDTO.builder()
                .senderId(this.sender.getEmployeeId())
                .receiverId(this.receiver.getEmployeeId())
                .categoryId(this.category.getNotificationCategoryId())
                .message(this.message)
                .status(this.status) // 추가: 상태를 DTO로 변환할 때도 포함
                .build();
    }
}
