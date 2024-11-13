package com.hq.heroes.education.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponseDTO {
    private long educationId;
    private String educationName;
    private String instructorName;
    private String institution;
    private LocalDate educationStart;
    private LocalDate educationEnd;
    private String categoryName;
    private Long categoryId;
    private String educationCurriculum;
    private int currentParticipant;
    private int participants;
}