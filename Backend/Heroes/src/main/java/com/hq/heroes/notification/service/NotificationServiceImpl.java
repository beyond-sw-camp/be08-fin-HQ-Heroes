package com.hq.heroes.notification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.certification.dto.CertificationResponseDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.education.dto.CourseResponseDTO;
import com.hq.heroes.education.dto.EducationResponseDTO;
import com.hq.heroes.education.entity.Course;
import com.hq.heroes.education.entity.Education;
import com.hq.heroes.evaluation.entity.Evaluation;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.dto.NotificationResDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.entity.enums.AutoNotificationType;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
import com.hq.heroes.notification.repository.NotificationCategoryRepository;
import com.hq.heroes.notification.repository.NotificationRepository;
import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.entity.SalaryHistory;
import com.hq.heroes.vacation.entity.Vacation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationCategoryRepository notificationCategoryRepository;

    // 추가: DTO로 변환하는 메서드
    public NotificationResDTO convertToNotificationDTO(Notification notification) {
        return NotificationResDTO.builder()
                .notificationId(notification.getNotificationId())
                .senderName(notification.getSender().getEmployeeName())
                .receiverName(notification.getReceiver().getEmployeeName())
                .categoryName(notification.getCategory().getNotificationCategoryName())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .receiveDelete(notification.isRecieveDelete())
                .sendDelete(notification.isSendDelete())
                .build();
    }

    @Override
    public List<NotificationResDTO> getAllNotifications() {
        // Notification 엔티티 리스트를 불러온 후, 각 엔티티를 NotificationResDTO로 변환하여 반환
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResDTO getNotificationById(Long notificationId) {
        // 특정 Notification 엔티티를 조회한 후 DTO로 변환하여 반환
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 ID : " + notificationId));
        return convertToNotificationDTO(notification);
    }

    @Override
    @Async
    public void createNotificationAsync(NotificationReqDTO requestDTO) {
        if (requestDTO.getSenderId() == null || requestDTO.getReceiverId() == null || requestDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }
        createNotification(requestDTO);
    }

    @Override
    @Transactional
    public void createNotification(NotificationReqDTO requestDTO) {
        Employee sender = employeeRepository.findById(requestDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 발신자 ID입니다: " + requestDTO.getSenderId()));
        Employee receiver = employeeRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 수신자 ID입니다: " + requestDTO.getReceiverId()));
        NotificationCategory category = notificationCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다: " + requestDTO.getCategoryId()));

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .category(category)
                .message(requestDTO.getMessage())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : NotificationStatus.UNREAD)  // 상태값 설정
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public NotificationResDTO updateNotification(Long notificationId, NotificationReqDTO requestDTO) {
        // 알림을 수정한 후, 변경된 알림을 DTO로 변환하여 반환
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 ID : " + notificationId));

        notification.setMessage(requestDTO.getMessage());
        notification.setStatus(requestDTO.getStatus());

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToNotificationDTO(updatedNotification);
    }

    @Override
    public List<NotificationResDTO> getNotificationsByReceiverId(String employeeId) {
        // 특정 수신자 ID에 대한 Notification 리스트를 조회한 후 DTO로 변환하여 반환
        List<Notification> notifications = notificationRepository.findByReceiver_EmployeeId(employeeId);
        return notifications.stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResDTO> getNotificationsBySenderId(String employeeId) {
        // 특정 발신자 ID에 대한 Notification 리스트를 조회한 후 DTO로 변환하여 반환
        List<Notification> notifications = notificationRepository.findBySender_EmployeeId(employeeId);
        return notifications.stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadNotificationCount(String employeeId) {
        int count = notificationRepository.countByReceiver_EmployeeIdAndStatus(employeeId, NotificationStatus.UNREAD);
        if (count == 0) {
            return 0;
        }
        return count;
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
    @Async
    public void deleteNotificationAsync(Long notificationId, String employeeId) {
        if (notificationId == null || employeeId == null) {
            throw new IllegalArgumentException("필수 정보가 누락되었습니다.");
        }
        deleteNotification(notificationId, employeeId);
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
            notification.setStatus(NotificationStatus.READ);
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

    // 자동 알림 발송을 비동기로 처리
    @Async("notificationExecutor")
    @Override
    public void sendNotificationAsync(String receiverId, AutoNotificationType notificationType, Object data) {
        Map<String, Object> params = new HashMap<>();
        params.put("receiverId", receiverId);
        sendAutomaticNotification(notificationType, params, data);
    }

    @Override
    @Transactional
    public Notification sendAutomaticNotification(AutoNotificationType notificationType, Map<String, Object> params, Object data) {
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
        if ("근태".equals(category) && data instanceof Vacation vacation) {
            String startDate = vacation.getVacationStartDate().toString();
            String endDate = vacation.getVacationEndDate().toString();
            String comment = vacation.getComment();

            message = switch (notificationType) {
                case VACATION_APPLICATION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 승인 요청</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "        <p><strong>사유:</strong> " + comment + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case VACATION_APPROVAL -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 승인</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "        <p><strong>사유:</strong> " + comment + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case VACATION_REJECTION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 반려</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "        <p><strong>사유:</strong> " + comment + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case VACATION_CANCEL_REQUEST -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 취소 신청이 있습니다.</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "        <p><strong>사유:</strong> " + comment + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case VACATION_CANCELLATION_APPROVAL -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 취소 신청이 승인 되었습니다.</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case VACATION_CANCELLATION_REJECTION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>휴가</strong>] " + vacation.getApplicant().getEmployeeName() + "님의 휴가 취소 신청이 반려되었습니다.</p>\n" +
                        "        <p><strong>시작일:</strong> " + startDate + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + endDate + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 근태 알림 타입입니다: " + notificationType);
            };
        } else if ("급여".equals(category) && data instanceof SalaryHistoryDTO salaryHistoryDTO) {
            // 급여 카테고리의 알림
            Optional<Employee> employee = employeeRepository.findByEmployeeId(salaryHistoryDTO.getEmployeeId());
            int month = salaryHistoryDTO.getSalaryMonth().getMonthValue();
            String employeeName = employee.get().getEmployeeName();
            Double salary = salaryHistoryDTO.getPostTaxTotal();
            int salaryInt = (int) Math.floor(salary); // 소수점 이하 버림

            // 한국 원화 형식으로 변환
            NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);
            String formattedSalary = currencyFormat.format(salaryInt) + "원";

            message = switch (notificationType) {
                case MONTHLY_SALARY_PAYMENT -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>급여</strong>] " + month + "월 급여가 지급되었습니다.</p>\n" +
                        "        <p><strong>사원 이름:</strong> " + employeeName + "</p>\n" +
                        "        <p><strong>총 급여:</strong> " + formattedSalary + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 급여 알림 타입입니다: " + notificationType);
            };
        } else if ("교육".equals(category) && data instanceof EducationResponseDTO educationResponseDTO) {
            // 교육 - 교육 카테고리 알림
            String educationCategory = educationResponseDTO.getCategoryName();  // 교육 카테고리
            String institution = educationResponseDTO.getInstitution();    // 교육 기관
            String educationName = educationResponseDTO.getEducationName();  // 교육 명

            message = switch (notificationType) {
                case EDUCATION_ENROLL -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>" + educationCategory + "</strong>] 새로운 교육이 등록되었습니다.</p>\n" +
                        "        <p><strong>교육기관:</strong> " + institution + "</p>\n" +
                        "        <p><strong>교육명:</strong> " + educationName + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 교육 알림 타입입니다: " + notificationType);
            };
        } else if ("교육".equals(category) && data instanceof CourseResponseDTO courseResponseDTO) {
            // 교육 - 교육 카테고리 알림
            message = switch (notificationType) {
                case EDUCATION_APPLICATION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>교육</strong>] 교육이 신청되었습니다.</p>\n" +
                        "        <p><strong>신청한 교육명:</strong> " + courseResponseDTO.getEducationName() + "</p>\n" +
                        "        <p><strong>시작일:</strong> " + courseResponseDTO.getStartDate().toString() + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + courseResponseDTO.getEndDate().toString() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case EDUCATION_CANCEL -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>교육</strong>] 교육 신청이 취소되었습니다.</p>\n" +
                        "        <p><strong>취소한 교육명:</strong> " + courseResponseDTO.getEducationName() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case EDUCATION_COMPLETION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>교육</strong>] 교육이 이수 처리되었습니다.</p>\n" +
                        "        <p><strong>이수한 교육명:</strong> " + courseResponseDTO.getEducationName() + "</p>\n" +
                        "        <p><strong>시작일:</strong> " + courseResponseDTO.getStartDate().toString() + "</p>\n" +
                        "        <p><strong>종료일:</strong> " + courseResponseDTO.getEndDate().toString() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 교육 알림 타입입니다: " + notificationType);
            };
        } else if ("교육".equals(category) && data instanceof CertificationResponseDTO certificationResponseDTO) {
            // 교육 - 회사 자격증 카테고리 알림
            String deptName = certificationResponseDTO.getDeptName();

            message = switch (notificationType) {
                case COMPANY_CERTIFICATION_REGISTRATION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>자격증</strong>] 추천 자격증이 등록되었습니다.</p>\n" +
                        "        <p><strong>부서명:</strong> " + deptName + "</p>\n" +
                        "        <p><strong>자격증명:</strong> " + certificationResponseDTO.getCertificationName() + "</p>\n" +
                        "        <p><strong>발급기관:</strong> " + certificationResponseDTO.getInstitution() + "</p>\n" +
                        "        <p><strong>혜택:</strong> " + certificationResponseDTO.getBenefit() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 교육 알림 타입입니다: " + notificationType);
            };
        } else if ("교육".equals(category) && data instanceof EmployeeCertificationResponseDTO employeeCertificationResponseDTO) {
            // 교육 - 사원 자격증 카테고리 알림
            String employeeName = employeeCertificationResponseDTO.getEmployeeName();

            message = switch (notificationType) {
                case EMPLOYEE_CERTIFICATION_REGISTRATION -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>자격증</strong>] " + employeeName + "님의 자격증 등록을 요청하였습니다.</p>\n" +
                        "        <p><strong>자격증명:</strong> " + employeeCertificationResponseDTO.getCertificationName() + "</p>\n" +
                        "        <p><strong>발급기관:</strong> " + employeeCertificationResponseDTO.getInstitution() + "</p>\n" +
                        "        <p><strong>취득일:</strong> " + employeeCertificationResponseDTO.getAcquisitionDate().toString() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                case EMPLOYEE_CERTIFICATION_APPROVAL -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>자격증</strong>] " + employeeName + "님의 자격증이 등록 되었습니다.</p>\n" +
                        "        <p><strong>자격증명:</strong> " + employeeCertificationResponseDTO.getCertificationName() + "</p>\n" +
                        "        <p><strong>발급기관:</strong> " + employeeCertificationResponseDTO.getInstitution() + "</p>\n" +
                        "        <p><strong>취득일:</strong> " + employeeCertificationResponseDTO.getAcquisitionDate().toString() + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 교육 알림 타입입니다: " + notificationType);
            };
        } else if ("평가".equals(category) && data instanceof Evaluation evaluation) {
            // DateTimeFormatter를 사용해 날짜 형식을 yy-MM-dd로 설정
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
            String formattedDate = evaluation.getUpdatedAt().format(formatter);

            message = switch (notificationType) {
                case EVALUATION_RESULT -> "<html>\n" +
                        "    <body>\n" +
                        "        <p>[<strong>평가</strong>] " + evaluation.getEmployee().getEmployeeName() + "님의 평가 결과 입니다.</p>\n" +
                        "        <p><strong>평가 점수:</strong> " + evaluation.getScore() + "</p>\n" +
                        "        <p><strong>평가일:</strong> " + formattedDate + "</p>\n" +
                        "    </body>\n" +
                        "</html>\n";
                default -> throw new IllegalArgumentException("알 수 없는 평가 알림 타입입니다: " + notificationType);
            };
        } else {
            message = null;
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

        return notification;
    }


}
