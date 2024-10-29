package com.hq.heroes.salary.dto;

import com.hq.heroes.salary.entity.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SalaryHistoryDTO {
    private Long salaryId;
    private String employeeId;
    private LocalDateTime salaryMonth; // 지급 일자
    private Double preTaxTotal; // 세전 총액
    private Double postTaxTotal; // 세후 총액
    private Double bonus; // 성과급
    private long workTime; // 근무 시간

    // 공제 항목
    private Double nationalPension; // 국민연금
    private Double healthInsurance; // 건강보험
    private Double longTermCare; // 장기요양
    private Double employmentInsurance; // 고용보험
    private Double incomeTax; // 소득세
    private Double localIncomeTax; // 지방소득세
}
