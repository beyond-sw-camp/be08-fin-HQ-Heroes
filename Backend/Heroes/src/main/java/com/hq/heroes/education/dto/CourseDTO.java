package com.hq.heroes.education.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long courseId; // 수업 번호 (신청 시 사용)
    private String educationName; // 교육명
    private String instructorName; // 강사명
    private String category; // 카테고리
    private String institution; // 교육기관
    private int participants; // 신청 인원
    private int totalParticipants; // 총인원
}
