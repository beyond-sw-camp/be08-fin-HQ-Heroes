package com.hq.heroes.notification.entity.enums;

import lombok.Getter;

@Getter
public enum AutoNotificationType {
    VACATION_APPLICATION("근태", "휴가 승인 요청이 있습니다."),
    VACATION_APPROVAL("근태", "휴가가 승인되었습니다."),
    VACATION_REJECTION("근태", "휴가가 반려되었습니다."),
    PAYROLL_GENERATION("급여", "급여가 생성되었습니다."),
    EDUCATION_ENROLL("교육", "새로운 교육이 등록 되었습니다."),
    EDUCATION_APPROVAL("교육", "교육이 접수 되었습니다."),
    CERTIFICATION_APPROVAL("교육", "자격증이 등록되었습니다.");

    private final String category;
    private final String message;

    AutoNotificationType(String category, String message) {
        this.category = category;
        this.message = message;
    }

}
