package com.hq.heroes.education.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponseDTO {
    private long educationId;         // 교육 번호
    private String educationName;     // 교육 이름
    private String instructorName;    // 강사 이름
    private String institution;       // 교육 기관
    private LocalDate educationStart;  // 교육 시작일
    private LocalDate educationEnd;    // 교육 종료일
    private String categoryName; // 카테고리 이름 추가
    private Long categoryId; // 카테고리 ID 추가
    private String educationCurriculum; // 교육 커리큘럼
    private int currentParticipant; // 신청인원
    private int participants; // 수강정원
}