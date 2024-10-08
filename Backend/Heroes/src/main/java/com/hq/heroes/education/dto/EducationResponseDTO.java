package com.hq.heroes.education.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponseDTO {
    private Long educationId;
    private int participants;
    private int totalParticipants;
    private String instructorName;
    private String educationName;
    private String category;
    private String institution;
    private LocalDateTime educationStart;
    private LocalDateTime educationEnd;
}