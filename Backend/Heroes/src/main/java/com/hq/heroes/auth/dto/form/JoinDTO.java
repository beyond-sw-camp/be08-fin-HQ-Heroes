package com.hq.heroes.auth.dto.form;

import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinDTO {

    // 사원 이름
    private String employeeName;

    // 이메일
    private String email;

    // 비밀번호
    private String password;

    // 역할 (회원가입 시 ROLE_USER로 기본 설정)
    private Role role = Role.ROLE_USER;
    
    // 입사일
    private LocalDate joinDate;

    // 생년 월일
    private LocalDate birthDate;

    // 연차 일수 (기본값 설정 가능)
    private Long annualLeave;

    // 사원 상태 (회원가입 시 기본값 ACTIVE)
    private Status status = Status.ACTIVE;

    // 연락처
    private String phoneNumber;

    // 도로명 주소
    private String roadAddress;

    // 지 번
    private String lotAddress;

    // 상세 주소
    private String detailedAddress;

    // 증명 사진 URL (옵션)
    private String profileImageUrl;

    // Department Id;
    private Long deptId;

    // Team ID (필수)
    private Long teamId;

    // Position ID (필수)
    private Long positionId;

    // Job ID (필수)
    private Long jobId;

    private MultipartFile profileImage;  // 프로필 이미지 필드 추가
}

