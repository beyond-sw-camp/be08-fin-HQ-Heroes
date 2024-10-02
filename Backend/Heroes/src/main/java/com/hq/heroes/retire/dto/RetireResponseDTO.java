package com.hq.heroes.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetireResponseDTO {
    private double payrollAvg;  // 월 평균 급여
    private double annualBonus; // 연간 보너스
    private int period;         // 근무 기간 (년수)


    public Double getAveragePayroll() {
        return payrollAvg;
    }

    public void setPayrollAvg(Double payrollAvg) {
        this.payrollAvg = payrollAvg;
    }
}
