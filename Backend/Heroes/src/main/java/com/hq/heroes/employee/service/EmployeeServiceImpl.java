package com.hq.heroes.employee.service;

import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.common.service.FirebaseStorageService;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final DepartmentRepository departmentRepository;
    private final JobRepository jobRepository;
    private final PositionRepository positionRepository;
    private final TeamRepository teamRepository;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAllEmployeesDTO();
    }

    @Override
    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id).orElse(null);
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

        Department department = departmentRepository.findById(employeeDTO.getDeptId()).orElseThrow();
        Job job = jobRepository.findById(employeeDTO.getJobId()).orElseThrow();
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
}
