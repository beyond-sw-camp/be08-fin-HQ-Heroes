package com.hq.heroes.retire.service;

import com.hq.heroes.retire.dto.RetireResponseDTO;

public interface RetireService {
    RetireResponseDTO getRetirementPayByEmployeeId(String employeeId);
    RetireResponseDTO updateRetirementPay(String employeeId, RetireResponseDTO retireResponseDTO);
    boolean deleteRetirementPay(String employeeId);
}