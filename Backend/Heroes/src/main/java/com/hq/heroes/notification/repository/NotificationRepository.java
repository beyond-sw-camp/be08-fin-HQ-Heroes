package com.hq.heroes.notification.repository;

import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver_EmployeeId(String employeeId);
    List<Notification> findBySender_EmployeeId(String employeeId);

    int countByReceiver_EmployeeIdAndStatus(String employeeId, NotificationStatus status);
}
