package com.hq.heroes.salary.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class DeductDTO {
    private Long deductId;
    private String deductName;
    private Double deductionRate;
    private String description;
}
