package com.hq.heroes.notification.entity;

import com.hq.heroes.auth.entity.Employee;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public NotificationResDTO toResDTO() {
        return NotificationResDTO.builder()
                .notificationId(this.notificationId)
                .senderName(this.sender.getEmployeeName())  // Assuming Employee entity has a 'name' field
                .receiverName(this.receiver.getEmployeeName())  // Assuming Employee entity has a 'name' field
                .categoryName(this.category.getNotificationCategoryName())  // Assuming NotificationCategory has this field
                .message(this.message)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }

}
