package com.hq.heroes.vacation.dto;

import com.hq.heroes.vacation.entity.enums.VacationStatus;
import com.hq.heroes.vacation.entity.enums.VacationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationDTO {

    private Long vacationId;
    private String employeeId;
    private VacationType vacationType;
    private LocalDateTime vacationStart;
    private LocalDateTime vacationEnd;
    private VacationStatus vacationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
