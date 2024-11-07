package com.hq.heroes.education.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EducationRequestDTO {
    private String educationName;       // 교육 이름
    private String instructorName;      // 강사 이름
    private String institution;         // 교육 기관
    private LocalDate educationStart;   // 교육 시작일
    private LocalDate educationEnd;     // 교육 종료일
    private Long categoryId;            // 교육 카테고리 ID
    private String educationCurriculum; // 교육 커리큘럼
    private int participants;           // 수강 정원
}

