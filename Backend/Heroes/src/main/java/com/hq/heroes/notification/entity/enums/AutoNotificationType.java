package com.hq.heroes.notification.entity.enums;

import lombok.Getter;

@Getter
public enum AutoNotificationType {
    VACATION_APPLICATION("근태", "휴가 승인 요청이 있습니다."),           // 휴가 승인 요청 -> 결재자
    VACATION_APPROVAL("근태", "휴가가 승인되었습니다."),                 // 휴가 승인 알림 발송 -> 신청자
    VACATION_REJECTION("근태", "휴가가 반려되었습니다."),               // 휴가 반려 알림 발송 -> 신청자
    VACATION_CANCEL_REQUEST("근태", "휴가 취소 요청이 있습니다."),       // 휴가 취소 요청 -> 결재자
    VACATION_CANCELLATION_APPROVAL("근태", "휴가 취소가 승인되었습니다."), // 휴가 취소 승인 알림 발송 -> 신청자
    VACATION_CANCELLATION_REJECTION("근태", "휴가 취소가 반려되었습니다."), // 휴가 취소 반려 알림 발송 -> 신청자

    MONTHLY_SALARY_PAYMENT("급여", "월 급여가 지급되었습니다."),         // 월 급여 지급 -> 전사

    EDUCATION_ENROLL("교육", "새로운 교육이 등록되었습니다."),            // 교육 등록 알림 발송 -> 전사원
    EDUCATION_APPLICATION("교육", "교육이 신청되었습니다."),             // 교육 신청 알림 발송 -> 신청자
    EDUCATION_CANCEL("교육", "교육 신청이 취소되었습니다."),              // 교육 취소 알림 발송 -> 취소자
    EDUCATION_COMPLETION("교육", "교육을 이수하였습니다."),              // 교육 이수 알림 발송 -> 이수자(신청자)

    COMPANY_CERTIFICATION_REGISTRATION("교육", "회사 추천 자격증이 등록되었습니다."), // 회사 자격증 등록 알림 -> 부서
    EMPLOYEE_CERTIFICATION_REGISTRATION("교육", "~~사원의 ~~자격증이 등록 요청이 있습니다."), // 사원 자격증 등록 알림 -> 인사팀
    EMPLOYEE_CERTIFICATION_APPROVAL("교육", "~~자격증이 등록되었습니다."), // 사원 자격증 승인 알림 -> 등록자

    EVALUATION_RESULT("평가", "~년 ~반기 평가 결과 생성");           // 평가 결과 알림 -> 피평가자

    private final String category;
    private final String message;

    AutoNotificationType(String category, String message) {
        this.category = category;
        this.message = message;
    }
}
