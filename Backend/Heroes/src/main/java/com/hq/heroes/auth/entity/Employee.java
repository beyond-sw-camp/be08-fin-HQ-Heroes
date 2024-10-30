package com.hq.heroes.auth.entity;

import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import com.hq.heroes.employee.dto.EmployeeDTO;
import com.hq.heroes.employee.entity.Department;
import com.hq.heroes.employee.entity.JobRole;
import com.hq.heroes.employee.entity.Position;
import com.hq.heroes.employee.entity.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_employee")
public class Employee {

    //    사원 번호 (로그인시 필요)
    @Id
    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;  // 필드명을 employeeCode에서 employeeId로 변경

    //    사원 이름
    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    //    이메일
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    //    비밀번호
    @Column(name = "password", nullable = false)
    private String password;

    //    역할
    @Enumerated(EnumType.STRING)
    @Column(name = "is_admin", nullable = false)
    private Role role = Role.ROLE_USER;

    //    입사일
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    //    연차 일수
    @Column(name = "annual_leave", nullable = false)
    private Long annualLeave;

    //    사원 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    //    생성일자
    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    //    수정일자
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // 생년월일
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 연락처
    @Column(name = "phone_number")
    private String phoneNumber;

    // 도로명 주소
    @Column(name = "road_address")
    private String roadAddress;

    // 지 번
    @Column(name = "lot_address")
    private String lotAddress;

    // 상세 주소
    @Column(name = "detailed_address")
    private String detailedAddress;

    // 증명 사진 URL
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    // Team과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    // Team과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Position과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    // 과의 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobrole_id", nullable = false)
    private JobRole job;

    @PrePersist
    public void prePersist() {
        if (this.employeeId == null) {  // employeeCode -> employeeId
            this.employeeId = generateShortRandomCode();
        }
    }

    private String generateShortRandomCode() {
        String prefix = LocalDate.now().getYear() + "" + LocalDate.now().getMonthValue();
        String randomNumbers = getRandomNumbers(4);

        return prefix + randomNumbers;
    }

    // 랜덤 숫자 생성
    private String getRandomNumbers(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(number);
        }
        return sb.toString();
    }

}
