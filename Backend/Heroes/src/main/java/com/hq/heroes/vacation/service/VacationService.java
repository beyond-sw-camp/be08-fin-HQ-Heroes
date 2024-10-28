package com.hq.heroes.vacation.service;

import com.hq.heroes.vacation.dto.VacationDTO;
import com.hq.heroes.vacation.entity.Vacation;

import java.util.List;

public interface VacationService {

    Vacation submitVacation(VacationDTO vacationDTO);

    List<VacationDTO> getAllVacations();

    void approveVacation(Long vacationId);

    void rejectVacation(Long vacationId);

    List<VacationDTO> getApprovedVacationsByEmployeeId(String employeeId);

    List<VacationDTO> getTeamVacations(String employeeId);
}
