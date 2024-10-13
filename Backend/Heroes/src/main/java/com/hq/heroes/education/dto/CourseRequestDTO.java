package com.hq.heroes.education.dto;

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
    private Long employeeId;
    private Long categoryId;
    private String educationName;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String categoryName;
}
