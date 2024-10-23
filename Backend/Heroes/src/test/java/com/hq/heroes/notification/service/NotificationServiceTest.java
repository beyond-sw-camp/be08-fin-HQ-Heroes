package com.hq.heroes.notification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.notification.dto.NotificationReqDTO;
import com.hq.heroes.notification.entity.Notification;
import com.hq.heroes.notification.entity.NotificationCategory;
import com.hq.heroes.notification.entity.enums.NotificationStatus;
import com.hq.heroes.notification.repository.NotificationCategoryRepository;
import com.hq.heroes.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationCategoryRepository notificationCategoryRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    Notification notification;
    Notification notification2;
    Notification notification3;
    Notification notification4;
    Employee hong;
    Employee kim;

    @BeforeEach
    void setUp() {
        // 발신자 (Sender)
        hong = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .phoneNumber("01012345678")
                .roadAddress("서울 동작구 보라매로 87")
                .lotAddress("서울 동작구 신대방동 344-4")
                .detailedAddress("5층")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();

        // 수신자 (Receiver)
        kim = Employee.builder()
                .employeeId("2024100002")
                .employeeName("김철수")
                .email("silverwater@naver.com")
                .password("1234")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 22))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1992, 11, 5))
                .phoneNumber("01098765432")
                .roadAddress("경기 고양시 일산서구 가좌로 2")
                .lotAddress("경기 고양시 일산서구 가좌동 158-1")
                .detailedAddress("101호")
                .profileImageUrl("http://example.com/profile2.jpg")
                .build();

        // 알림 카테고리
        NotificationCategory notificationCategory = NotificationCategory.builder()
                .notificationCategoryId(1L)
                .notificationCategoryName("일반")
                .build();

        // 알림 (Notification)
        notification = Notification.builder()
                .notificationId(1L)
                .sender(hong)
                .receiver(kim)
                .category(notificationCategory)
                .message("알림 테스트 입니다. 홍->김1")
                .status(NotificationStatus.UNREAD)
                .recieveDelete(false)
                .sendDelete(false)
                .build();

        // 알림 (Notification)
        notification2 = Notification.builder()
                .notificationId(2L)
                .sender(hong)
                .receiver(kim)
                .category(notificationCategory)
                .message("알림 테스트 입니다. 홍->김2")
                .status(NotificationStatus.UNREAD)
                .recieveDelete(false)
                .sendDelete(false)
                .build();

        // 알림 (Notification)
        notification3 = Notification.builder()
                .notificationId(3L)
                .sender(kim)
                .receiver(hong)
                .category(notificationCategory)
                .message("알림 테스트 입니다. 김->홍1")
                .status(NotificationStatus.UNREAD)
                .recieveDelete(false)
                .sendDelete(false)
                .build();

        // 알림 (Notification)
        notification4 = Notification.builder()
                .notificationId(2L)
                .sender(kim)
                .receiver(hong)
                .category(notificationCategory)
                .message("알림 테스트 입니다. 김->홍2")
                .status(NotificationStatus.UNREAD)
                .recieveDelete(false)
                .sendDelete(false)
                .build();
    }

    @Test
    @DisplayName("알림 상세 조회 테스트")
    void getNotificationById() {
        // when
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        Notification result = notificationService.getNotificationById(1L);

        // then
        assertNotNull(result);
        assertEquals(notification, result);
        System.out.println("result.getSender() = " + result.getSender().getEmployeeName());
        System.out.println("result.getReceiver() = " + result.getReceiver().getEmployeeName());
        System.out.println("result.getMessage() = " + result.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 알림 조회 시 실패 테스트")
    void getNotificationById_NotFound() {
        //given
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());  // 알림이 존재하지 않음

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.getNotificationById(999L);  // 존재하지 않는 ID로 조회 시도
        }, "해당 알림이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("알림 등록 테스트")
    void createNotification() {
        //given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        Notification newNotification = Notification.builder()
                .notificationId(1L)  // 새로운 알림 ID
                .sender(hong)
                .receiver(kim)
                .message("등록 알림 테스트 메시지")
                .status(NotificationStatus.UNREAD)
                .recieveDelete(false)
                .sendDelete(false)
                .build();

        // 스터빙: save 호출 시 동일한 객체를 반환하도록 설정
        when(notificationRepository.save(newNotification)).thenReturn(newNotification);

        // 스터빙: findById(1L) 호출 시 Optional로 newNotification을 반환
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(newNotification));

        //when
        notificationRepository.save(newNotification);
        Optional<Notification> result = notificationRepository.findById(1L);

        //then
        assertTrue(result.isPresent());  // 결과가 있는지 확인
        assertEquals(newNotification, result.get());  // 저장된 알림이 일치하는지 확인

        // 추가적으로 값을 확인하는 부분
        System.out.println("result.getSender() = " + result.get().getSender().getEmployeeName());
        System.out.println("result.getReceiver() = " + result.get().getReceiver().getEmployeeName());
        System.out.println("result.getMessage() = " + result.get().getMessage());
    }


    @Test
    @DisplayName("알림 수정 테스트")
    void updateNotification() {
        //given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        notification.setMessage("알림 수정 테스트 메시지");
        when(notificationRepository.save(notification)).thenReturn(notification);

        //when
        notificationRepository.save(notification);
        Optional<Notification> result = notificationRepository.findById(1L);

        //then
        assertTrue(result.isPresent());
        assertEquals(notification, result.get());

        System.out.println("result.get(). = " + result.get().getNotificationId());
        System.out.println("result.get().getMessage() = " + result.get().getMessage());
    }

    @Test
    @DisplayName("알림 수정 시 존재하지 않는 알림에 대한 예외 처리 테스트")
    void updateNotification_NotFound() {
        //given
        NotificationReqDTO requestDTO = NotificationReqDTO.builder()
                .message("수정 테스트 메시지")
                .status(NotificationStatus.READ)
                .build();

        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());  // 존재하지 않는 알림 ID

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.updateNotification(999L, requestDTO);  // 존재하지 않는 알림 수정 시도
        }, "존재하지 않는 알림 ID : 999");
    }


    @Test
    @DisplayName("발신자나 수신자가 아닌 사용자가 알림을 수정하려고 할 때 실패 테스트")
    void updateNotificationByInvalidUser() {
        //given
        Employee invalidUser = Employee.builder()
                .employeeId("9999")  // 발신자나 수신자가 아닌 사용자
                .employeeName("잘못된 사용자")
                .build();

        NotificationReqDTO requestDTO = NotificationReqDTO.builder()
                .message("수정된 메시지")
                .status(NotificationStatus.READ)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));  // 알림 1L 존재

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.updateNotification(1L, requestDTO);  // 알림 수정 시도
        }, "해당 알림과 관련된 사용자가 아닙니다.");
    }


    @Test
    @DisplayName("수신된 알림 목록 조회 테스트")
    void getNotificationsByReceiverId() {
        //given
        List<Notification> hongNotifications = new ArrayList<>();
        hongNotifications.add(notification);
        hongNotifications.add(notification2);

        List<Notification> kimNotifications = new ArrayList<>();
        kimNotifications.add(notification3);
        kimNotifications.add(notification4);

        // 스터빙: findAllByReceiverId 호출 시 해당 알림 목록을 반환하도록 설정
        when(notificationRepository.findByReceiver_EmployeeId("2024100001")).thenReturn(hongNotifications);
        when(notificationRepository.findByReceiver_EmployeeId("2024100002")).thenReturn(kimNotifications);

        //when
        List<Notification> resultHongReceivedNotifications = notificationService.getNotificationsByReceiverId("2024100001");
        List<Notification> resultKimReceivedNotifications = notificationService.getNotificationsByReceiverId("2024100002");

        //then
        assertEquals(hongNotifications, resultHongReceivedNotifications);  // 홍길동이 받은 알림이 일치하는지 확인
        assertEquals(kimNotifications, resultKimReceivedNotifications);  // 김철수가 받은 알림이 일치하는지 확인

        System.out.println("홍길동이 받은 알림: " + resultHongReceivedNotifications.get(0).getMessage());
        System.out.println("홍길동이 받은 알림: " + resultHongReceivedNotifications.get(1).getMessage());
        System.out.println("김철수가 받은 알림: " + resultKimReceivedNotifications.get(0).getMessage());
        System.out.println("김철수가 받은 알림: " + resultKimReceivedNotifications.get(1).getMessage());
    }


    @Test
    @DisplayName("발신된 알림 목록 조회 테스트")
    void getNotificationsBySenderId() {
        //given
        List<Notification> hongNotifications = new ArrayList<>();
        hongNotifications.add(notification);
        hongNotifications.add(notification2);

        List<Notification> kimNotifications = new ArrayList<>();
        kimNotifications.add(notification3);
        kimNotifications.add(notification4);

        // 스터빙: findAllByReceiverId 호출 시 해당 알림 목록을 반환하도록 설정
        when(notificationRepository.findBySender_EmployeeId("2024100001")).thenReturn(hongNotifications);
        when(notificationRepository.findBySender_EmployeeId("2024100002")).thenReturn(kimNotifications);

        //when
        List<Notification> resultHongSendNotifications = notificationService.getNotificationsBySenderId("2024100001");
        List<Notification> resultKimSendNotifications = notificationService.getNotificationsBySenderId("2024100002");

        //then
        assertEquals(hongNotifications, resultHongSendNotifications);  // 홍길동이 받은 알림이 일치하는지 확인
        assertEquals(kimNotifications, resultKimSendNotifications);  // 김철수가 받은 알림이 일치하는지 확인

        System.out.println("홍길동이 보낸 알림: " + resultHongSendNotifications.get(0).getMessage());
        System.out.println("홍길동이 보낸 알림: " + resultHongSendNotifications.get(1).getMessage());
        System.out.println("김철수가 보낸 알림: " + resultKimSendNotifications.get(0).getMessage());
        System.out.println("김철수가 보낸 알림: " + resultKimSendNotifications.get(1).getMessage());
    }

    @Test
    @DisplayName("알림 읽음 처리 테스트")
    void markAsRead() {
        //given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));    // 홍->김 알림
        //when
        // 수신자가 김철수 일 때
        if (notification.getReceiver().getEmployeeId().equals(kim.getEmployeeId())) {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }
        Optional<Notification> resultNotification = notificationRepository.findById(1L);

        //then
        assertTrue(resultNotification.isPresent());
        assertEquals(notification, resultNotification.get());

        System.out.println("notification.getStatus() = " + notification.getStatus());
        System.out.println("resultNotification.get().getStatus() = " + resultNotification.get().getStatus());
    }

    @Test
    @DisplayName("발신자가 알림을 삭제할 때 처리 테스트")
    void deleteNotificationBySender() {
        //given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification)); // 알림 1L 설정

        //when
        boolean result = notificationService.deleteNotification(1L, hong.getEmployeeId());  // 발신자인 홍길동이 알림 삭제 시도

        //then
        assertTrue(result);
        assertTrue(notification.isSendDelete()); // 발신자가 삭제되었는지 확인
        assertFalse(notification.isRecieveDelete()); // 수신자 삭제 상태는 변경되지 않음
        System.out.println("발신자 삭제 상태: " + notification.isSendDelete());
    }

    @Test
    @DisplayName("수신자가 알림을 삭제할 때 처리 테스트")
    void deleteNotificationByReceiver() {
        //given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification)); // 알림 1L 설정

        //when
        boolean result = notificationService.deleteNotification(1L, kim.getEmployeeId());  // 수신자인 김철수가 알림 삭제 시도

        //then
        assertTrue(result);
        assertTrue(notification.isRecieveDelete()); // 수신자가 삭제되었는지 확인
        assertFalse(notification.isSendDelete()); // 발신자 삭제 상태는 변경되지 않음
        System.out.println("수신자 삭제 상태: " + notification.isRecieveDelete());
    }

    @Test
    @DisplayName("발신자와 수신자가 모두 삭제한 경우 알림 완전 삭제 처리 테스트")
    void deleteNotificationCompletely() {
        //given
        // 발신자와 수신자 모두 삭제된 상태를 만들어줍니다.
        notification.setSendDelete(true);  // 이미 발신자가 삭제한 상태로 설정
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification)); // 알림 1L 설정

        //when
        boolean result = notificationService.deleteNotification(1L, kim.getEmployeeId());  // 수신자인 김철수가 알림 삭제 시도

        //then
        assertTrue(result);
        assertTrue(notification.isSendDelete());  // 발신자 삭제 상태 확인
        assertTrue(notification.isRecieveDelete());  // 수신자 삭제 상태 확인
        System.out.println("알림이 완전히 삭제되었는지 확인");
    }

    @Test
    @DisplayName("잘못된 사용자가 알림을 삭제하려고 할 때 예외 처리 테스트")
    void deleteNotificationByInvalidUser() {
        //given
        Employee invalidUser = Employee.builder()
                .employeeId("9999")  // 홍길동이나 김철수가 아닌 사용자
                .employeeName("잘못된 사용자")
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification)); // 알림 1L 설정

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.deleteNotification(1L, invalidUser.getEmployeeId());
        }, "해당 알림과 관련된 사용자가 아닙니다.");
    }

    @Test
    @DisplayName("알림 삭제 시 알림이 존재하지 않는 경우 예외 처리 테스트")
    void deleteNotification_NotFound() {
        //given
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());  // 존재하지 않는 알림 ID

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.deleteNotification(999L, hong.getEmployeeId());  // 존재하지 않는 ID로 삭제 시도
        }, "해당 알림이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("알림 삭제 시 발신자 또는 수신자가 아닌 사용자가 삭제 시도할 때 실패 테스트")
    void deleteNotificationByInvalidUser_Failure() {
        //given
        Employee invalidUser = Employee.builder()
                .employeeId("9999")  // 홍길동이나 김철수가 아닌 사용자
                .employeeName("잘못된 사용자")
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));  // 알림 1L 존재

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationService.deleteNotification(notification.getNotificationId(), invalidUser.getEmployeeId());  // 잘못된 사용자가 삭제 시도
        }, "해당 알림과 관련된 사용자가 아닙니다.");
    }

}