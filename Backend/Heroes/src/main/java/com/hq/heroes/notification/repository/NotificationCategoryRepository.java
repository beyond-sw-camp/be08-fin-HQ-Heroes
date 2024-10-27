package com.hq.heroes.notification.repository;

import com.hq.heroes.notification.entity.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationCategoryRepository extends JpaRepository<NotificationCategory, Long> {
    Optional<NotificationCategory> findByNotificationCategoryName(String name);
}
