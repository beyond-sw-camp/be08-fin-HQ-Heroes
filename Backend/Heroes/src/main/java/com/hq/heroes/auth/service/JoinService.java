package com.hq.heroes.auth.service;

import com.hq.heroes.auth.dto.form.JoinDTO;
import com.hq.heroes.auth.entity.Employee;
import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.auth.repository.EmployeeRepository;
import com.hq.heroes.employee.entity.Job;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import com.hq.heroes.employee.repository.JobRepository;
import com.hq.heroes.employee.repository.PositionRepository;
import com.hq.heroes.employee.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final EmployeeRepository userRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final JobRepository jobRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String join(JoinDTO joinDTO) throws Exception {
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
        employee.setProfileImageUrl(joinDTO.getProfileImageUrl());  // 프로필 이미지 URL

        // 팀, 포지션, 직업 정보 설정
        Optional<Team> team = teamRepository.findById(joinDTO.getTeamId());
        Optional<Position> position = positionRepository.findById(joinDTO.getPositionId());
        Optional<Job> job = jobRepository.findById(joinDTO.getJobId());

        Team teamEntity = team.get();
        Position positionEntity = position.get();
        Job jobEntity = job.get();

        employee.setTeam(teamEntity);  // Team ID
        employee.setPosition(positionEntity);  // Position ID
        employee.setJob(jobEntity);  // Job ID

        // Employee 저장
        userRepository.save(employee);

        return employee.getEmployeeName() + "님 환영합니다!";
    }
}
