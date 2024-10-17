package com.hq.heroes.notification.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-service")
@Tag(name = "Notification APIs", description = "알림 관련 API 목록")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @Operation(summary = "알림 목록 조회", description = "전체 알림 목록을 조회한다.")
    public ResponseEntity<List<NotificationResDTO>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationResDTO> notificationDTOs = notifications.stream()
                .map(Notification::toResDTO) // Using toResDTO() to convert Notification to ResDTO
                .collect(Collectors.toList());

        return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
    }

    @GetMapping("/notification/{notification-id}")
    @Operation(summary = "알림 상세 조회", description = "알림 ID로 해당 알림 정보를 조회한다.")
    public ResponseEntity<NotificationResDTO> getNotificationById(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);

        if (notification != null) {
            return new ResponseEntity<>(notification.toResDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/notification")
    @Operation(summary = "알림 등록", description = "알림 정보를 받아 등록한다.")
    public ResponseEntity<NotificationResDTO> createNotification(@RequestBody NotificationReqDTO requestDTO) {
        Notification notification = notificationService.createNotification(requestDTO);
        return new ResponseEntity<>(notification.toResDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/notification/{notification-id}")
    @Operation(summary = "알림 수정", description = "알림 정보를 받아 수정한다.")
    public ResponseEntity<NotificationResDTO> updateNotification(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId,
            @RequestBody NotificationReqDTO requestDTO) {
        Notification updatedNotification = notificationService.updateNotification(notificationId, requestDTO);

        if (updatedNotification != null) {
            return new ResponseEntity<>(updatedNotification.toResDTO(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/notifications/receiver/{employeeId}")
    @Operation(summary = "수신된 알림 목록 조회", description = "수신자의 ID로 알림 목록을 조회한다.")
    public ResponseEntity<List<NotificationResDTO>> getNotificationsByReceiverId(
            @Parameter(description = "수신자의 사원 ID", example = "1") @PathVariable("employeeId") String employeeId) {
        List<Notification> notifications = notificationService.getNotificationsByReceiverId(employeeId);
        List<NotificationResDTO> notificationDTOs = notifications.stream()
                .map(Notification::toResDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(notificationDTOs, HttpStatus.OK);
    }

    @GetMapping("/unread-count/{employeeId}")
    public ResponseEntity<Integer> getUnreadNotificationCount(@PathVariable String employeeId) {
        int unreadCount = notificationService.getUnreadNotificationCount(employeeId);
        return ResponseEntity.ok(unreadCount);
    }


    // 알림 상태 업데이트 API
    @PutMapping("/notification/{notification-id}/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽은 후 상태를 'READ'로 업데이트한다.")
    public ResponseEntity<Void> markNotificationAsRead(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId,
            @Parameter(description = "수신자의 사원 ID", example = "employeeId") @RequestParam String employeeId) {

        boolean updated = notificationService.markAsRead(notificationId, employeeId);

        if (updated) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 알림 상태 업데이트 API
    @PutMapping("/notification/{notification-id}/delete")
    @Operation(summary = "알림 삭제 처리", description = "알림의 삭제 상태를 true로 업데이트한다.")
    public ResponseEntity<Void> setNotificationDeleteStatus(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId) {

        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();
            } else {
                System.out.println("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            System.out.println("No authenticated user found.");
        }

        boolean updated = notificationService.deleteNotification(notificationId, employeeId);

        if (updated) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
