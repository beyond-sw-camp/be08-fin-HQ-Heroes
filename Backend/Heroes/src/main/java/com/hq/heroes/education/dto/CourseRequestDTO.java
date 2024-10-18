package com.hq.heroes.education.dto;

import com.hq.heroes.education.entity.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {
    private Long educationId; // 교육 번호
    private String employeeId; // 사원 번호
    private Long categoryId; // 카테고리 번호
    private String educationName; // 교육 이름
    private String employeeName; // 사원 이름
    private LocalDate startDate; // 교육 시작일
    private LocalDate endDate; // 교육 종료일
    private String categoryName; // 카테고리 이름

    // 기본 상태값을 'FAIL (미이수)'로 설정
    private CourseStatus status = CourseStatus.FAIL;
}
