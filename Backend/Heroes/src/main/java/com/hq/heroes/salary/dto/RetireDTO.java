package com.hq.heroes.salary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RetireDTO {
    private String employeeId;
    private LocalDateTime checkInDate;
    private Double postTaxTotal;

    public RetireDTO(String employeeId, LocalDateTime checkInDate, Double postTaxTotal) {
        this.employeeId = employeeId;
        this.checkInDate = checkInDate;
        this.postTaxTotal = postTaxTotal;
    }
}
