package com.hq.heroes.auth.service;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.service.FirebaseStorageService;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinServiceTest {

    @Mock
    private EmployeeRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private FirebaseStorageService firebaseStorageService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private JoinService joinService;

    @Test
    @DisplayName("성공적으로 홍길동 회원가입 테스트")
    void join_success() throws Exception {
        //given
        JoinDTO joinDTO = JoinDTO.builder()
                .employeeName("홍길동")
                .email("hong@naver.com")
                .password("password")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .phoneNumber("01012345678")
                .roadAddress("서울 동작구 보라매로 87")
                .lotAddress("서울 동작구 신대방동 344-4")
                .detailedAddress("5층")
                .deptId(1L)
                .teamId(1L)
                .positionId(1L)
                .jobId(1L)
                .profileImage(mock(MultipartFile.class)) // 프로필 이미지 모킹
                .build();

        Employee employee = Employee.builder()
                .employeeId("2024100001")
                .employeeName("홍길동")
                .email("hong@naver.com")
                .role(Role.ROLE_USER)
                .joinDate(LocalDate.of(2024, 10, 23))
                .annualLeave(0L)
                .status(Status.ACTIVE)
                .birthDate(LocalDate.of(1990, 5, 20))
                .phoneNumber("01012345678")
                .roadAddress("서울 동작구 보라매로 87")
                .lotAddress("서울 동작구 신대방동 344-4")
                .detailedAddress("5층")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();

        when(userRepository.existsByEmail("hong@naver.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(new Team()));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(new Position()));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(new Job()));
        when(userRepository.save(any(Employee.class))).thenReturn(employee);
        when(firebaseStorageService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("http://example.com/profile.jpg");

        //when
        String result = joinService.join(joinDTO);

        //then
        assertEquals("홍길동님 환영합니다!", result);
        verify(firebaseStorageService, times(1)).uploadFile(any(MultipartFile.class), anyString());

        System.out.println("result = " + result);
    }

    @Test
    @DisplayName("이메일 중복으로 인해 회원가입 실패 테스트")
    void join_fail_duplicateEmail() throws Exception {
        //given
        JoinDTO joinDTO = JoinDTO.builder()
                .employeeName("홍길동")
                .email("hong@naver.com")
                .password("password")
                .build();

        // 이메일이 중복된 상황 설정
        when(userRepository.existsByEmail("hong@naver.com")).thenReturn(true);

        // 이미 존재하는 사용자의 상태를 ACTIVE로 설정하여 예외 발생
        Employee existingEmployee = Employee.builder().status(Status.ACTIVE).build();
        when(userRepository.findByEmail("hong@naver.com")).thenReturn(Optional.of(existingEmployee));

        //when & then
        Exception exception = assertThrows(Exception.class, () -> joinService.join(joinDTO));
        assertEquals("존재하는 회원입니다.", exception.getMessage());

        System.out.println("exception = " + exception.getMessage());
    }


    @Test
    @DisplayName("부서 정보가 없을 때 회원가입 실패 테스트")
    void join_fail_noDepartment() throws Exception {
        //given
        JoinDTO joinDTO = JoinDTO.builder()
                .employeeName("홍길동")
                .email("hong@naver.com")
                .deptId(999L)  // 존재하지 않는 부서 ID 설정
                .build();

        // 이메일 중복이 아닌 경우 설정
        when(userRepository.existsByEmail("hong@naver.com")).thenReturn(false);

        // 부서가 존재하지 않는 상황 설정
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        //when & then
        Exception exception = assertThrows(Exception.class, () -> joinService.join(joinDTO));
        assertEquals("부서를 찾을 수 없습니다.", exception.getMessage());

        System.out.println("exception = " + exception.getMessage());
    }

}
