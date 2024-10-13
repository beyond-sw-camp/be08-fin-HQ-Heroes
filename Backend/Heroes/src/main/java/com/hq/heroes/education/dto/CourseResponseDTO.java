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
public class CourseResponseDTO {
    private long courseId;
    private String educationName;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String categoryName;
}
