package com.hq.heroes.employee.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.service.FirebaseStorageService;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.JobRole;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRoleRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final DepartmentRepository departmentRepository;
    private final JobRoleRepository jobRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .employeeId(employee.getEmployeeId())
                .employeeName(employee.getEmployeeName())
                .teamId(employee.getTeam() != null ? employee.getTeam().getTeamId() : null)
                .teamName(employee.getTeam() != null ? employee.getTeam().getTeamName() : null)
                .deptId(employee.getTeam() != null && employee.getTeam().getDepartment() != null ? employee.getTeam().getDepartment().getDeptId() : null)
                .deptName(employee.getTeam() != null && employee.getTeam().getDepartment() != null ? employee.getTeam().getDepartment().getDeptName() : null)
                .jobRoleId(employee.getJob() != null ? employee.getJob().getJobRoleId() : null)
                .jobRoleName(employee.getJob() != null ? employee.getJob().getJobRoleName() : null)
                .positionId(employee.getPosition() != null ? employee.getPosition().getPositionId() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .email(employee.getEmail())
                .joinDate(employee.getJoinDate())
                .annualLeave(employee.getAnnualLeave())
                .status(employee.getStatus())
                .birthDate(employee.getBirthDate())
                .phoneNumber(employee.getPhoneNumber())
                .roadAddress(employee.getRoadAddress())
                .lotAddress(employee.getLotAddress())
                .detailedAddress(employee.getDetailedAddress())
                .profileImageUrl(employee.getProfileImageUrl())
                .build();
    }


    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                // 사원 ID가 "0000"이 아닌 경우만 필터링
                .filter(employee -> !employee.getEmployeeId().equals("0000"))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<EmployeeDTO> getAllEmployeesByDepartment(Long deptId) {
        return employeeRepository.findEmployeesByDepartmentId(deptId).stream()
                .filter(employee -> !employee.getEmployeeId().equals("0000"))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(String id) {
        return employeeRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    @Override
    public Employee updateEmployee(EmployeeDTO employeeDTO) {

        Employee employee = employeeRepository.findById(employeeDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사원을 찾을 수 없습니다."));

        employee.setRoadAddress(employeeDTO.getRoadAddress());
        employee.setLotAddress(employeeDTO.getLotAddress());
        employee.setDetailedAddress(employeeDTO.getDetailedAddress());

        if (employeeDTO.getProfileImage() != null && !employeeDTO.getProfileImage().isEmpty()) {
            log.info("프로필 이미지 존재함");

            // Firebase Storage에 업로드할 파일명 (employeeId로 생성)
            String fileName = employeeDTO.getEmployeeId() + "_profile.png";

            try {
                // Firebase Storage에 이미지 파일 업로드
                String imageUrl = firebaseStorageService.uploadFile(employeeDTO.getProfileImage(), fileName);
                employee.setProfileImageUrl(imageUrl);  // 이미지 URL을 데이터베이스에 저장
            } catch (Exception e) {
                log.error("이미지 파일 저장 중 오류 발생: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 파일 저장 중 오류 발생: " + e.getMessage());
            }
        } else {
            log.info("프로필 이미지가 존재하지 않음");
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee adminUpdateEmployee(EmployeeDTO employeeDTO) {

        Employee employee = employeeRepository.findById(employeeDTO.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사원을 찾을 수 없습니다."));

        employee.setEmployeeName(employeeDTO.getEmployeeName());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setEmail(employeeDTO.getEmail());
        employee.setRoadAddress(employeeDTO.getRoadAddress());
        employee.setLotAddress(employeeDTO.getLotAddress());
        employee.setDetailedAddress(employeeDTO.getDetailedAddress());
        employee.setStatus(employeeDTO.getStatus());

        Department department = departmentRepository.findById(employeeDTO.getDeptId()).orElseThrow();
        JobRole job = jobRepository.findById(employeeDTO.getJobRoleId()).orElseThrow();
        Position position = positionRepository.findById(employeeDTO.getPositionId()).orElseThrow();
        Team team = teamRepository.findById(employeeDTO.getTeamId()).orElseThrow();

        employee.setDepartment(department);
        employee.setJob(job);
        employee.setPosition(position);
        employee.setTeam(team);

        if (employeeDTO.getProfileImage() != null && !employeeDTO.getProfileImage().isEmpty()) {
            log.info("프로필 이미지 존재함");

            // Firebase Storage에 업로드할 파일명 (employeeId로 생성)
            String fileName = employeeDTO.getEmployeeId() + "_profile.png";

            try {
                // Firebase Storage에 이미지 파일 업로드
                String imageUrl = firebaseStorageService.uploadFile(employeeDTO.getProfileImage(), fileName);
                employee.setProfileImageUrl(imageUrl);  // 이미지 URL을 데이터베이스에 저장
            } catch (Exception e) {
                log.error("이미지 파일 저장 중 오류 발생: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 파일 저장 중 오류 발생: " + e.getMessage());
            }
        } else {
            log.info("프로필 이미지가 존재하지 않음");
        }
        return employeeRepository.save(employee);
    }

    // 임시 비밀번호 발급
    @Override
    public String setTempPassword(String email, String tempPW) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원"));

        try {
            employee.setPassword(bCryptPasswordEncoder.encode(tempPW));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("상태 값 이상함");
        }

        employee = employeeRepository.save(employee);
        return employee.getEmployeeName() + "님의 임시 비밀번호가 발급되었습니다.\n" + "가입하신 이메일을 확인해주세요";
    }

    // 사원이 직접 비밀번호 변경
    @Override
    public boolean updatePassword(String employeeId, String currentPassword, String newPassword) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 현재 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(currentPassword, employee.getPassword())) {
            return false;  // 현재 비밀번호가 일치하지 않으면 false 반환
        }

        // 새 비밀번호 암호화 후 저장
        employee.setPassword(bCryptPasswordEncoder.encode(newPassword));
        employeeRepository.save(employee);
        return true;
    }
}
