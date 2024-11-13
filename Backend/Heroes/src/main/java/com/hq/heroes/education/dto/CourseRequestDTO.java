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
    private Long educationId;
    private String employeeId;
    private Long categoryId;
    private String educationName;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String categoryName;
    private CourseStatus status = CourseStatus.FAIL;
}
