package com.hq.heroes.retire.dto;

import lombok.Data;

@Data
public class RetireRequestDTO {
    private String employeeId;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}

