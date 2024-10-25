package com.hq.heroes.certification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.jwt.JWTUtil;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.service.EmployeeCertificationService;
import com.hq.heroes.certification.service.EmployeeCertificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeCertificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class EmployeeCertificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeCertificationService employeeCertificationService1;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeCertificationServiceImpl employeeCertificationService;

    @InjectMocks
    private EmployeeCertificationController employeeCertificationController;

    @MockBean
    private JWTUtil jwtUtil;

//    @Test
//    void createEmployeeCertification() throws Exception {
//        // Given
//        Employee employee = new Employee();
//        employee.setEmployeeId(String.valueOf(1L)); // 테스트를 위한 사원 ID
//
//        EmployeeCertificationRequestDTO requestDTO = EmployeeCertificationRequestDTO.builder()
//                .employeeId(String.valueOf(employee.getEmployeeId()))
//                .certificationName("Java Certification")
//                .institution("Java Institute")
//                .acquisitionDate(LocalDate.now())
//                .build();
//
//        EmployeeCertification employeeCertification = EmployeeCertification.builder()
//                .registrationId(1L)
//                .employee(employee) // employee 필드 설정
//                .certificationName(requestDTO.getCertificationName())
//                .institution(requestDTO.getInstitution())
//                .acquisitionDate(requestDTO.getAcquisitionDate())
//                .build();
//
//        // Mocking the service call
//        when(employeeCertificationService.createEmployeeCertification(any(EmployeeCertificationRequestDTO.class)))
//                .thenReturn(employeeCertification.toECResponseDTO()); // EmployeeCertificationResponseDTO를 반환
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/employee-certification/my-certification")
//                .contentType(MediaType.APPLICATION_JSON);
//    }

}