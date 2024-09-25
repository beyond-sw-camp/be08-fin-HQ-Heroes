package com.hq.heroes.auth.entity;

import com.hq.heroes.auth.entity.enums.Role;
import com.hq.heroes.auth.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import java.util.Random;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_employee")
public class Employee {

//    사원 번호 (로그인시 필요)
    @Id
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
    private Date joinDate;

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
    private Date createAt;

//    수정일자
    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;

    @PrePersist
    public void prePersist() {
        if (this.employeeId == null) {  // employeeCode -> employeeId
            this.employeeId = generateShortRandomCode();
        }
    }

    private String generateShortRandomCode() {
        String prefix = "EMP";
        String randomLetters = getRandomLetters(3);
        String randomNumbers = getRandomNumbers(3);

        return prefix + "-" + randomLetters + randomNumbers;
    }

    // 랜덤 알파벳 생성
    private String getRandomLetters(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
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
