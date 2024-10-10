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
public class EducationCurriculumDTO {
    private Long curriculumId; // 커리큘런 번호
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String educationPlace;
    private int participants;
    private Long educationId;
}
