package com.hq.heroes.vacation.service;

import com.hq.heroes.vacation.dto.VacationDTO;

import java.util.List;

public interface VacationService {

    void submitVacation(VacationDTO vacationDTO);

    List<VacationDTO> getAllVacations();
}
