package com.hq.heroes.education.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
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
    private LocalDate applicationStartDate; // 신청 시작일 추가
    private LocalDate applicationEndDate; // 신청 종료일 추가
    private Integer participants;
}