package com.hq.heroes.employee.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class EmployeeServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;  // 가상 레포지토리

    @InjectMocks
    EmployeeServiceImpl employeeService;  // 실제 서비스 로직, Mock 레포지토리를 주입

    Employee employee;

    @BeforeEach
    void setUp() {
        // 테스트용 Employee 객체 초기화
        employee = new Employee();
        employee.setEmployeeId("20140021");
        employee.setEmployeeName("홍길동");
        employee.setEmail("hong@example.com");
        employee.setDetailedAddress("101호");
        employee.setLotAddress("서울 강북구 우이동 73-123");
        employee.setRoadAddress("서울 강북구 삼양로139가길 3");

        when(employeeRepository.findById("20140021")).thenReturn(Optional.of(employee));
    }

    @Test
    @DisplayName("특정 사원 조회 TEST")
    void 특정사원조회() {

        // when (메서드 호출)
        Employee result = employeeService.getEmployeeById("20140021");

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo("20140021");
        assertThat(result.getEmployeeName()).isEqualTo("홍길동");

    }

//    employee.setRoadAddress(employeeDTO.getRoadAddress());
//    employee.setLotAddress(employeeDTO.getLotAddress());
//    employee.setDetailedAddress(employeeDTO.getDetailedAddress());

    @Test
    @DisplayName("사원 -> 사원 정보 수정")
    void 사원정보수정() throws Exception {
        //given
        Employee employee = employeeService.getEmployeeById("20140021");
        employee.setDetailedAddress("202호");
        employee.setRoadAddress("서울 강북구 삼양로202가길 2");
        employee.setLotAddress("서울 강북구 우이동 22-202");

        //when
        employeeRepository.save(employee);

        Employee updateEmployee = employeeService.getEmployeeById("20140021");

        //then
        assertThat(updateEmployee).isNotNull();
        assertThat(updateEmployee.getDetailedAddress()).isEqualTo("202호");
        assertThat(updateEmployee.getRoadAddress()).isEqualTo("서울 강북구 삼양로202가길 2");
        assertThat(updateEmployee.getLotAddress()).isEqualTo("서울 강북구 우이동 22-202");


    }

}