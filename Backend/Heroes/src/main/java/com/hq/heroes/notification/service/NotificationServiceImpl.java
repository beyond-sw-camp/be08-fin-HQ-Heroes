package com.hq.heroes.notification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
import com.hq.heroes.notification.repository.NotificationCategoryRepository;
import com.hq.heroes.notification.repository.NotificationRepository;
import com.hq.heroes.vacation.entity.Vacation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    @Transactional
    public void sendAutomaticNotification(AutoNotificationType notificationType, Map<String, Object> params, Object data) {
        // 알림 타입에서 카테고리와 메시지를 가져오기
        String category = notificationType.getCategory();
        String message;

        // 수신자 및 발신자 ID를 파라미터로 받아 사용
        String receiverId = (String) params.get("receiverId");

        // 0000 = 시스템 아이디
        Employee sender = employeeRepository.findById("0000")
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 발신자 ID입니다."));
        Employee receiver = employeeRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 수신자 ID입니다."));

        NotificationCategory notificationCategory = notificationCategoryRepository.findByNotificationCategoryName(category)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + category));

        // 근태 카테고리인 경우에만 휴가 관련 데이터 처리
        if ("근태".equals(category) && data instanceof Vacation) {
            Vacation vacation = (Vacation) data;
            String startDate = vacation.getVacationStartDate().toString();
            String endDate = vacation.getVacationEndDate().toString();

            message = switch (notificationType) {
                case VACATION_APPLICATION ->
                        "<html><body><p>" + vacation.getApplicant().getEmployeeName() + "님의 휴가 승인 요청이 있습니다. (" + startDate + " ~ " + endDate + ")</p></body></html>";
                case VACATION_APPROVAL ->
                        "<html><body><p>" + vacation.getApplicant().getEmployeeName() + "님의 휴가가 승인되었습니다(" + startDate + " ~ " + endDate + ")</p></body></html>";
                case VACATION_REJECTION ->
                        "<html><body><p>" + vacation.getApplicant().getEmployeeName() + "님의 휴가가 반려되었습니다(" + startDate + " ~ " + endDate + ")</p></body></html>";
                case MONTHLY_VACATION_GRANTED ->
                        "<html><body><p>월차가 지급되었습니다.</p></body></html>";
                default -> throw new IllegalArgumentException("알 수 없는 근태 알림 타입입니다: " + notificationType);
            };
        } else {
            // 근태 외 카테고리의 기본 메시지 처리
            message = switch (notificationType) {
                case MONTHLY_SALARY_PAYMENT -> "<html><body><p>월 급여가 지급되었습니다.</p></body></html>";
                case EDUCATION_ENROLL -> "<html><body><p>새로운 교육이 등록되었습니다.</p></body></html>";
                case EDUCATION_APPLICATION -> "<html><body><p>교육이 신청되었습니다.</p></body></html>";
                case EDUCATION_CANCEL -> "<html><body><p>교육 신청이 취소되었습니다.</p></body></html>";
                case EDUCATION_COMPLETION -> "<html><body><p>교육을 이수하였습니다.</p></body></html>";
                case COMPANY_CERTIFICATION_REGISTRATION -> "<html><body><p>회사 추천 자격증이 등록되었습니다.</p></body></html>";
                case EMPLOYEE_CERTIFICATION_REGISTRATION -> "<html><body><p>사원의 자격증이 등록 요청이 있습니다.</p></body></html>";
                case EMPLOYEE_CERTIFICATION_APPROVAL -> "<html><body><p>사원의 자격증이 승인되었습니다.</p></body></html>";
                case EVALUATION_RESULT -> "<html><body><p>평가 결과가 생성되었습니다.</p></body></html>";
                default -> throw new IllegalArgumentException("알 수 없는 알림 타입입니다: " + notificationType);
            };
        }

        // 알림 생성
        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .category(notificationCategory)
                .message(message)
                .status(NotificationStatus.UNREAD)
                .sendDelete(true)  // 자동 알림이므로 sendDelete 설정
                .build();

        notificationRepository.save(notification);
    }



}
