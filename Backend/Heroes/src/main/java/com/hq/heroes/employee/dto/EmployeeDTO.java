package com.hq.heroes.employee.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private String employeeId;
    private String employeeName;
    private Long teamId;
    private String teamName;
    private Long deptId;
    private String deptName;
    private String email;
    private Long jobId;
    private String jobName;
    private Long positionId;
    private String positionName;
    private LocalDate joinDate;

    private LocalDate birthDate;
    private String phoneNumber;
    private String roadAddress;
    private String lotAddress;
    private String detailedAddress;
    // 파일 자체를 업로드하는 필드
    private MultipartFile profileImage; // 업로드된 이미지 파일

    // 프로필 이미지 URL (이 값을 데이터베이스에 저장)
    private String profileImageUrl;

    public EmployeeDTO(String employeeId, String employeeName, Long teamId, String teamName,
                       Long deptId, String deptName, String email, Long jobId, String jobName,
                       Long positionId, String positionName, LocalDate joinDate, LocalDate birthDate,
                       String phoneNumber, String roadAddress, String lotAddress, String detailedAddress,
                       String profileImageUrl) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.deptId = deptId;
        this.deptName = deptName;
        this.email = email;
        this.jobId = jobId;
        this.jobName = jobName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.joinDate = joinDate;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.roadAddress = roadAddress;
        this.lotAddress = lotAddress;
        this.detailedAddress = detailedAddress;
        this.profileImageUrl = profileImageUrl;
    }

}

