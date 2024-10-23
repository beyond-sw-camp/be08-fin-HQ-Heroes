package com.hq.heroes.employee.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class EmployeeServiceTest {

    @Mock
    EmployeeRepository employeeRepository;  // 가상 레포지토리

    @InjectMocks
    EmployeeServiceImpl employeeService;  // 실제 서비스 로직, Mock 레포지토리를 주입

    Employee employee;  // 클래스 변수로 선언

    @BeforeEach
    void setUp() {
        // 테스트용 Employee 객체 초기화
        Job job1 = Job.builder()
                .jobId(1L)
                .jobName("영업담당자")
                .build();

        Job job2 = Job.builder()
                .jobId(2L)
                .jobName("개발자")
                .build();

        Position position1 = Position.builder()
                .positionId(1L)
                .positionName("팀장")
                .build();

        Position position2 = Position.builder()
                .positionId(1L)
                .positionName("팀원")
                .build();

        Team team1 = Team.builder()
                .teamId(1L)
                .teamName("영업1팀")
                .build();

        Team team2 = Team.builder()
                .teamId(2L)
                .teamName("영업2팀")
                .build();

        List<Team> teams1 = new ArrayList<>();
        teams1.add(team1);
        teams1.add(team2);

        Department department1 = Department.builder()
                .deptId(1L)
                .deptName("영업부")
                .teams(teams1)
                .build();

        // 클래스 변수에 객체 할당
        employee = Employee.builder()
                .employeeId("20140021")
                .employeeName("홍길동")
                .email("karina_2000@gmail.com")
                .detailedAddress("101호")
                .lotAddress("서울 강북구 우이동 73-123")
                .roadAddress("서울 강북구 삼양로139가길 3")
                .phoneNumber("010-7890-1234")
                .profileImageUrl("profileImageUrl111")
                .job(job1)
                .position(position1)
                .team(team1)
                .department(department1)
                .build();

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


    @Test
    @DisplayName("관리자 -> 사원 정보 수정")
    void 관리자_사원정보수정() throws Exception {
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