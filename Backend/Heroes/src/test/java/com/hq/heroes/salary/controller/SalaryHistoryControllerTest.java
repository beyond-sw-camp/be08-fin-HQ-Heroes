package com.hq.heroes.salary.controller;

import com.hq.heroes.salary.dto.SalaryHistoryDTO;
import com.hq.heroes.salary.service.SalaryHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SalaryHistoryControllerTest {

    @Mock
    private SalaryHistoryService salaryHistoryService;

    @InjectMocks
    private SalaryHistoryController salaryHistoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSalaryHistories() {
        String employeeId = "12345";

        SalaryHistoryDTO salaryHistory1 = SalaryHistoryDTO.builder()
                .preTaxTotal(5000.0)
                .postTaxTotal(4000.0)
                .build();

        SalaryHistoryDTO salaryHistory2 = SalaryHistoryDTO.builder()
                .preTaxTotal(6000.0)
                .postTaxTotal(5000.0)
                .build();

        when(salaryHistoryService.getAllSalaries(employeeId)).thenReturn(Arrays.asList(salaryHistory1, salaryHistory2));

        List<SalaryHistoryDTO> result = salaryHistoryController.getAllSalaryHistories(employeeId);

        assertEquals(2, result.size());
        verify(salaryHistoryService, times(1)).getAllSalaries(employeeId); // Verify service method was called
    }

    @Test
    public void testCreateSalary() {
        SalaryHistoryDTO salaryHistoryDTO = SalaryHistoryDTO.builder()
                .preTaxTotal(5000.0)
                .postTaxTotal(4000.0)
                .build();

        when(salaryHistoryService.createSalary(any(SalaryHistoryDTO.class))).thenReturn(salaryHistoryDTO);

        SalaryHistoryDTO result = salaryHistoryController.createSalary(salaryHistoryDTO);

        assertNotNull(result);
        assertEquals(5000.0, result.getPreTaxTotal());
        verify(salaryHistoryService, times(1)).createSalary(any(SalaryHistoryDTO.class)); // Verify service method was called
    }

    @Test
    public void testGetLastThreeMonthsSalarySum() {
        String employeeId = "12345";
        double expectedSum = 15000.0;

        when(salaryHistoryService.getLastThreeMonthsSalarySum(employeeId)).thenReturn(expectedSum);

        ResponseEntity<Double> responseEntity = salaryHistoryController.getLastThreeMonthsSalarySum(employeeId);

        assertEquals(expectedSum, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue()); // Check response status code
        verify(salaryHistoryService, times(1)).getLastThreeMonthsSalarySum(employeeId); // Verify service method was called
    }
}
