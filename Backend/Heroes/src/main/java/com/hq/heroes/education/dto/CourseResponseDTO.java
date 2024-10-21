package com.hq.heroes.education.dto;

import com.hq.heroes.education.entity.enums.CourseStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private long courseId; // 강의 번호
    private String educationName; // 강의 이름
    private String employeeName; // 사원 이름
    private LocalDate startDate; // 강의 시작일
    private LocalDate endDate; // 강의 종료일
    private String categoryName; // 카테고리 이름
    private CourseStatus status; // 이수 여부
}
