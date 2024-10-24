package com.hq.heroes.employee.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)  // Mockito 사용
class EmployeeServiceTest {

    @Mock
    EmployeeRepository employeeRepository;  // 가상 레포지토리

    @Mock
    TeamRepository teamRepository;

    @Mock
    DepartmentRepository departmentRepository;

    @Mock
    PositionRepository positionRepository;

    @Mock
    JobRepository jobRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

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

        Position position1 = Position.builder()
                .positionId(1L)
                .positionName("팀장")
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
                .password("1234")
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

    }

    @Test
    @DisplayName("특정 사원 조회 TEST")
    void 특정사원조회() {

        when(employeeRepository.findById("20140021")).thenReturn(Optional.of(employee));

        // when (메서드 호출)
        Employee result = employeeService.getEmployeeById("20140021");

        // then (검증)
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo("20140021");
        assertThat(result.getEmployeeName()).isEqualTo("홍길동");

    }

    @Test
    @DisplayName("사원 -> 사원 정보 수정")
    void 사원정보수정_성공() throws Exception {
        //given
        when(employeeRepository.findById("20140021")).thenReturn(Optional.of(employee));

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
    @DisplayName("사원 정보 수정 실패 - 사원을 찾을 수 없는 경우")
    void 사원정보수정_실패() {
        // given
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("999999999999999");

        when(employeeRepository.findById("999999999999999")).thenReturn(Optional.empty());

        // when & then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            employeeService.updateEmployee(employeeDTO);
        });

        // 예외 메시지 검증
        assertThat(exception.getMessage()).isEqualTo("해당 사원을 찾을 수 없습니다.");
    }



    @Test
    @DisplayName("관리자 -> 사원 정보 수정")
    void 관리자_사원정보수정_성공() throws Exception {
        //given
        when(employeeRepository.findById("20140021")).thenReturn(Optional.of(employee));

        Job job2 = Job.builder().jobId(2L).jobName("개발자").build();
        jobRepository.save(job2);

        Position position2 = Position.builder().positionId(2L).positionName("팀원").build();
        positionRepository.save(position2);

        Team team3 = Team.builder().teamId(3L).teamName("개발1팀").build();
        teamRepository.save(team3);

        Department department2 = Department.builder().deptId(2L).deptName("개발부").build();
        departmentRepository.save(department2);

        // 직원 정보 수정
        Employee employee = employeeService.getEmployeeById("20140021");
        employee.setDetailedAddress("202호");
        employee.setRoadAddress("서울 강북구 삼양로202가길 2");
        employee.setLotAddress("서울 강북구 우이동 22-202");
        employee.setEmployeeName("이몽룡");
        employee.setPhoneNumber("010-7890-4567");
        employee.setEmail("karina_4567@gmail.com");
        employee.setProfileImageUrl("profileImageUrl4567");
        employee.setJob(job2);
        employee.setTeam(team3);
        employee.setPosition(position2);
        employee.setDepartment(department2);

        //when
        employeeRepository.save(employee);

        //then
        Employee updateEmployee = employeeService.getEmployeeById("20140021");
        assertThat(updateEmployee).isNotNull();
        assertThat(updateEmployee.getDetailedAddress()).isEqualTo("202호");
        assertThat(updateEmployee.getRoadAddress()).isEqualTo("서울 강북구 삼양로202가길 2");
        assertThat(updateEmployee.getLotAddress()).isEqualTo("서울 강북구 우이동 22-202");
        assertThat(updateEmployee.getEmployeeName()).isEqualTo("이몽룡");
        assertThat(updateEmployee.getEmail()).isEqualTo("karina_4567@gmail.com");
        assertThat(updateEmployee.getPhoneNumber()).isEqualTo("010-7890-4567");
        assertThat(updateEmployee.getProfileImageUrl()).isEqualTo("profileImageUrl4567");
        assertThat(updateEmployee.getJob()).isEqualTo(job2);
        assertThat(updateEmployee.getTeam()).isEqualTo(team3);
        assertThat(updateEmployee.getPosition()).isEqualTo(position2);
        assertThat(updateEmployee.getDepartment()).isEqualTo(department2);
    }

    @Test
    @DisplayName("사원 정보 수정 실패 - 사원이 존재하지 않는 경우")
    void 관리자_사원정보수정실패_사원없음() {
        // given
        when(employeeRepository.findById("9999999999")).thenReturn(Optional.empty());

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("9999999999");

        // when & then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            employeeService.updateEmployee(employeeDTO);
        });

        // 예외 메시지 검증
        assertThat(exception.getMessage()).isEqualTo("해당 사원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("임시비밀번호 발급")
    void 임시비밀번호발급() throws Exception {
        // given
        String email = "karina_2000@gmail.com";
        String tempPassword = "4567";

        // Mocking employeeRepository의 findByEmail 메서드
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        // Mocking bCryptPasswordEncoder의 encode 메서드
        when(bCryptPasswordEncoder.encode(tempPassword)).thenReturn("encodedTempPassword");

        // Mocking employeeRepository의 save 메서드
        when(employeeRepository.save(employee)).thenReturn(employee);

        // when (임시 비밀번호 발급 메서드 호출)
        String result = employeeService.setTempPassword(email, tempPassword);

        // then
        assertThat(result).isEqualTo("홍길동님의 임시 비밀번호가 발급되었습니다.\n가입하신 이메일을 확인해주세요");

        // employee의 비밀번호가 업데이트 되었는지 검증
        verify(bCryptPasswordEncoder).encode(tempPassword); // 비밀번호 인코딩 검증
        verify(employeeRepository).save(employee); // save 호출 검증

        // employee의 비밀번호 확인
        assertThat(employee.getPassword()).isEqualTo("encodedTempPassword"); // 비밀번호가 인코딩된 값인지 확인
    }

    @Test
    @DisplayName("사원 비밀번호 업데이트 성공")
    void 사원_비밀번호업데이트_성공() {
        // given
        String employeeId = "20140021";
        String currentPassword = "1234";  // 실제 암호화되지 않은 현재 비밀번호
        String newPassword = "newPassword";

        // Mocking employeeRepository의 findByEmployeeId 메서드
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));

        // Mocking bCryptPasswordEncoder의 matches 메서드 - 현재 비밀번호가 일치하는 경우
        when(bCryptPasswordEncoder.matches(currentPassword, employee.getPassword())).thenReturn(true);

        // Mocking bCryptPasswordEncoder의 encode 메서드
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when (메서드 호출)
        boolean result = employeeService.updatePassword(employeeId, currentPassword, newPassword);

        // then (검증)
        assertThat(result).isTrue();  // 비밀번호 업데이트 성공했는지 확인
        assertThat(employee.getPassword()).isEqualTo("encodedNewPassword");  // 비밀번호가 새롭게 인코딩되었는지 확인

        // Mocking 저장 메서드 호출 여부 검증
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("사원 비밀번호 업데이트 실패 - 현재 비밀번호 불일치")
    void 사원_비밀번호업데이트_실패() {
        // given
        String employeeId = "20140021";
        String currentPassword = "wrongPassword";  // 틀린 현재 비밀번호
        String newPassword = "newPassword";

        // Mocking employeeRepository의 findByEmployeeId 메서드
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));

        // Mocking bCryptPasswordEncoder의 matches 메서드 - 현재 비밀번호가 일치하지 않는 경우
        when(bCryptPasswordEncoder.matches(currentPassword, employee.getPassword())).thenReturn(false);

        // when (메서드 호출)
        boolean result = employeeService.updatePassword(employeeId, currentPassword, newPassword);

        // then (검증)
        assertThat(result).isFalse();  // 비밀번호 업데이트가 실패했는지 확인

        // 저장 메서드 호출이 없는지 확인
        verify(employeeRepository, org.mockito.Mockito.never()).save(employee);
    }


}