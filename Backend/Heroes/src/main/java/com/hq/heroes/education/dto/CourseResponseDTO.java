package com.hq.heroes.education.dto;

import com.hq.heroes.education.entity.enums.CourseStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {
    private long courseId;
    private String educationName;
    private String employeeId;
    private String employeeName;
    private String instructorName;
    private String institution;
    private LocalDate startDate;
    private LocalDate endDate;
    private String categoryName;
    private CourseStatus courseStatus;
    private String educationCurriculum;
}
