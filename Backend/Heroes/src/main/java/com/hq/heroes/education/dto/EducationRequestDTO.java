package com.hq.heroes.education.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationRequestDTO {
    private String educationName;
    private int participants;
    private int totalParticipants;
    private String instructorName;
    private String category;
    private String institution;
    private LocalDateTime educationStart;
    private LocalDateTime educationEnd;
}
