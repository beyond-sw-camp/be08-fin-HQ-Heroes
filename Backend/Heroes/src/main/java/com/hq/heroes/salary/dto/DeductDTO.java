package com.hq.heroes.salary.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductDTO {
    private Long deductId;
    private String deductName;
    private Double deductionRate;
    private String description;
}
