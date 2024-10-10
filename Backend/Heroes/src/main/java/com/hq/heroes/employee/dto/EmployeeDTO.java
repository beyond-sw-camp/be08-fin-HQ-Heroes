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
    private String teamName;
    private String deptName;
    private String email;
    private String jobName;
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

    public EmployeeDTO(String employeeId, String employeeName, String teamName, String deptName,
                       String email, String jobName, String positionName, LocalDate joinDate,
                       LocalDate birthDate, String phoneNumber, String roadAddress,
                       String lotAddress, String detailedAddress, String profileImageUrl) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.teamName = teamName;
        this.deptName = deptName;
        this.email = email;
        this.jobName = jobName;
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

