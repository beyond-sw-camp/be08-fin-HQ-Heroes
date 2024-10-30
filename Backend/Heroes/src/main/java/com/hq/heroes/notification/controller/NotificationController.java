package com.hq.heroes.notification.controller;

import com.hq.heroes.auth.dto.form.CustomEmployeeDetails;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.dto.NotificationsReqDTO;
import com.hq.heroes.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-service")
@Tag(name = "Notification APIs", description = "알림 관련 API 목록")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    @Operation(summary = "알림 목록 조회", description = "전체 알림 목록을 조회한다.")
    public ResponseEntity<List<NotificationResDTO>> getAllNotifications() {
        List<NotificationResDTO> notificationResDTOs = notificationService.getAllNotifications();

        return new ResponseEntity<>(notificationResDTOs, HttpStatus.OK);
    }

    //- 테스트
    @GetMapping("/notification/{notification-id}")
    @Operation(summary = "알림 상세 조회", description = "알림 ID로 해당 알림 정보를 조회한다.")
    public ResponseEntity<NotificationResDTO> getNotificationById(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId) {
        NotificationResDTO notificationResDTO = notificationService.getNotificationById(notificationId);

        if (notificationResDTO != null) {
            return new ResponseEntity<>(notificationResDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/notification")
    @Operation(summary = "알림 등록", description = "알림 정보를 받아 등록한다.")
    public ResponseEntity<String> createNotification(@RequestBody NotificationsReqDTO requestDTO) {
        if (requestDTO.getSenderId() == null || requestDTO.getCategoryId() == null || requestDTO.getMessage() == null) {
            return new ResponseEntity<>("필수 정보가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        for (String receiverId : requestDTO.getReceiverIds()) {
            if (receiverId != null) {  // receiverId가 null이 아닐 때만 실행
                NotificationReqDTO reqDTO = NotificationReqDTO.builder()
                        .senderId(requestDTO.getSenderId())
                        .receiverId(receiverId)
                        .categoryId(requestDTO.getCategoryId())
                        .status(requestDTO.getStatus())
                        .message(requestDTO.getMessage())
                        .build();
                notificationService.createNotificationAsync(reqDTO);
            }
        }

        return new ResponseEntity<>("알림 발송 성공", HttpStatus.CREATED);
    }


    //- 테스트
    @PutMapping("/notification/{notification-id}")
    @Operation(summary = "알림 수정", description = "알림 정보를 받아 수정한다.")
    public ResponseEntity<NotificationResDTO> updateNotification(
            @Parameter(description = "알림 ID", example = "1") @PathVariable("notification-id") Long notificationId,
            @RequestBody NotificationReqDTO requestDTO) {
        NotificationResDTO notificationResDTO = notificationService.updateNotification(notificationId, requestDTO);

        if (notificationResDTO != null) {
            return new ResponseEntity<>(notificationResDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    //- 테스트
    @GetMapping("/notifications/receiver/{employeeId}")
    @Operation(summary = "수신된 알림 목록 조회", description = "수신자의 ID로 알림 목록을 조회한다.")
    public ResponseEntity<List<NotificationResDTO>> getNotificationsByReceiverId(
            @Parameter(description = "수신자의 사원 ID", example = "1") @PathVariable("employeeId") String employeeId) {
        List<NotificationResDTO> notificationResDTOS = notificationService.getNotificationsByReceiverId(employeeId);


        return new ResponseEntity<>(notificationResDTOS, HttpStatus.OK);
    }

    //- 테스트
    @GetMapping("/notifications/sender/{employeeId}")
    @Operation(summary = "발신된 알림 목록 조회", description = "발신자의 ID로 알림 목록을 조회한다.")
    public ResponseEntity<List<NotificationResDTO>> getNotificationsBySenderId(
            @Parameter(description = "발신자의 사원 ID", example = "1") @PathVariable("employeeId") String employeeId) {
        List<NotificationResDTO> notificationResDTOS = notificationService.getNotificationsBySenderId(employeeId);

        return new ResponseEntity<>(notificationResDTOS, HttpStatus.OK);
    }

    @GetMapping("/unread-count/{employeeId}")
    public ResponseEntity<Integer> getUnreadNotificationCount(@PathVariable String employeeId) {
        int unreadCount = notificationService.getUnreadNotificationCount(employeeId);
        return ResponseEntity.ok(unreadCount);
    }


    // 알림 상태 업데이트 API - 테스트
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

    // 알림 삭제 API - 테스트
    @PutMapping("/notification/delete")
    @Operation(summary = "알림 삭제 처리", description = "알림의 삭제 상태를 true로 업데이트한다.")
    public ResponseEntity<Void> setNotificationDeleteStatus(
            @RequestBody Long[] notificationIds) {

        String employeeId = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomEmployeeDetails) {
                CustomEmployeeDetails userDetails = (CustomEmployeeDetails) principal;
                employeeId = userDetails.getUsername();
            } else {
                log.debug("Principal is not an instance of CustomEmployeeDetails.");
            }
        } else {
            log.debug("No authenticated user found.");
        }
        for (Long notificationId : notificationIds) {
            notificationService.deleteNotificationAsync(notificationId, employeeId);
        }
        return null;
    }


}
