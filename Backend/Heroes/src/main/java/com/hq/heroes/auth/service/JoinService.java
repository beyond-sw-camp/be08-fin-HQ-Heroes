package com.hq.heroes.auth.service;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.DepartmentRepository;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final EmployeeRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final JobRepository jobRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String join(JoinDTO joinDTO) throws Exception {

        System.out.println("=================JoinService.join============");

        String email = joinDTO.getEmail();

        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            if (!userRepository.findByEmail(email).get().getStatus().equals(Status.INACTIVE)) {
                throw new Exception("존재하는 회원입니다.");
            } else {
                // 비활성화된 회원일 경우 삭제 후 재가입
                userRepository.deleteById(userRepository.findByEmail(email).get().getEmployeeId());
            }
        }

        // Employee 객체 생성 및 필드 매핑
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        employee.setEmployeeName(joinDTO.getEmployeeName());
        employee.setStatus(joinDTO.getStatus() != null ? joinDTO.getStatus() : Status.ACTIVE);  // 상태 기본값 설정
        employee.setRole(joinDTO.getRole() != null ? joinDTO.getRole() : Role.ROLE_USER);  // 역할 기본값 설정
        employee.setBirthDate(joinDTO.getBirthDate());  // 생년월일
        employee.setJoinDate(joinDTO.getJoinDate());    // 입사일
        employee.setAnnualLeave(joinDTO.getAnnualLeave() != null ? joinDTO.getAnnualLeave() : 0L);  // 연차 기본값 설정
        employee.setPhoneNumber(joinDTO.getPhoneNumber());  // 연락처
        employee.setRoadAddress(joinDTO.getRoadAddress());  // 도로명 주소
        employee.setLotAddress(joinDTO.getLotAddress());    // 지번 주소
        employee.setDetailedAddress(joinDTO.getDetailedAddress());  // 상세 주소

        // 팀, 포지션, 직업 정보 설정
        Optional<Department> department = departmentRepository.findById(joinDTO.getDeptId());
        Optional<Team> team = teamRepository.findById(joinDTO.getTeamId());
        Optional<Position> position = positionRepository.findById(joinDTO.getPositionId());
        Optional<Job> job = jobRepository.findById(joinDTO.getJobId());

        Department departmentEntity = department.get();
        Team teamEntity = team.get();
        Position positionEntity = position.get();
        Job jobEntity = job.get();

        employee.setDepartment(departmentEntity);   // Department ID
        employee.setTeam(teamEntity);  // Team ID
        employee.setPosition(positionEntity);  // Position ID
        employee.setJob(jobEntity);  // Job ID

        // Employee 저장 및 employeeId 생성
        Employee savedEmployee = userRepository.save(employee);  // 저장 후 employeeId 생성됨

        // 프로필 이미지 파일 처리
        if (joinDTO.getProfileImage() != null && !joinDTO.getProfileImage().isEmpty()) {
            System.out.println("============== 프로필 이미지 존재함 ================= ");

            // 파일 저장 경로 설정
            String uploadDir = "C://Users//Playdata//Documents//be08-fin-HQ-Heroes//Backend//Heroes//src//main//resources//static//files";
            String fileName = savedEmployee.getEmployeeId() + "_profile.png";   // 저장된 employeeId로 파일명 생성
            String filePath = uploadDir + fileName;

            System.out.println("============== filePath ================= " + filePath);

            try {
                // 이미지 파일을 지정한 경로에 저장
                joinDTO.getProfileImage().transferTo(new File(filePath)); // 파일 저장
                savedEmployee.setProfileImageUrl(filePath);  // 파일 경로를 데이터베이스에 저장
                userRepository.save(savedEmployee);  // 파일 경로 업데이트 후 다시 저장
            } catch (Exception e) {
                System.out.println("이미지 파일 저장 중 오류 발생: " + e.getMessage());
                throw new Exception("이미지 파일 저장 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.out.println("============== 프로필 이미지가 존재하지 않음 ================= ");
        }

        return savedEmployee.getEmployeeName() + "님 환영합니다!";
    }

}
