package com.hq.heroes.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_notification_category")
public class NotificationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_category_id")
    private Long notificationCategoryId;

    @Column(name = "category_name")
    private String notificationCategoryName;

}
