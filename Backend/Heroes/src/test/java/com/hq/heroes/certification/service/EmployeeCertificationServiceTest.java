package com.hq.heroes.certification.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.certification.dto.EmployeeCertificationRequestDTO;
import com.hq.heroes.certification.dto.EmployeeCertificationResponseDTO;
import com.hq.heroes.certification.entity.Certification;
import com.hq.heroes.certification.entity.EmployeeCertification;
import com.hq.heroes.certification.repository.CertificationRepository;
import com.hq.heroes.certification.repository.EmployeeCertificationRepository;
import com.hq.heroes.employee.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCertificationServiceTest {

    @InjectMocks
    private EmployeeCertificationServiceImpl employeeCertificationService;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private EmployeeCertificationRepository employeeCertificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeCertificationRequestDTO certificationRequestDTO;

    EmployeeCertification employeeCertification;
    Employee employee;

    @BeforeEach
    void setUp() {
        // 사원 초기화
        employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .build();

        // 사원 자격증 초기화
        employeeCertification = EmployeeCertification.builder()
                .registrationId(1L)
                .certificationName("자격증 A")
                .institution("발급 기관 A")
                .acquisitionDate(LocalDate.of(2024, 10, 1))
                .employee(employee)
                .build();
    }

    @Test
    @DisplayName("사원ID로 자격증 조회하기 테스트 - 성공")
    void getMyCertificationByEmployeeId_Found() {
        // Mocking: 사원 ID에 해당하는 자격증 리스트 설정
        when(employeeCertificationRepository.findByEmployee_EmployeeId("emp001")).thenReturn(List.of(employeeCertification));

        // when
        List<EmployeeCertification> result = employeeCertificationService.getMyCertificationByEmployeeId("emp001");

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("자격증 A", result.get(0).getCertificationName());

        // verify
        verify(employeeCertificationRepository).findByEmployee_EmployeeId("emp001");
    }

    @Test
    @DisplayName("사원ID로 자격증 조회하기 테스트 - 실패")
    void getMyCertificationByEmployeeId_NotFound() {
        // Mocking: 사원 ID에 해당하는 자격증이 없는 경우
        when(employeeCertificationRepository.findByEmployee_EmployeeId("2024100001")).thenReturn(Collections.emptyList());

        // when
        List<EmployeeCertification> result = employeeCertificationService.getMyCertificationByEmployeeId("2024100001");

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // verify
        verify(employeeCertificationRepository).findByEmployee_EmployeeId("2024100001");
    }

    @Test
    @DisplayName("사원 자격증 등록 테스트 - 성공")
    void createEmployeeCertification_Success() {
        // Arrange
        String employeeId = "2024100001";
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);

        EmployeeCertificationRequestDTO certificationRequestDTO = new EmployeeCertificationRequestDTO();
        certificationRequestDTO.setCertificationName("Sample Certification");
        certificationRequestDTO.setInstitution("Sample Institution");
        certificationRequestDTO.setAcquisitionDate(LocalDate.now());
        certificationRequestDTO.setEmployeeId(employeeId);

        // Mocking: 사원 ID에 해당하는 사원이 있을 경우
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Mocking: save 메서드가 호출될 때, 새로운 EmployeeCertification 객체를 반환하도록 설정
        EmployeeCertification savedCertification = new EmployeeCertification();
        savedCertification.setRegistrationId(1L);
        savedCertification.setCertificationName(certificationRequestDTO.getCertificationName());
        savedCertification.setInstitution(certificationRequestDTO.getInstitution());
        savedCertification.setAcquisitionDate(certificationRequestDTO.getAcquisitionDate());
        savedCertification.setEmployee(employee);

        when(employeeCertificationRepository.save(any(EmployeeCertification.class))).thenReturn(savedCertification);

        // Act
        EmployeeCertificationResponseDTO responseDTO = employeeCertificationService.createEmployeeCertification(certificationRequestDTO).toECResponseDTO();

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Sample Certification", responseDTO.getCertificationName());
        assertEquals("Sample Institution", responseDTO.getInstitution());

        // verify
        verify(employeeRepository).findById(employeeId);
        verify(employeeCertificationRepository).save(any(EmployeeCertification.class));
    }

    @Test
    @DisplayName("사원 자격증 등록 테스트 - 실패(사원 없음)")
    void createEmployeeCertification_EmployeeNotFound() {
        // Arrange
        String employeeId = "2024100004"; // 존재하지 않는 사원 ID로 변경합니다.

        EmployeeCertificationRequestDTO certificationRequestDTO = new EmployeeCertificationRequestDTO();
        certificationRequestDTO.setCertificationName("Sample Certification");
        certificationRequestDTO.setInstitution("Sample Institution");
        certificationRequestDTO.setAcquisitionDate(LocalDate.now());
        certificationRequestDTO.setEmployeeId(employeeId);

        // Mocking: 사원이 존재하지 않는 경우
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            employeeCertificationService.createEmployeeCertification(certificationRequestDTO);
        });

        // verify
        verify(employeeRepository).findById(employeeId);
    }


}