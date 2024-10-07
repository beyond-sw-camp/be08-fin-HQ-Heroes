package com.hq.heroes.notification.repository;

import com.hq.heroes.notification.entity.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCategoryRepository extends JpaRepository<NotificationCategory, Long> {

}
